package ni.org.jug.exchangerate.rest;

import ni.org.jug.exchangerate.exception.CentralBankExchangeRateNotFoundException;
import ni.org.jug.exchangerate.exception.InvalidRangeException;
import ni.org.jug.exchangerate.model.CentralBankExchangeRate;
import ni.org.jug.exchangerate.repository.CentralBankExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 *
 * @author aalaniz
 */
@RestController
@RequestMapping("/api/centralBankExchangeRates")
public class CentralBankExchangeRateController {

    @Autowired
    CentralBankExchangeRateRepository centralBankExchangeRateRepository;

    @GetMapping
    public Response findAll() {
        Iterable itr = centralBankExchangeRateRepository.findAll();
        return new Response(itr);
    }

    @GetMapping("/{year:\\d+}-{month:\\d+}-{day:\\d+}")
    public CentralBankExchangeRate findById(@PathVariable int year, @PathVariable int month, @PathVariable int day) {
        Integer id = CentralBankExchangeRate.calculateId(year, month, day);
        return centralBankExchangeRateRepository.findById(id).orElseThrow(() -> new CentralBankExchangeRateNotFoundException(id));
    }

    @GetMapping("/today")
    public CentralBankExchangeRate today() {
        Integer id = CentralBankExchangeRate.calculateId(LocalDate.now());
        return centralBankExchangeRateRepository.findById(id).orElseThrow(() -> new CentralBankExchangeRateNotFoundException(id));
    }

    @GetMapping("/id")
    public Response findByIdBetween(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        Integer id1 = CentralBankExchangeRate.calculateId(start);
        Integer id2 = CentralBankExchangeRate.calculateId(end);

        if (id1.compareTo(id2) > 0) {
            throw InvalidRangeException.create(id1, id2);
        }

        List<CentralBankExchangeRate> exchangeRates = centralBankExchangeRateRepository.findByIdBetween(id1, id2);
        return new Response(exchangeRates);
    }

    @GetMapping("/{year:\\d+}-{month:\\d+}")
    public Response findByPeriod(@PathVariable int year, @PathVariable int month) {
        YearMonth period = YearMonth.of(year, month);
        List<CentralBankExchangeRate> exchangeRates = centralBankExchangeRateRepository.findByDateBetween(period.atDay(1),
                period.atEndOfMonth());
        return new Response(exchangeRates);
    }

    @GetMapping("/period")
    public Response findByPeriodBetween(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        YearMonth period1 = YearMonth.from(start);
        YearMonth period2 = YearMonth.from(end);

        if (period1.isAfter(period2)) {
            throw InvalidRangeException.create(period1, period2);
        }

        List<CentralBankExchangeRate> exchangeRates = centralBankExchangeRateRepository.findByDateBetween(period1.atDay(1),
                period2.atEndOfMonth());
        return new Response(exchangeRates);
    }

    @GetMapping("/period/current")
    public Response findByCurrentPeriod() {
        YearMonth currentPeriod = YearMonth.now();
        List<CentralBankExchangeRate> exchangeRates = centralBankExchangeRateRepository.findByDateBetween(currentPeriod.atDay(1),
                currentPeriod.atEndOfMonth());
        return new Response(exchangeRates);
    }

    @GetMapping("/period/next")
    public Response findByNextPeriod() {
        YearMonth nextPeriod = YearMonth.now().plusMonths(1);
        List<CentralBankExchangeRate> exchangeRates = centralBankExchangeRateRepository.findByDateBetween(nextPeriod.atDay(1),
                nextPeriod.atEndOfMonth());
        return new Response(exchangeRates);
    }

}
