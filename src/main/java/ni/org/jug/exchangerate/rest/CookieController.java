package ni.org.jug.exchangerate.rest;

import ni.org.jug.exchangerate.exception.BankNotFoundException;
import ni.org.jug.exchangerate.exception.CookieNotFoundException;
import ni.org.jug.exchangerate.logic.BankService;
import ni.org.jug.exchangerate.model.Bank;
import ni.org.jug.exchangerate.model.Cookie;
import ni.org.jug.exchangerate.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;

/**
 *
 * @author aalaniz
 */
@RestController
@RequestMapping("/api/banks/{id}/cookies")
public class CookieController {

    @Autowired
    BankRepository bankRepository;

    @Autowired
    BankService bankService;

    @GetMapping
    public Response findAll(@PathVariable Integer id) {
        Set<Cookie> cookies = bankRepository.findById(id).orElseThrow(() -> new BankNotFoundException(id)).getCookies();
        return new Response(cookies);
    }

    @GetMapping("/{cookieId}")
    public Cookie findById(@PathVariable Integer id, @PathVariable Integer cookieId) {
        Bank bank = bankRepository.findById(id).orElseThrow(() -> new BankNotFoundException(id));
        Cookie cookie = bank.getCookie(cookieId).orElseThrow(() -> new CookieNotFoundException(cookieId));
        return cookie;
    }

    @PostMapping
    public ResponseEntity create(@PathVariable Integer id, @RequestBody Cookie cookie) {
        cookie = bankService.addCookie(id, cookie);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("{cookieId}").buildAndExpand(cookie.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping
    public void update(@PathVariable Integer id, @RequestBody Cookie cookie) {
        Bank bank = bankRepository.findById(id).orElseThrow(() -> new BankNotFoundException(id));
        bank.addCookie(cookie);
        bankRepository.save(bank);
    }

    @DeleteMapping("/{cookieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer cookieId, @PathVariable Integer id) {
        Bank bank = bankRepository.findById(id).orElseThrow(() -> new BankNotFoundException(id));
        Cookie cookie = bank.getCookie(cookieId).orElseThrow(() -> new CookieNotFoundException(cookieId));
        bank.removeCookie(cookie);
        bankRepository.save(bank);
    }

}
