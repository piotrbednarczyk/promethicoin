package models.order;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import models.Market;
import play.Configuration;
import play.Logger;
import play.libs.ws.WSClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Created by Piotr Bednarczyk on 2017-02-26.
 */
@Singleton
public class OrderBookHolder {
    private final Config config;
    private final WSClient wsClient;
    private final Map<Market, OrderBook> orderBooks = new HashMap<>();

    @Inject
    public OrderBookHolder(Configuration configuration, WSClient wsClient) throws ExecutionException, InterruptedException {
        this.config = configuration.underlying();
        this.wsClient = wsClient;
    }

    public void update(OrderBookUpdate orderBookUpdate) {
        Objects.requireNonNull(orderBookUpdate);
        Preconditions.checkArgument(orderBooks.containsKey(orderBookUpdate.getMarket()),
                "No order book found for {}", orderBookUpdate);
        orderBooks.get(orderBookUpdate.getMarket()).update(orderBookUpdate);
    }

    public void initializeOrderBook(OrderBook orderBook) {
        Objects.requireNonNull(orderBook);
        Logger.debug("initializeOrderBook {}", orderBook);
        orderBooks.put(orderBook.getMarket(), orderBook);
    }

    public boolean containsOrderBook(Market market) {
        return orderBooks.containsKey(market);
    }
}
