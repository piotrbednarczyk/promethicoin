package models.detector;

import models.Ticker;

/**
 * Created by Piotr Bednarczyk on 2017-02-22.
 */
public interface PricePattern {
    boolean isMatched(Ticker ticker);
}
