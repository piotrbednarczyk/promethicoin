package connectors.poloniex.subscribtion;

import models.Market;

/**
 * Created by Piotr Bednarczyk on 2017-03-01.
 */
public interface OrderBookSubscriberFactory {
    OrderBookSubscriber newOrderBookSubscriber(Market market);
}
