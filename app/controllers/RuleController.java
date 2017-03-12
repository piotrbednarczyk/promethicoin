package controllers;

import akka.actor.ActorRef;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import connectors.poloniex.api.PoloniexTradingConnector;
import models.CurrencyPair;
import models.Market;
import models.Rule;
import models.detector.DropPercentLimitPattern;
import models.detector.PricePatternFactory;
import models.order.MarketOrder;
import models.order.OrderSide;
import play.mvc.Controller;
import play.mvc.Result;

import java.math.BigDecimal;

/**
 * Created by Piotr Bednarczyk on 2017-02-25.
 */
public class RuleController extends Controller {

    private final PricePatternFactory detectorFactory;
    private final ActorRef orderExecutionActor;
    private final ActorRef rulesEngineActor;
    private final ActorRef orderBookActor;
    private PoloniexTradingConnector connector;

    @Inject
    public RuleController(PricePatternFactory detectorFactory,
                          @Named("orderExecutionActor") ActorRef orderExecutionActor,
                          @Named("rulesEngineActor") ActorRef rulesEngineActor,
                          @Named("orderBookActor") ActorRef orderBookActor,
                          PoloniexTradingConnector connector) {
        this.detectorFactory = detectorFactory;
        this.orderExecutionActor = orderExecutionActor;
        this.rulesEngineActor = rulesEngineActor;
        this.orderBookActor = orderBookActor;
        this.connector = connector;
    }

    public Result addRule() {

        DropPercentLimitPattern detector = detectorFactory.newDropPercentLimit(new BigDecimal("0.0001"));

        connector.getAccountBalances();

        final Rule rule = new Rule(new Market("Poloniex", CurrencyPair.USDT_BTC), detector, () -> {
            orderExecutionActor.tell(new MarketOrder(new Market("Poloniex", CurrencyPair.USDT_BTC), BigDecimal.TEN, OrderSide.ASK), null);
        }, true);
        rulesEngineActor.tell(rule, null);

        return ok("Rule added");
    }
}
