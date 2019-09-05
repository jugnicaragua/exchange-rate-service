package ni.org.jug.exchangerate.rest;

import ni.org.jug.exchangerate.exception.BankNotFoundException;
import ni.org.jug.exchangerate.model.Bank;
import ni.org.jug.exchangerate.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 *
 * @author aalaniz
 */
@RestController
@RequestMapping("/api/banks")
public class BankController {

    @Autowired
    BankRepository bankRepository;

    @GetMapping
    public Response findAll() {
        Iterable itr = bankRepository.findAll();
        return new Response(itr);
    }

    @GetMapping("/{id}")
    public Bank findById(@PathVariable Integer id) {
        return bankRepository.findById(id).orElseThrow(() -> new BankNotFoundException(id));
    }

    @GetMapping("/description/{bank}")
    public Bank findByShortDescription(@PathVariable String bank) {
        return Optional.ofNullable(bankRepository.findByShortDescription(bank))
                .orElseThrow(() -> new BankNotFoundException(BankNotFoundException.SearchableField.DESCRIPTION, bank));
    }

}
