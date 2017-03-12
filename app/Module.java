import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import connectors.poloniex.subscribtion.OrderBookSubscriberFactory;
import connectors.poloniex.subscribtion.SubscriptionInitializer;
import models.PriceStatisticsProvider;
import models.RulesEngineActor;
import models.SimplePriceStatisticsProvider;
import models.UpdatablePriceStatisticsProvider;
import models.detector.PricePatternFactory;
import models.order.OrderBookActor;
import models.order.OrderExecutionActor;
import play.Logger;
import play.libs.akka.AkkaGuiceSupport;

/**
 * Created by Piotr on 2017-02-17.
 */
public class Module extends AbstractModule implements AkkaGuiceSupport {

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

        bindActor(OrderBookActor.class, "orderBookActor");
        bindActor(OrderExecutionActor.class, "orderExecutionActor");
        bindActor(RulesEngineActor.class, "rulesEngineActor");
    }
}
