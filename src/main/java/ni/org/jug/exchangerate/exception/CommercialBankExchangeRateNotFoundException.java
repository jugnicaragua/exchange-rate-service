package ni.org.jug.exchangerate.exception;

/**
 *
 * @author aalaniz
 */
public class CommercialBankExchangeRateNotFoundException extends NotFoundException {

    public CommercialBankExchangeRateNotFoundException(Object id) {
        super("Compra/Venta de dolares", id);
    }

}
