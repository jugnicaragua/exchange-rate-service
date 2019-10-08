package ni.org.jug.exchangerate.exception;

public class UserSubscriptionNotFoundException extends NotFoundException {

    private static final String ENTITY_DESCRIPTION = "Subscripcion";
    private static final String EMAIL_FIELD = "email";

    public UserSubscriptionNotFoundException(String email) {
        super(ENTITY_DESCRIPTION, EMAIL_FIELD, email);
    }
}
