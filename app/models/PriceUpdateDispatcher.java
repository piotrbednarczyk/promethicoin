package models;

import com.google.inject.Inject;
import play.Logger;

import java.util.Objects;

/**
 * Created by Piotr Bednarczyk on 2017-02-20.
 */
public class PriceUpdateDispatcher {
    private final UpdatablePriceStatisticsProvider statisticsProvider;
    private final RulesEngine rulesEngine;

    @Inject
    public PriceUpdateDispatcher(UpdatablePriceStatisticsProvider statisticsProvider,
                                 RulesEngine rulesEngine) {
        this.statisticsProvider = statisticsProvider;
        this.rulesEngine = rulesEngine;
    }

    public void onPriceUpdate(Ticker ticker) {
        Objects.requireNonNull(ticker);
        Logger.trace("onPriceUpdate {}", ticker);

        statisticsProvider.update(ticker);
        rulesEngine.processEvent(ticker);
    }
}
