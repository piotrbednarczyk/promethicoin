package models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by Piotr Bednarczyk on 2017-02-19.
 */
@Data
@Builder
public class Ticker {
    @NonNull
    private final LocalDateTime occurrenceTime;
    @NonNull
    private final String source;
    @NonNull
    private final Price lastPrice;
    @NonNull
    private final BigDecimal lowestAsk;
    @NonNull
    private final BigDecimal highestBid;
    @NonNull
    private final BigDecimal percentChange;
    @NonNull
    private final BigDecimal baseVolume;
    @NonNull
    private final BigDecimal quoteVolume;
    @NonNull
    private final BigDecimal dayHigh;
    @NonNull
    private final BigDecimal dayLow;
    @NonNull
    private final Boolean isFrozen;

    public CurrencyPair getCurrencyPair() {
        return lastPrice.getCurrencyPair();
    }
}