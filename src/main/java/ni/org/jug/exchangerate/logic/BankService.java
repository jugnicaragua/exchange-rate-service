package ni.org.jug.exchangerate.logic;

import ni.org.jug.exchangerate.exception.BankNotFoundException;
import ni.org.jug.exchangerate.model.Bank;
import ni.org.jug.exchangerate.model.Cookie;
import ni.org.jug.exchangerate.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author aalaniz
 */
@Service
@Transactional
public class BankService {

    @Autowired
    BankRepository bankRepository;

    public Cookie addCookie(Integer id, Cookie transientCookie) {
        Bank bank = bankRepository.findById(id).orElseThrow(() -> new BankNotFoundException(id));
        bank.addCookie(transientCookie);
        bankRepository.save(bank);
        return bank.getCookie(transientCookie.getName()).orElse(null);
    }

}
