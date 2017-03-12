package models.order;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import models.Market;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Created by Piotr Bednarczyk on 2017-02-26.
 */
@Data
public class OrderBook {
    @NonNull
    private final Market market;
    private final long sequence;
    private final boolean frozen;
    private final SortedMap<BigDecimal, OrderBookItem> asks = new TreeMap<>();
    private final SortedMap<BigDecimal, OrderBookItem> bids = new TreeMap<>();

    @Builder
    public OrderBook(Market market, Stream<OrderBookItem> asks, Stream<OrderBookItem> bids, long sequence, boolean frozen) {
        Objects.requireNonNull(market);
        Objects.requireNonNull(asks);
        Objects.requireNonNull(bids);

        this.market = market;
        this.frozen = frozen;
        this.sequence = sequence;
        asks.forEach(item -> this.asks.put(item.getRate(), item));
        bids.forEach(item -> this.bids.put(item.getRate(), item));
    }

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

    @Override
    public String toString() {
        return "OrderBook{" +
                "sequence=" + sequence +
                ", market=" + market +
                ", frozen=" + frozen +
                ", asks size=" + asks.size() +
                ", bids size=" + bids.size() +
                '}';
    }
}
