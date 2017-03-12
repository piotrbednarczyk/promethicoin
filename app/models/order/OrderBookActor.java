package models.order;

import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import com.google.inject.Inject;
import connectors.poloniex.api.PoloniexPublicConnector;
import models.Market;
import play.Logger;

import java.util.concurrent.CompletionStage;

import static akka.pattern.PatternsCS.pipe;

/**
 * Created by Piotr Bednarczyk on 2017-03-12.
 */
public class OrderBookActor extends UntypedActor {

    public static final class OrderBookRequest {
        private final Market market;

        public OrderBookRequest(Market market) {
            this.market = market;
        }
    }

    public static final int ORDER_BOOK_DEPTH = 100;

    private final ActorSystem system;
    private final PoloniexPublicConnector poloniexConnector;
    private OrderBookHolder orderBookHolder;
    private OrderBookUpdatesQueue updatesQueue;

    @Inject
    public OrderBookActor(ActorSystem actorSystem, OrderBookHolder orderBookHolder,
                          PoloniexPublicConnector poloniexConnector,
                          OrderBookUpdatesQueue updatesQueue) {
        Logger.info("OrderBookActor creation");
        this.updatesQueue = updatesQueue;
        this.orderBookHolder = orderBookHolder;
        this.poloniexConnector = poloniexConnector;
        this.system = actorSystem;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        Logger.debug("modernOrderBookActor received {}", message);

        if (message instanceof OrderBookUpdate) {
            onUpdate((OrderBookUpdate) message);
        } else if (message instanceof OrderBook) {
            onInit((OrderBook) message);
        } else if(message instanceof OrderBookRequest) {
            onOrderBookRequest((OrderBookRequest) message);
        } else {
            unhandled(message);
        }
    }

    private void onInit(OrderBook orderBook) {
        orderBookHolder.initializeOrderBook(orderBook);
        updatesQueue.getUpToDateUpdates(orderBook.getMarket(), orderBook.getSequence())
                .forEachOrdered(update -> updateOrderBook(update));
    }

    private void onUpdate(OrderBookUpdate update) {
        if(orderBookHolder.holdsOrderBook(update.getMarket())) {
            updateOrderBook(update);
        } else {
            updatesQueue.addUpdate(update);
            if(isFirstUpdateInQueue(update.getMarket())) {
                downloadInitialOrderBook(update);
            }
        }
    }

    private void onOrderBookRequest(OrderBookRequest orderBookRequest) {
        sender().tell(orderBookHolder.getOrderBook(orderBookRequest.market), self());
    }

    private void updateOrderBook(OrderBookUpdate update) {
        orderBookHolder.updateOrderBook(update);
    }

    private boolean isFirstUpdateInQueue(Market market) {
        return updatesQueue.getQueueSize(market) == 1;
    }

    private void downloadInitialOrderBook(OrderBookUpdate update) {
        pipe(getOrderBook(update.getMarket()), system.dispatcher()).to(self());
    }

    private CompletionStage<OrderBook> getOrderBook(Market market) {
        return poloniexConnector.getOrderBook(market, ORDER_BOOK_DEPTH);
    }
}
