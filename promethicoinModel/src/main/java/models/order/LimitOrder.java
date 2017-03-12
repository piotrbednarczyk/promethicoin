package models.order;

import lombok.Data;
import lombok.NonNull;
import models.Market;
import models.Price;

import java.math.BigDecimal;

/**
 * Created by Piotr Bednarczyk on 2017-03-10.
 */
@Data
public class LimitOrder {

    @NonNull
    private final Market market;
    @NonNull
    private final BigDecimal volume;
    @NonNull
    private final Price price;
}
