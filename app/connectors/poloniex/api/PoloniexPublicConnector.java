package connectors.poloniex.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import models.Market;
import models.order.OrderBook;
import models.order.OrderBookItem;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import util.IteratorToStream;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by Piotr Bednarczyk on 2017-03-11.
 */
public class PoloniexPublicConnector {

    private final WSClient wsClient;
    private final PoloniexCommands pc;

    @Inject
    public PoloniexPublicConnector(PoloniexCommands pc, WSClient wsClient) {
        this.pc = pc;
        this.wsClient = wsClient;
    }

    public CompletionStage<OrderBook> getOrderBook(Market market, int orderBookDepth) {
        Objects.requireNonNull(market);
        Logger.info("OrderBook request for {}", market);

        return getOrderBookResponse(market, orderBookDepth).thenApply(new ResponseToOrderBook(market));
    }

    private CompletionStage<WSResponse> getOrderBookResponse(Market market, int depth) {
        WSRequest request = wsClient.url(pc.getPublicApiUrl())
                .setQueryParameter(pc.getCommand(), pc.getReturnOrderBook())
                .setQueryParameter(pc.getCurrencyPair(), market.getCurrencyPair().getCodes())
                .setQueryParameter(pc.getOrderBookDepth(), Integer.toString(depth));

        return request.get();
    }

    private static class ResponseToOrderBook implements Function<WSResponse, OrderBook> {

        private final Market market;

        public ResponseToOrderBook(Market market) {
            this.market = market;
        }

        @Override
        public OrderBook apply(WSResponse response) {
            return getOrderBook(response.asJson());
        }

        private OrderBook getOrderBook(JsonNode orderBookNode) {
            Logger.info("OrderBook for {} received {}", market, orderBookNode);

            return OrderBook.builder()
                    .market(market)
                    .asks(getOrderBookItemStream(orderBookNode.get("asks")))
                    .bids(getOrderBookItemStream(orderBookNode.get("bids")))
                    .frozen(isFrozen(orderBookNode))
                    .sequence(getSequence(orderBookNode))
                    .build();
        }

        private long getSequence(JsonNode orderBookNode) {
            return orderBookNode.get("seq").asLong();
        }

        private boolean isFrozen(JsonNode orderBookNode) {
            return orderBookNode.get("isFrozen").asInt() == 1;
        }

        private Stream<OrderBookItem> getOrderBookItemStream(JsonNode orderBookItemList) {
            return new IteratorToStream<JsonNode>()
                    .apply(orderBookItemList.elements())
                    .map(itemNode -> getOrderBookItem(itemNode));
        }

        private OrderBookItem getOrderBookItem(JsonNode orderBookItem) {
            return new OrderBookItem(new BigDecimal(orderBookItem.get(0).asText()),
                    new BigDecimal(orderBookItem.get(1).asText()));
        }
    }
}
