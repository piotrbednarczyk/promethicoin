package models;

import lombok.Data;
import lombok.NonNull;

/**
 * Created by Piotr Bednarczyk on 2017-02-24.
 */
@Data
public class CurrencyPair {

    public static final CurrencyPair USDT_BTC = new CurrencyPair("USDT_BTC");

    @NonNull
    private final String codes;
}
