package ni.org.jug.exchangerate.repository;

import ni.org.jug.exchangerate.model.Currency;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author aalaniz
 */
public interface CurrencyRepository extends CrudRepository<Currency, Integer> {

    @Query("SELECT c FROM Currency c WHERE c.isoStringCode = 'USD'")
    Currency findDollar();

}
