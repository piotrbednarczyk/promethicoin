package models.order;

import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.NonNull;
import models.Market;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Piotr Bednarczyk on 2017-02-26.
 */
@Data
public class OrderBook {
    @NonNull
    private final Market market;
    private final Map<BigDecimal, OrderBookItem> asks = new HashMap<>();
    private final Map<BigDecimal, OrderBookItem> bids = new HashMap<>();

    public void update(OrderBookUpdate orderBookUpdate) {
        Objects.requireNonNull(orderBookUpdate);
        Preconditions.checkArgument(orderBookUpdate.getMarket().equals(market), "orderBook market does not match!");

        switch (orderBookUpdate.getSide()) {
            case ASK:
                updateOrderBookItemMap(asks, orderBookUpdate);
                break;
            case BID:
                updateOrderBookItemMap(bids, orderBookUpdate);
        }
    }

    private void updateOrderBookItemMap(Map<BigDecimal, OrderBookItem> itemMap, OrderBookUpdate update) {
        switch (update.getType()) {
            case ORDER_BOOK_REMOVE:
                itemMap.remove(update.getRate());
                break;
            case ORDER_BOOK_MODIFY:
                itemMap.put(update.getRate(), new OrderBookItem(update.getRate(), update.getVolume()));
        }
    }
}
