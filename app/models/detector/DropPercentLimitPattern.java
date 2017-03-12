package models.detector;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import models.*;
import play.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Piotr Bednarczyk on 2017-02-22.
 */
public class DropPercentLimitPattern implements PricePattern {

    private final PriceStatisticsProvider statisticsProvider;
    private final BigDecimal percentageLimit;

    @Inject
    public DropPercentLimitPattern(PriceStatisticsProvider statisticsProvider, @Assisted BigDecimal percentageLimit) {
        Objects.requireNonNull(percentageLimit);
        Preconditions.checkArgument(percentageLimit.signum() > 0,"percentageLimit has to be positive!");
        Logger.info("DropPercentLimitPattern creation with limit {}%", percentageLimit);
        this.percentageLimit = percentageLimit;
        this.statisticsProvider = statisticsProvider;
    }

    @Override
    public boolean isMatched(PriceUpdate priceUpdate) {
        Objects.requireNonNull(priceUpdate);

        BigDecimal maxObservedValue = getStatisticsForCurrencyPair(priceUpdate.getMarket()).getMaxObservedValue();
        BigDecimal difference = calculatePercentageDifference(maxObservedValue, priceUpdate.getLastPrice().getValue());
        boolean matched = difference.compareTo(percentageLimit) >= 0;

        if(matched) {
            Logger.info("Pattern with limit {}% matched for {} (max observed value {})",
                    percentageLimit, priceUpdate.getLastPrice(), maxObservedValue);
        }

        return matched;
    }

    private PriceStatistics getStatisticsForCurrencyPair(Market market) {
        Optional<PriceStatistics> statisticsOptional = statisticsProvider.getStatisticsForCurrencyPair(market);
        return statisticsOptional.orElseThrow(() -> new IllegalStateException("No statistics for " + market));
    }

    private BigDecimal calculatePercentageDifference(BigDecimal initialValue, BigDecimal newValue) {
        return initialValue.subtract(newValue)
                .divide(initialValue, 10, RoundingMode.HALF_UP)
                .scaleByPowerOfTen(2);
    }
}
