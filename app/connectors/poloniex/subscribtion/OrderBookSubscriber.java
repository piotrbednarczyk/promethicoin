package connectors.poloniex.subscribtion;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import models.Market;
import models.order.OrderBookUpdate;
import models.order.OrderBookUpdate.OrderBookUpdateType;
import models.order.OrderSide;
import play.Logger;
import rx.functions.Action1;
import util.IteratorToStream;
import ws.wamp.jawampa.PubSubData;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static models.order.OrderBookUpdate.OrderBookUpdateType.ORDER_BOOK_MODIFY;
import static models.order.OrderBookUpdate.OrderBookUpdateType.ORDER_BOOK_REMOVE;

/**
 * Created by Piotr on 2017-02-19.
 */
public class OrderBookSubscriber implements Action1<PubSubData> {

    private final ActorRef orderBookActor;
    private final Market market;

    @Inject
    public OrderBookSubscriber(@Named("orderBookActor") ActorRef orderBookActor, @Assisted Market market) {
        Logger.info("OrderBookSubscriber construction for {}", market);

        this.orderBookActor = orderBookActor;
        this.market = market;
    }

    @Override
    public void call(PubSubData data) {
        if(Logger.isTraceEnabled()) {
            Logger.trace("Received {} {} for {}", data.arguments(), data.keywordArguments(), market);
        }

        try {
            processData(data);
        } catch (Exception e) {
            Logger.error("Message processing exception ", e);
            throw e;
        }
    }

    private void processData(PubSubData data) {
        long sequence = getMessageSequence(data);
        Map<Boolean, List<JsonNode>> tradesAndOrderBookUpdates = extractUpdatesFromMessage(data);
        sendOrderBookUpdates(tradesAndOrderBookUpdates, sequence);
    }

    private void sendOrderBookUpdates(Map<Boolean, List<JsonNode>> tradesAndOrderBookUpdates, long sequence) {
        tradesAndOrderBookUpdates.get(Boolean.FALSE)
                .stream()
                .map(new JsonNodeToOrderBookUpdate(sequence))
                .forEach(orderBookUpdate -> orderBookActor.tell(orderBookUpdate, null));
    }

    private Map<Boolean, List<JsonNode>> extractUpdatesFromMessage(PubSubData data) {
        return new IteratorToStream<JsonNode>()
                .apply(data.arguments().elements())
                .collect(Collectors.partitioningBy(jsonNode -> jsonNode.get("type").asText().equals("newTrade")));
    }

    private long getMessageSequence(PubSubData data) {
        return data.keywordArguments().get("seq").asLong();
    }

    /**
     *     Order book updates returned from Poloniex in JSON format:
     *     [{data: {rate: '0.00300888', type: 'bid', amount: '3.32349029'},type: 'orderBookModify'}]
     *     [{data: {rate: '0.00311164', type: 'ask' },type: 'orderBookRemove'}]
     */
    public class JsonNodeToOrderBookUpdate implements Function<JsonNode, OrderBookUpdate> {

        private final long sequence;

        public JsonNodeToOrderBookUpdate(long sequence) {
            this.sequence = sequence;
        }

        @Override
        public OrderBookUpdate apply(JsonNode node) {
            OrderBookUpdateType type = getOrderBookUpdateType(node);
            JsonNode data = node.get("data");

            return OrderBookUpdate.builder()
                    .type(type)
                    .market(market)
                    .sequence(sequence)
                    .side(getOrderBookSide(data))
                    .rate(new BigDecimal(data.get("rate").asText()))
                    .volume(getVolume(type, data))
                    .build();
        }

        private BigDecimal getVolume(OrderBookUpdateType type, JsonNode data) {
            return ORDER_BOOK_MODIFY.equals(type) ? new BigDecimal(data.get("amount").asText()) : null;
        }

        private OrderSide getOrderBookSide(JsonNode data) {
           return OrderSide.valueOf(data.get("type").asText().toUpperCase());
        }

        private OrderBookUpdateType getOrderBookUpdateType(JsonNode node) {
            switch (node.get("type").asText()) {
                case "orderBookModify":
                    return ORDER_BOOK_MODIFY;
                case "orderBookRemove":
                    return ORDER_BOOK_REMOVE;
                default:
                    throw new IllegalArgumentException(
                            MessageFormat.format("Can't map provided value {0} to OrderBookUpdateType",
                                    node.get("type").asText()));
            }
        }
    }

    //TRADES
    //[{data: {tradeID: '364476',rate: '0.00300888',amount: '0.03580906',date: '2014-10-07 21:51:20',total: '0.00010775',type: 'sell'},type: 'newTrade'}]
}
