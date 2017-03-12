package models;

import akka.actor.UntypedActor;
import com.google.inject.Inject;
import play.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Piotr Bednarczyk on 2017-02-26.
 */
public class RulesEngineActor extends UntypedActor {

    private final UpdatablePriceStatisticsProvider statisticsProvider;

    private final ConcurrentMap<Market, List<Rule>> rules = new ConcurrentHashMap<>();

    @Inject
    public RulesEngineActor(UpdatablePriceStatisticsProvider statisticsProvider) {
        Logger.info("RulesEngineActor construction");
        this.statisticsProvider = statisticsProvider;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof PriceUpdate) {
            update((PriceUpdate) message);
        } else if(message instanceof Rule) {
            add((Rule) message);
        } else {
            unhandled(message);
        }
    }

    private void add(Rule rule) {
        Market market = rule.getMarket();
        if(! rules.containsKey(market)) {
            rules.put(market, new ArrayList<>());
        }
        rules.get(market).add(rule);
    }

    private void update(PriceUpdate priceUpdate) {
        Market market = priceUpdate.getMarket();

        if(! rules.containsKey(market)) {
            return;
        }
        Logger.debug("Processing event for {}", market);
        statisticsProvider.update(priceUpdate);

        //TODO: should all rules be executed or just first match?
        rules.get(market).stream().forEachOrdered(rule -> {
            if (rule.isMatched(priceUpdate)) {
                rule.executeAction();
                afterExecution(rule);
            }
        });
    }

    private void removeRule(Rule rule) {
        rules.get(rule.getMarket()).remove(rule);
    }

    private void afterExecution(Rule rule) {
        if(rule.isRemovableAfterSingleUse()) {
            removeRule(rule);
        }
    }
}
