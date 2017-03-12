package models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Piotr Bednarczyk on 2017-02-24.
 */
public class SimplePriceStatisticsProvider implements UpdatablePriceStatisticsProvider {

    private final Map<Market, PriceStatistics> currencyPairStatistics = new HashMap<>();

    @Override
    public void update(PriceUpdate priceUpdate) {
        Objects.requireNonNull(priceUpdate);
        Market market = priceUpdate.getMarket();

        if(currencyPairStatistics.containsKey(market)) {
            currencyPairStatistics.get(market).updateOnLastPrice(priceUpdate.getLastPrice());
        } else {
            currencyPairStatistics.put(market, new PriceStatistics(priceUpdate.getLastPrice()));
        }
    }

    @Override
    public Optional<PriceStatistics> getStatisticsForCurrencyPair(Market market) {
        return Optional.ofNullable(currencyPairStatistics.get(market));
    }
}
