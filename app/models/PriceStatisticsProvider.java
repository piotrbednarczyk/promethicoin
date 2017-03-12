package models;

import java.util.Optional;

/**
 * Created by Piotr Bednarczyk on 2017-02-25.
 */
public interface PriceStatisticsProvider {
    Optional<PriceStatistics> getStatisticsForCurrencyPair(Market market);
}
