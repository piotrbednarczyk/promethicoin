package models.detector;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import models.Price;
import models.PriceUpdate;
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
    public boolean isMatched(PriceUpdate priceUpdate) {
        Logger.info("Checking price drop...");
        Objects.requireNonNull(priceUpdate);
        return priceUpdate.getLastPrice().lessOrEqual(dropLimit);
    }
}
