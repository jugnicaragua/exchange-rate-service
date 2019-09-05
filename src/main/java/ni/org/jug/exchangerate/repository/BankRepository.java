package ni.org.jug.exchangerate.repository;

import ni.org.jug.exchangerate.model.Bank;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 *
 * @author aalaniz
 */
public interface BankRepository extends CrudRepository<Bank, Integer> {

    @Override
    @Query("SELECT DISTINCT b FROM Bank b LEFT JOIN FETCH b.cookies c")
    Iterable<Bank> findAll();

    @Override
    @Query("SELECT DISTINCT b FROM Bank b LEFT JOIN FETCH b.cookies c WHERE b.id = :id")
    Optional<Bank> findById(@Param("id") Integer id);

    @Query("SELECT DISTINCT b FROM Bank b LEFT JOIN FETCH b.cookies c WHERE b.description.shortDescription = :shortDescription")
    Bank findByShortDescription(@Param("shortDescription") String shortDescription);

}
