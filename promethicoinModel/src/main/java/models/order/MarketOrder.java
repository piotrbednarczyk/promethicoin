package models.order;

import lombok.Data;
import lombok.NonNull;
import models.Market;
import models.Price;

import java.math.BigDecimal;

/**
 * Created by Piotr Bednarczyk on 2017-02-25.
 */
@Data
public class MarketOrder implements Order {

    @NonNull
    private final Market market;
    @NonNull
    private final BigDecimal volume;
    @NonNull
    private final OrderSide side;
}
