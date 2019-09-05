package ni.org.jug.exchangerate.exception;

/**
 *
 * @author aalaniz
 */
public class CookieNotFoundException extends NotFoundException {

    public CookieNotFoundException(Object id) {
        super("Cookie", id);
    }

}
