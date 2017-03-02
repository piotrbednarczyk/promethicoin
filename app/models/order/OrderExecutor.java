package models.order;

import play.Logger;

import java.util.Objects;

/**
 * Created by Piotr Bednarczyk on 2017-02-24.
 */
public class OrderExecutor {

    public void executeOrder(Order order) {
        Objects.requireNonNull(order);
        Logger.info("executeOrder {} {}", order, order.getClass());


    }
}
