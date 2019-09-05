package ni.org.jug.exchangerate.exception;

/**
 *
 * @author aalaniz
 */
public class BankNotFoundException extends NotFoundException {

    private static final String ENTITY_DESCRIPTION = "Banco";

    public enum SearchableField implements Field {
        DESCRIPTION("descripcion");

        private final String field;

        SearchableField(String field) {
            this.field = field;
        }

        @Override
        public String description() {
            return field;
        }
    }

    public BankNotFoundException(Object id) {
        super(ENTITY_DESCRIPTION, id);
    }

    public BankNotFoundException(SearchableField field, Object value) {
        super(ENTITY_DESCRIPTION, field, value);
    }

}
