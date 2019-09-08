package ni.org.jug.exchangerate.rest;

import ni.org.jug.exchangerate.model.CommercialBankExchangeRate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author aalaniz
 */
public class CommercialBankDataByDate {

    private final LocalDate date;
    private final List<CommercialBankExchangeRate> exchangeRates;

    private CommercialBankDataByDate(LocalDate date, List<CommercialBankExchangeRate> exchangeRates) {
        this.date = date;
        this.exchangeRates = exchangeRates;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<CommercialBankExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    public static List<CommercialBankDataByDate> create(List<CommercialBankExchangeRate> exchangeRates) {
        Map<LocalDate, List<CommercialBankExchangeRate>> exchangeRateByDates = Objects.requireNonNull(exchangeRates)
                .stream()
                .collect(Collectors.groupingBy(CommercialBankExchangeRate::getDate));
        List<CommercialBankDataByDate> responseByDates = exchangeRateByDates.entrySet()
                .stream()
                .map(entry -> new CommercialBankDataByDate(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return responseByDates;
    }

}
