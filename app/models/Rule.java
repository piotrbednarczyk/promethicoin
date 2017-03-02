package models;

import models.detector.PricePattern;

/**
 * Created by Piotr Bednarczyk on 2017-02-25.
 */
public class Rule {

    private final PricePattern patternDetector;
    private final Action action;
    private final boolean removableAfterSingleUse;

    public Rule(PricePattern patternDetector, Action action, boolean removableAfterSingleUse) {
        this.patternDetector = patternDetector;
        this.action = action;
        this.removableAfterSingleUse = removableAfterSingleUse;
    }

    public boolean isMatched(Ticker ticker) {
        return patternDetector.isMatched(ticker);
    }

    public void executeAction() {
        action.execute();
    }

    public boolean isRemovableAfterSingleUse() {
        return removableAfterSingleUse;
    }
}
