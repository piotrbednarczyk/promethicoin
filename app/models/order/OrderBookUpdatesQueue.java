package models.order;

import models.Market;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Piotr Bednarczyk on 2017-03-12.
 */
public class OrderBookUpdatesQueue {

    private final static class SequenceComparator implements Comparator<OrderBookUpdate> {

        @Override
        public int compare(OrderBookUpdate o1, OrderBookUpdate o2) {
            return o1.getSequence() < o2.getSequence() ? -1 : o1.getSequence() == o2.getSequence() ? 0 : 1;
        }
    }

    private final Map<Market, Queue<OrderBookUpdate>> updateQueues = new HashMap<>();

    public void addUpdate(OrderBookUpdate update) {
        Market market = update.getMarket();
        if(! updateQueues.containsKey(market)) {
            initializeQueueForMarket(market);
        }

        updateQueues.get(market).add(update);
    }

    private void initializeQueueForMarket(Market market) {
        updateQueues.put(market, new PriorityQueue<>(new SequenceComparator()));
    }

    public int getQueueSize(Market market) {
        return updateQueues.containsKey(market) ? updateQueues.get(market).size() : 0;
    }

    public Stream<OrderBookUpdate> getUpToDateUpdates(Market market, long currentSequence) {
        return updateQueues.containsKey(market)
                ? getUpdateStream(currentSequence, updateQueues.get(market)) : Stream.empty();
    }

    private Stream<OrderBookUpdate> getUpdateStream(long currentSequence, Queue<OrderBookUpdate> updates) {
        return Stream.generate(updates::poll)
                .limit(updates.size())
                .filter(update -> update.getSequence() > currentSequence);
    }
}
