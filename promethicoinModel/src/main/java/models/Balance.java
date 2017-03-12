package models;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

/**
 * Created by Piotr Bednarczyk on 2017-03-10.
 */
@Data
public class Balance {
    @NonNull
    private final BigDecimal amount;
    @NonNull
    private final Currency currency;
}
