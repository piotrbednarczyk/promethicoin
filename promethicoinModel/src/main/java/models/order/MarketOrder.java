package models.order;

import models.Market;
import models.Price;

import java.math.BigDecimal;

/**
 * Created by Piotr Bednarczyk on 2017-02-25.
 */
public class MarketOrder implements Order {

    @Override
    public Market getMarket() {
        return null;
    }

    @Override
    public BigDecimal getVolume() {
        return null;
    }

    @Override
    public Price getPrice() {
        return null;
    }
}
