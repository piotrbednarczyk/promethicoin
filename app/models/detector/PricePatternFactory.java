package models.detector;

import models.Price;

import java.math.BigDecimal;

/**
 * Created by Piotr Bednarczyk on 2017-02-25.
 */
public interface PricePatternFactory {
    DropFixedLimitPattern newDropFixedLimit(Price dropLimit);
    RiseFixedLimitPattern newRiseFixedLimit(Price riseLimit);
    DropPercentLimitPattern newDropPercentLimit(BigDecimal percentLimit);
}