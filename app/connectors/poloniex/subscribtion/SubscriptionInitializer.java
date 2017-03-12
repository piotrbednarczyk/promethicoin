package connectors.poloniex.subscribtion;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import connectors.poloniex.subscribtion.wamp.WampClientProvider;
import models.CurrencyPair;
import models.Market;
import play.Logger;
import play.api.Configuration;
import play.inject.ApplicationLifecycle;
import ws.wamp.jawampa.WampClient;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;

/**
 * Created by Piotr Bednarczyk on 2017-02-18.
 */
public class SubscriptionInitializer implements ApplicationLifecycle {

    private final Config config;
    private final WampClientProvider wampClientProvider;
    private final OrderBookSubscriberFactory subscriberFactory;
    private final PriceUpdateSubscriber priceUpdateSubscriber;
    private WampClient client;

    @Inject
    public SubscriptionInitializer(Configuration configuration,
                                   PriceUpdateSubscriber priceUpdateSubscriber,
                                   WampClientProvider wampClientProvider,
                                   OrderBookSubscriberFactory subscriberFactory) throws Exception {
        this.config = configuration.underlying();
        this.wampClientProvider = wampClientProvider;
        this.priceUpdateSubscriber = priceUpdateSubscriber;
        this.subscriberFactory = subscriberFactory;

        startSubscriptions();
    }

    @Override
    public void addStopHook(Callable<? extends CompletionStage<?>> hook) {
        Logger.info("Stop hook - terminating subscription from Poloniex");
        client.close().toBlocking().last();
        Logger.info("Subscription from Poloniex terminated");
    }

    public void startSubscriptions() throws Exception {
        Logger.info("Starting ticker subscription from Poloniex");

        client = wampClientProvider.getWampClient();

        subscribeToTicker(priceUpdateSubscriber);
        subscribeToOrderBooks(subscriberFactory, loadCurrencyPairs(), loadPoloniexName());

        client.open();
    }

    private List<String> loadCurrencyPairs() {
        return config.getStringList("poloniex.currencyPairs");
    }

    private String loadPoloniexName() {
        return config.getString("poloniex.name");
    }

    private void subscribeToOrderBooks(OrderBookSubscriberFactory subscriberFactory, List<String> stringList, String exchange) {
        stringList.stream().forEach(currencyPair -> client.statusChanged().subscribe((state) -> {
            if (state instanceof WampClient.ConnectedState) {
                OrderBookSubscriber subscriber = subscriberFactory
                        .newOrderBookSubscriber(new Market(exchange, new CurrencyPair(currencyPair)));
                client.makeSubscription(currencyPair)
                        .subscribe(subscriber);
            }
        }));
    }

    private void subscribeToTicker(PriceUpdateSubscriber priceUpdateSubscriber) {
        if (config.getBoolean("poloniex.wamp.ticker.enabled")) {
            client.statusChanged().subscribe((state) -> {
                if (state instanceof WampClient.ConnectedState) {
                    client.makeSubscription("ticker").subscribe(priceUpdateSubscriber);
                }
            });
        }
    }
}
