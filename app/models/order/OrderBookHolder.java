package models.order;

import com.google.common.base.Preconditions;
import models.Market;
import org.yaml.snakeyaml.error.Mark;
import play.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Piotr Bednarczyk on 2017-03-12.
 */
public class OrderBookHolder {

    private final Map<Market, OrderBook> orderBooks = new HashMap<>();

    public boolean holdsOrderBook(Market market) {
        return orderBooks.containsKey(market);
    }

    public OrderBook getOrderBook(Market market) {
        Preconditions.checkState(orderBooks.containsKey(market),
                "order book for market {} does not exist!", market);
        return orderBooks.get(market);
    }

    public void updateOrderBook(OrderBookUpdate orderBookUpdate) {
        Preconditions.checkArgument(orderBooks.containsKey(orderBookUpdate.getMarket()),
                "No order book found for {}", orderBookUpdate);

        Logger.debug("updateOrderBook {}", orderBookUpdate);
        orderBooks.get(orderBookUpdate.getMarket()).update(orderBookUpdate);
    }

    public void initializeOrderBook(OrderBook orderBook) {
        Preconditions.checkArgument(orderBook != null, "orderBook is null!");
        Logger.info("initializeOrderBook {}", orderBook);
        orderBooks.put(orderBook.getMarket(), orderBook);
    }
}
