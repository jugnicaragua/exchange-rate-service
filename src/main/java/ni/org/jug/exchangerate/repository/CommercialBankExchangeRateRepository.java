package ni.org.jug.exchangerate.repository;

import ni.org.jug.exchangerate.model.CommercialBankExchangeRate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author aalaniz
 */
public interface CommercialBankExchangeRateRepository extends CrudRepository<CommercialBankExchangeRate, Integer> {

    @Override
    @Query("SELECT c FROM CommercialBankExchangeRate c JOIN FETCH c.currency curr JOIN FETCH c.bank b")
    Iterable<CommercialBankExchangeRate> findAll();

    @Override
    @Query("SELECT c FROM CommercialBankExchangeRate c JOIN FETCH c.currency curr JOIN FETCH c.bank b WHERE c.id = :id")
    Optional<CommercialBankExchangeRate> findById(@Param("id") Integer id);

    @Query("SELECT c FROM CommercialBankExchangeRate c JOIN FETCH c.currency curr JOIN FETCH c.bank b WHERE c.date = :date")
    List<CommercialBankExchangeRate> findByDate(@Param("date") LocalDate date);

    @Query("SELECT c FROM CommercialBankExchangeRate c JOIN FETCH c.currency curr JOIN FETCH c.bank b WHERE c.date BETWEEN :start AND :end")
    List<CommercialBankExchangeRate> findByDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT c FROM CommercialBankExchangeRate c JOIN FETCH c.currency curr JOIN FETCH c.bank b " +
            "WHERE b.description.shortDescription = :bank")
    List<CommercialBankExchangeRate> findByBank(@Param("bank") String bank);

    @Query("SELECT c FROM CommercialBankExchangeRate c JOIN FETCH c.currency curr JOIN FETCH c.bank b " +
            "WHERE b.description.shortDescription = :bank AND c.date = :date")
    List<CommercialBankExchangeRate> findByBankAndDate(@Param("bank") String bank, @Param("date") LocalDate date);

    @Query("SELECT c FROM CommercialBankExchangeRate c JOIN FETCH c.currency curr JOIN FETCH c.bank b " +
            "WHERE b.description.shortDescription = :bank AND c.date BETWEEN :start AND :end")
    List<CommercialBankExchangeRate> findByBankAndDateBetween(@Param("bank") String bank, @Param("start") LocalDate date,
            @Param("end") LocalDate end);

}
