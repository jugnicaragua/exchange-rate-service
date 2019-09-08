package ni.org.jug.exchangerate.rest;

import ni.org.jug.exchangerate.exception.CommercialBankExchangeRateNotFoundException;
import ni.org.jug.exchangerate.exception.InvalidRangeException;
import ni.org.jug.exchangerate.model.CommercialBankExchangeRate;
import ni.org.jug.exchangerate.repository.CommercialBankExchangeRateRepository;
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
 * @author aalaniz
 */
@RestController
@RequestMapping("/api/commercialBankExchangeRates")
public class CommercialBankExchangeRateController {

    @Autowired
    CommercialBankExchangeRateRepository commercialBankExchangeRateRepository;

    @GetMapping
    public Response findAll() {
        Iterable itr = commercialBankExchangeRateRepository.findAll();
        return new Response(itr);
    }

    @GetMapping("/{id}")
    public CommercialBankExchangeRate findById(@PathVariable Integer id) {
        return commercialBankExchangeRateRepository.findById(id).orElseThrow(() -> new CommercialBankExchangeRateNotFoundException(id));
    }

    @GetMapping("/date/{date:\\d{4}\\-\\d{2}\\-\\d{2}}")
    public Response findByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<CommercialBankExchangeRate> exchangeRates = commercialBankExchangeRateRepository.findByDate(date);
        return new Response(exchangeRates);
    }

    @GetMapping("/date/today")
    public Response today() {
        List<CommercialBankExchangeRate> exchangeRates = commercialBankExchangeRateRepository.findByDate(LocalDate.now());
        return new Response(exchangeRates);
    }

    @GetMapping("/date")
    public Response findByDateBetween(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        if (start.isAfter(end)) {
            throw InvalidRangeException.create(start, end);
        }
        List<CommercialBankExchangeRate> exchangeRates = commercialBankExchangeRateRepository.findByDateBetween(start, end);
        return new Response(CommercialBankDataByDate.create(exchangeRates));
    }

    @GetMapping("/period/{year}-{month}")
    public Response findByPeriod(@PathVariable int year, @PathVariable int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        List<CommercialBankExchangeRate> exchangeRates = commercialBankExchangeRateRepository.findByDateBetween(yearMonth.atDay(1),
                yearMonth.atEndOfMonth());
        return new Response(CommercialBankDataByDate.create(exchangeRates));
    }

    @GetMapping("/period/current")
    public Response findByCurrentPeriod() {
        YearMonth yearMonth = YearMonth.now();
        List<CommercialBankExchangeRate> exchangeRates = commercialBankExchangeRateRepository.findByDateBetween(yearMonth.atDay(1),
                yearMonth.atEndOfMonth());
        return new Response(CommercialBankDataByDate.create(exchangeRates));
    }

    @GetMapping("/bank/{bank}")
    public Response findByBank(@PathVariable String bank) {
        List<CommercialBankExchangeRate> exchangeRates = commercialBankExchangeRateRepository.findByBank(bank);
        return new Response(exchangeRates);
    }

    @GetMapping("/bank/{bank}/date/{date:\\d{4}\\-\\d{2}\\-\\d{2}}")
    public Response findByBankAndDate(@PathVariable String bank,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<CommercialBankExchangeRate> exchangeRates = commercialBankExchangeRateRepository.findByBankAndDate(bank, date);
        return new Response(exchangeRates);
    }

    @GetMapping("/bank/{bank}/date")
    public Response findByBankAndDateBetween(@PathVariable String bank,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        if (start.isAfter(end)) {
            throw InvalidRangeException.create(start, end);
        }
        List<CommercialBankExchangeRate> exchangeRates = commercialBankExchangeRateRepository.findByBankAndDateBetween(bank, start, end);
        return new Response(exchangeRates);
    }

    @GetMapping("/bank/{bank}/{year:\\d+}-{month:\\d+}")
    public Response findByBankAndPeriod(@PathVariable String bank, @PathVariable int year,
            @PathVariable int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        List<CommercialBankExchangeRate> exchangeRates = commercialBankExchangeRateRepository.findByBankAndDateBetween(bank,
                yearMonth.atDay(1), yearMonth.atEndOfMonth());
        return new Response(exchangeRates);
    }

}
