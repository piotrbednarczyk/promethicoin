package models.order;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

/**
 * Created by Piotr Bednarczyk on 2017-02-26.
 */
@Data
public class OrderBookItem {

    @NonNull
    private final BigDecimal rate;
    @NonNull
    private final BigDecimal volume;
}
