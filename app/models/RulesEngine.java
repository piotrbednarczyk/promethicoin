package models;

import com.google.inject.Singleton;
import controllers.RuleController;
import play.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Piotr Bednarczyk on 2017-02-26.
 */
@Singleton
public class RulesEngine {

    private final ConcurrentMap<CurrencyPair, Rule> rules = new ConcurrentHashMap<>();

    //TODO: more abstract version on general events
    public void add(Rule rule, CurrencyPair currencyPair) {
        rules.put(currencyPair, rule);
    }

    //TODO: think about concurrency here
    public void processEvent(Ticker ticker) {
        CurrencyPair currencyPair = ticker.getCurrencyPair();

        if(! rules.containsKey(currencyPair)) {
            return;
        }

        Logger.debug("Processing event for {}", currencyPair);
        Rule rule = rules.get(currencyPair);
        if (rule.isMatched(ticker)) {
            rule.executeAction();
            afterExecution(rule, currencyPair);
        }
    }

    public void removeRule(Rule rule, CurrencyPair currencyPair) {
        rules.remove(currencyPair, rule);
    }

    private void afterExecution(Rule rule, CurrencyPair currencyPair) {
        if(rule.isRemovableAfterSingleUse()) {
            removeRule(rule, currencyPair);
        }
    }
}
