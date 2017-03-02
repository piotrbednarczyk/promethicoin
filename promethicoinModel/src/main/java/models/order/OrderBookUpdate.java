package models.order;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import models.Market;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * Created by Piotr Bednarczyk on 2017-02-27.
 */
@Data
@Builder
public class OrderBookUpdate {
    @NonNull private final Market market;
    @NonNull private final BigDecimal rate;
    private final BigDecimal volume;
    @NonNull private final OrderSide side;
    @NonNull private final OrderBookUpdateType type;

    public enum OrderBookUpdateType {
        ORDER_BOOK_MODIFY,
        ORDER_BOOK_REMOVE;

        public static OrderBookUpdateType fromRecievedUpdateType(String type) {
            if (type.equalsIgnoreCase("orderBookModify")) {
                return ORDER_BOOK_MODIFY;
            } else if (type.equalsIgnoreCase("orderBookRemove")) {
                return ORDER_BOOK_REMOVE;
            } else {
                throw new IllegalArgumentException(MessageFormat.format("Can't map provided value {0} to OrderBookUpdateType", type));
            }
        }
    }
}
