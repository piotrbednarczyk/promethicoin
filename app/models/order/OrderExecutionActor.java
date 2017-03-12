package models.order;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import connectors.poloniex.api.PoloniexTradingConnector;
import models.Market;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piotr Bednarczyk on 2017-02-24.
 */
public class OrderExecutionActor extends UntypedActor {

    private final PoloniexTradingConnector tradingConnector;
    private final ActorRef orderBookActor;

    private final List<MarketOrder> marketOrders = new ArrayList<>();

    @Inject
    public OrderExecutionActor(PoloniexTradingConnector tradingConnector, @Named("orderBookActor") ActorRef orderBookActor) {
        this.tradingConnector = tradingConnector;
        this.orderBookActor = orderBookActor;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof MarketOrder) {
            onMarketOrder((MarketOrder) message);
        } else if(message instanceof OrderBook) {
            onOrderBook((OrderBook) message);
        } else {
            unhandled(message);
        }
    }

    private void onOrderBook(OrderBook orderBook) {
        if(! marketOrders.isEmpty() && marketOrders.get(0).getMarket().equals(orderBook.getMarket())) {
            //TODO
        }
    }

    private void onMarketOrder(MarketOrder order) {
        Logger.info("onMarketOrder {}", order);
        marketOrders.add(order);
        getOrderBookForMarket(order.getMarket());
    }

    private void getOrderBookForMarket(Market market) {
        orderBookActor.tell(new OrderBookActor.OrderBookRequest(market), self());
    }
}
