package ni.org.jug.exchangerate.exception;

import java.util.Objects;

/**
 *
 * @author aalaniz
 */
public class InvalidRangeException extends RuntimeException {

    private static final String INVALID_RANGE_MESSAGE = "El parametro 1 de tipo %s con valor [%s] es mayor que el parametro 2 de tipo %s " +
            "con valor [%s]";

    private InvalidRangeException(String message) {
        super(message);
    }

    public static InvalidRangeException create(Object value1, Object value2) {
        Objects.requireNonNull(value1);
        Objects.requireNonNull(value2);
        String message = String.format(INVALID_RANGE_MESSAGE, value1.getClass().getSimpleName(), value1, value2.getClass().getSimpleName(),
                value2);
        return new InvalidRangeException(message);
    }

}
