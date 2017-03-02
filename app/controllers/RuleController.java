package controllers;

import com.google.inject.Inject;
import models.CurrencyPair;
import models.Rule;
import models.RulesEngine;
import models.detector.DropPercentLimitPattern;
import models.detector.PricePatternFactory;
import models.order.MarketOrder;
import models.order.OrderExecutor;
import play.mvc.Controller;
import play.mvc.Result;

import java.math.BigDecimal;

/**
 * Created by Piotr Bednarczyk on 2017-02-25.
 */
public class RuleController extends Controller {

    private final PricePatternFactory detectorFactory;
    private final OrderExecutor orderExecutor;
    private final RulesEngine rulesEngine;

    @Inject
    public RuleController(PricePatternFactory detectorFactory, OrderExecutor orderExecutor, RulesEngine rulesEngine) {
        this.detectorFactory = detectorFactory;
        this.orderExecutor = orderExecutor;
        this.rulesEngine = rulesEngine;
    }

    public Result addRule() {

        DropPercentLimitPattern detector = detectorFactory.newDropPercentLimit(new BigDecimal("0.0001"));

        final Rule rule = new Rule(detector, () -> {
            orderExecutor.executeOrder(new MarketOrder());
        }, true);
        rulesEngine.add(rule, CurrencyPair.USDT_BTC);

        return ok("Rule added");
    }
}
