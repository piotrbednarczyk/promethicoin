package models.order;

import models.Market;
import models.Price;

import java.math.BigDecimal;

/**
 * Created by Piotr Bednarczyk on 2017-02-21.
 */
public interface Order {

    Market getMarket();

    BigDecimal getVolume();
}
