package connectors.poloniex.subscribtion;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import models.Market;
import models.order.*;
import models.order.OrderBookUpdate.OrderBookUpdateBuilder;
import models.order.OrderBookUpdate.OrderBookUpdateType;
import play.Logger;
import rx.functions.Action1;
import ws.wamp.jawampa.PubSubData;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Piotr on 2017-02-19.
 */
public class OrderBookSubscriber implements Action1<PubSubData> {

    private final OrderBookUpdater orderBookUpdater;
    private final JsonNodeToOrderBookUpdate toOrderBookItem;
    private final Market market;

    private long lastSequence;

    @Inject
    public OrderBookSubscriber(OrderBookUpdater orderBookUpdater, @Assisted Market market) {
        Logger.info("OrderBookSubscriber construction for {}", market);

        this.orderBookUpdater = orderBookUpdater;
        this.toOrderBookItem = new JsonNodeToOrderBookUpdate();
        this.market = market;
    }

    @Override
    public void call(PubSubData data) {
        Logger.trace("Received {} {} for {}",
                data.arguments(), data.keywordArguments(), market);

        long sequence = getMessageSequence(data);
        Map<Boolean, List<JsonNode>> tradesAndOrderBooks = asStream(data.arguments().elements())
                .collect(Collectors.partitioningBy(jsonNode -> jsonNode.get("type").asText().equals("newTrade")));

        try {
            tradesAndOrderBooks.get(Boolean.FALSE)
                    .stream()
                    .map(jsonNode -> toOrderBookItem.apply(jsonNode))
                    .forEachOrdered(orderBookUpdate -> orderBookUpdater.update(orderBookUpdate, sequence));

        } catch (Exception e) {
            Logger.error("orderBookUpdate exception ", e);
            throw e;
        } finally {
            lastSequence = sequence;
        }
    }

    private long getMessageSequence(PubSubData data) {
        return data.keywordArguments().get("seq").asLong();
    }

    private Stream<JsonNode> asStream(Iterator<JsonNode> iterator) {
        Iterable<JsonNode> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

//    [{data: {rate: '0.00300888', type: 'bid', amount: '3.32349029'},type: 'orderBookModify'}]
//    [{data: {rate: '0.00311164', type: 'ask' },type: 'orderBookRemove'}]
//    [{data: {tradeID: '364476',rate: '0.00300888',amount: '0.03580906',date: '2014-10-07 21:51:20',total: '0.00010775',type: 'sell'},type: 'newTrade'}]
    public class JsonNodeToOrderBookUpdate implements Function<JsonNode, OrderBookUpdate> {

        @Override
        public OrderBookUpdate apply(JsonNode node) {
            OrderBookUpdateBuilder builder = OrderBookUpdate.builder();

            OrderBookUpdateType type = OrderBookUpdateType.fromRecievedUpdateType(node.get("type").asText());
            JsonNode data = node.get("data");

            builder.type(type);
            builder.market(market);
            builder.side(OrderSide.fromReceivedSide(data.get("type").asText()));
            builder.rate(new BigDecimal(data.get("rate").asText()));
            if (type.equals(OrderBookUpdateType.ORDER_BOOK_MODIFY)) {
                builder.volume(new BigDecimal(data.get("amount").asText()));
            }

            return builder.build();
        }
    }

}
