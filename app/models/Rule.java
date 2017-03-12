package models;

import models.detector.PricePattern;

/**
 * Created by Piotr Bednarczyk on 2017-02-25.
 */
public class Rule {

    private final Market market;
    private final PricePattern patternDetector;
    private final Action action;
    private final boolean removableAfterSingleUse;

    public Rule(Market market, PricePattern patternDetector, Action action, boolean removableAfterSingleUse) {
        this.market = market;
        this.patternDetector = patternDetector;
        this.action = action;
        this.removableAfterSingleUse = removableAfterSingleUse;
    }

    public boolean isMatched(PriceUpdate priceUpdate) {
        return patternDetector.isMatched(priceUpdate);
    }

    public void executeAction() {
        action.execute();
    }

    public boolean isRemovableAfterSingleUse() {
        return removableAfterSingleUse;
    }

    public Market getMarket() {
        return market;
    }
}
