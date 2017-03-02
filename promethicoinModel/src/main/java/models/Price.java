package models;

import lombok.Data;
import lombok.NonNull;
import sun.util.resources.cldr.ebu.CurrencyNames_ebu;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Created by Piotr Bednarczyk on 2017-02-23.
 */
@Data
public class Price {
    private final static int SCALE = 10;
    private final static RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    private final BigDecimal value;
    private final CurrencyPair currencyPair;

    public Price(@NonNull BigDecimal value, @NonNull CurrencyPair currencyPair) {
        if(value.signum() < 0) {
            throw new IllegalArgumentException("Price value is below zero!");
        }
        this.value = value.setScale(SCALE, ROUNDING_MODE);
        this.currencyPair = currencyPair;
    }

    public boolean greaterThan(Price price) {
        return greaterThan(price.getValue());
    }

    public boolean greaterOrEqual(Price price) {
        return greaterOrEqual(price.getValue());
    }

    public boolean lessThan(Price price) {
        return lessThan(price.getValue());
    }

    public boolean lessOrEqual(Price price) {
        return lessOrEqual(price.getValue());
    }

    public boolean equalTo(Price price) {
        return equalTo(price.getValue());
    }

    public boolean isPositive() {
        return greaterThan(BigDecimal.ZERO);
    }

    public boolean isNegative() {
        return lessThan(BigDecimal.ZERO);
    }

    public boolean isBetween(Price first, Price second) {
        return isBetween(first.getValue(), second.getValue());
    }

    public boolean greaterThan(BigDecimal value) {
        return this.value.compareTo(value) > 0;
    }

    public boolean greaterOrEqual(BigDecimal value) {
        return this.value.compareTo(value) >= 0;
    }

    public boolean lessThan(BigDecimal value) {
        return this.value.compareTo(value) < 0;
    }

    public boolean lessOrEqual(BigDecimal value) {
        return this.value.compareTo(value) <= 0;
    }

    public boolean equalTo(BigDecimal value) {
        return this.value.compareTo(value) == 0;
    }

    public boolean isBetween(BigDecimal first, BigDecimal second) {
        return value.compareTo(first) + value.compareTo(second) == 0;
    }

    public boolean isCurrencyPairEqual(Price price) {
        return isCurrencyPairEqual(price.getCurrencyPair());
    }

    public boolean isCurrencyPairEqual(CurrencyPair currencyPair) {
        return this.currencyPair.equals(currencyPair);
    }
}
