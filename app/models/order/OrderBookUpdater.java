package models.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import play.Configuration;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Piotr Bednarczyk on 2017-03-01.
 */
@Singleton
public class OrderBookUpdater {

    private final OrderBookHolder orderBookHolder;
    private final WSClient wsClient;
    private final Config config;

    @Inject
    public OrderBookUpdater(Configuration configuration, OrderBookHolder orderBookHolder, WSClient wsClient) {
        this.orderBookHolder = orderBookHolder;
        this.wsClient = wsClient;
        this.config = configuration.underlying();
    }

    public void update(OrderBookUpdate update, long sequence) {

        orderBookHolder.update(update);
    }

    private void initialize() throws ExecutionException, InterruptedException {
        //TODO: market would be better then currency pairs
        List<String> currencyPairs = config.getStringList("poloniex.currencyPairs");

        WSRequest request = wsClient.url(config.getString("poloniex.api.public.url"))
                .setQueryParameter(config.getString("poloniex.api.public.command"),
                        config.getString("poloniex.api.public.commands.orderBook.name"))
                .setQueryParameter(config.getString("poloniex.api.public.commands.orderBook.parameters.currencyPair"),
                        currencyPairs.get(0))
                .setQueryParameter(config.getString("poloniex.api.public.commands.orderBook.parameters.depth"),
                        "100");

        Logger.info("Initialize OrderBook request get");
        CompletionStage<WSResponse> responsePromise = request.get();
        responsePromise.thenApply(response -> {
            JsonNode orderBookNode = response.asJson();
            Logger.info("OrderBook seq {}", orderBookNode.get("seq"));
            Logger.info("OrderBook {}", orderBookNode);
            //sequence = response.asJson().get("seq").asLong();

            asStream(orderBookNode.get("asks").elements())
                    .map(askNode ->
                            new OrderBookItem(new BigDecimal(askNode.get(0).asText()), new BigDecimal(askNode.get(1).asText())));
            orderBookNode.get("bids").elements();

            return null;
        });
    }

    private <T> Stream<T> asStream(Iterator<T> iterator) {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
