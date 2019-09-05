package ni.org.jug.exchangerate.exception;

/**
 *
 * @author aalaniz
 */
public class NotFoundException extends RuntimeException {

    private static final String ENTITY_WITH_FIELD_NOT_FOUND_MESSAGE = "%s con %s [%s] no fue encontrado";

    private static final String FIELD_ID = "ID";

    protected NotFoundException(String entity, Object id) {
        super(String.format(ENTITY_WITH_FIELD_NOT_FOUND_MESSAGE, entity, FIELD_ID, id));
    }

    protected NotFoundException(String entity, Field field, Object value) {
        super(String.format(ENTITY_WITH_FIELD_NOT_FOUND_MESSAGE, entity, field.description(), value));
    }

    public interface Field {
        String description();
    }

}
