import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import connectors.poloniex.subscribtion.OrderBookSubscriberFactory;
import connectors.poloniex.subscribtion.SubscriptionInitializer;
import models.PriceStatisticsProvider;
import models.SimplePriceStatisticsProvider;
import models.UpdatablePriceStatisticsProvider;
import models.detector.PricePatternFactory;
import models.order.OrderBookHolder;
import play.Logger;

/**
 * Created by Piotr on 2017-02-17.
 */
public class Module extends AbstractModule {

    @Override
    protected void configure() {
        Logger.info("Configuration before start-up");
        install(new FactoryModuleBuilder()
                .build(PricePatternFactory.class));

        install(new FactoryModuleBuilder()
                .build(OrderBookSubscriberFactory.class));

        bind(PriceStatisticsProvider.class).to(SimplePriceStatisticsProvider.class);
        bind(UpdatablePriceStatisticsProvider.class).to(SimplePriceStatisticsProvider.class);
        bind(SimplePriceStatisticsProvider.class).in(Singleton.class);

        bind(SubscriptionInitializer.class).asEagerSingleton();
    }
}
