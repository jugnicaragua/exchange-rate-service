package ni.org.jug.exchangerate.repository;

import ni.org.jug.exchangerate.model.CentralBankExchangeRate;
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
public interface CentralBankExchangeRateRepository extends CrudRepository<CentralBankExchangeRate, Integer> {

    @Query("SELECT c FROM CentralBankExchangeRate c JOIN FETCH c.currency curr WHERE c.id = :id")
    @Override
    Optional<CentralBankExchangeRate> findById(@Param("id") Integer id);

    @Query("SELECT c FROM CentralBankExchangeRate c JOIN FETCH c.currency curr")
    @Override
    Iterable<CentralBankExchangeRate> findAll();

    @Query("SELECT c FROM CentralBankExchangeRate c JOIN FETCH c.currency curr WHERE c.id BETWEEN :start AND :end")
    List<CentralBankExchangeRate> findByIdBetween(@Param("start") Integer start, @Param("end") Integer end);

    @Query("SELECT c FROM CentralBankExchangeRate c JOIN FETCH c.currency curr WHERE c.date BETWEEN :start AND :end")
    List<CentralBankExchangeRate> findByDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(c) FROM CentralBankExchangeRate c WHERE c.date BETWEEN :start AND :end")
    Long countByDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

}
