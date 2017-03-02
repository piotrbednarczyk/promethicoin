package models.detector;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import models.Price;
import models.Ticker;

import java.util.Objects;

/**
 * Created by Piotr Bednarczyk on 2017-02-24.
 */
public class RiseFixedLimitPattern implements PricePattern {

    private final Price riseLimit;

    @Inject
    public RiseFixedLimitPattern(@Assisted Price limit) {
        Objects.requireNonNull(limit);
        this.riseLimit = limit;
    }

    @Override
    public boolean isMatched(Ticker ticker) {
        Objects.requireNonNull(ticker);
        return ticker.getLastPrice().greaterOrEqual(riseLimit);
    }
}
