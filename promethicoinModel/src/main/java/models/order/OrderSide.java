package models.order;

import java.text.MessageFormat;

/**
 * Created by Piotr Bednarczyk on 2017-02-27.
 */
public enum OrderSide {
    BID,
    ASK;

    public static OrderSide fromReceivedSide(String side) {
        if (side.equalsIgnoreCase("ask")) {
            return ASK;
        } else if (side.equalsIgnoreCase("bid")) {
            return BID;
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Can't map provided value {0} to OrderSide", side));
        }
    }
}
