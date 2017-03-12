package models.detector;

import models.PriceUpdate;

/**
 * Created by Piotr Bednarczyk on 2017-02-22.
 */
public interface PricePattern {
    boolean isMatched(PriceUpdate priceUpdate);
}
