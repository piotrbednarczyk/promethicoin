package models.detector;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import models.Price;
import models.Ticker;
import play.Logger;

import java.util.Objects;

/**
 * Created by Piotr Bednarczyk on 2017-02-24.
 */
public class DropFixedLimitPattern implements PricePattern {

    private final Price dropLimit;

    @Inject
    public DropFixedLimitPattern(@Assisted Price dropLimit) {
        Logger.info("DropFixedLimitPattern construction");
        Objects.requireNonNull(dropLimit);
        this.dropLimit = dropLimit;
    }

    @Override
    public boolean isMatched(Ticker ticker) {
        Logger.info("Checking price drop...");
        Objects.requireNonNull(ticker);
        return ticker.getLastPrice().lessOrEqual(dropLimit);
    }
}
