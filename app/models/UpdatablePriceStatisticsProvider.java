package models;

/**
 * Created by Piotr Bednarczyk on 2017-02-25.
 */
public interface UpdatablePriceStatisticsProvider extends PriceStatisticsProvider {
    void update(Ticker ticker);
}
