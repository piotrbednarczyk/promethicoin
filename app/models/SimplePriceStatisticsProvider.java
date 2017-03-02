package models;

import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Piotr Bednarczyk on 2017-02-24.
 */
public class SimplePriceStatisticsProvider implements UpdatablePriceStatisticsProvider {

    private final Map<CurrencyPair, PriceStatistics> currencyPairStatistics = new HashMap<>();

    @Override
    public void update(Ticker ticker) {
        Objects.requireNonNull(ticker);
        CurrencyPair currencyPair = ticker.getCurrencyPair();

        if(currencyPairStatistics.containsKey(currencyPair)) {
            currencyPairStatistics.get(currencyPair).updateOnLastPrice(ticker.getLastPrice());
        } else {
            currencyPairStatistics.put(currencyPair, new PriceStatistics(ticker.getLastPrice()));
        }
    }

    @Override
    public Optional<PriceStatistics> getStatisticsForCurrencyPair(CurrencyPair currencyPair) {
        return Optional.ofNullable(currencyPairStatistics.get(currencyPair));
    }
}
