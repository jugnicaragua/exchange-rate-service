package ni.org.jug.exchangerate.exception;

/**
 *
 * @author aalaniz
 */
public class CentralBankExchangeRateNotFoundException extends NotFoundException {

    public CentralBankExchangeRateNotFoundException(Object id) {
        super("Tipo de Cambio Oficial", id);
    }

}
