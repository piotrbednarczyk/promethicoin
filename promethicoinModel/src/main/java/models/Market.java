package models;

import lombok.Data;
import lombok.NonNull;
import models.CurrencyPair;

/**
 * Created by Piotr Bednarczyk on 2017-02-24.
 */
@Data
public class Market {
    @NonNull
    private final String exchange;
    @NonNull
    private final CurrencyPair currencyPair;
}
