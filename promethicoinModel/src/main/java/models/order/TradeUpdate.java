package models.order;

import lombok.Data;
import lombok.NonNull;
import models.Market;

import java.math.BigDecimal;

/**
 * Created by Piotr Bednarczyk on 2017-02-28.
 */
@Data
public class TradeUpdate {
    @NonNull private final Market market;
    @NonNull private final BigDecimal rate;
    @NonNull private final BigDecimal volume;
}
