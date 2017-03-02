package models;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Created by Piotr Bednarczyk on 2017-02-24.
 */
@EqualsAndHashCode
@ToString
@Getter
public class PriceStatistics {

    private final CurrencyPair currencyPair;
    private BigDecimal lastValue;
    private BigDecimal maxObservedValue;
    private BigDecimal minObservedValue;

    public PriceStatistics(@NonNull Price price) {
        this.lastValue = price.getValue();
        this.currencyPair = price.getCurrencyPair();
        maxObservedValue = lastValue;
        minObservedValue = lastValue;
    }

    public void updateOnLastPrice(@NonNull Price price) {
        Preconditions.checkArgument(price.isCurrencyPairEqual(price), "price currencyPairs do not match!");
        BigDecimal newValue = price.getValue();

        if(price.greaterOrEqual(maxObservedValue)) {
            maxObservedValue = newValue;
        } else {
            minObservedValue = newValue;
        }
        lastValue = newValue;
    }
}
