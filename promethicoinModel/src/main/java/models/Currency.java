package models;

import lombok.Data;
import lombok.NonNull;

/**
 * Created by Piotr Bednarczyk on 2017-03-10.
 */
@Data
public class Currency {
    @NonNull
    private final String code;
}
