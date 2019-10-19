package ni.org.jug.exchangerate.logic;

import ni.jug.exchangerate.ExchangeRateClient;
import ni.jug.exchangerate.cb.CommercialBank;
import ni.jug.exchangerate.cb.ExchangeRateTrade;
import ni.jug.exchangerate.cb.ExecutionContext;
import ni.jug.exchangerate.ncb.MonthlyExchangeRate;
import ni.jug.exchangerate.ncb.NiCentralBankExchangeRateClient;
import ni.org.jug.exchangerate.model.Bank;
import ni.org.jug.exchangerate.model.CentralBankExchangeRate;
import ni.org.jug.exchangerate.model.CommercialBankExchangeRate;
import ni.org.jug.exchangerate.model.Cookie;
import ni.org.jug.exchangerate.model.Currency;
import ni.org.jug.exchangerate.repository.BankRepository;
import ni.org.jug.exchangerate.repository.CentralBankExchangeRateRepository;
import ni.org.jug.exchangerate.repository.CommercialBankExchangeRateRepository;
import ni.org.jug.exchangerate.repository.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 * @author aalaniz
 */
@Service
@Transactional
public class ExchangeRateImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateImporter.class);

    @Autowired
    CentralBankExchangeRateRepository centralBankExchangeRateRepository;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    BankRepository bankRepository;

    @Autowired
    CommercialBankExchangeRateRepository commercialBankExchangeRateRepository;

    private Currency dollar;

    @PostConstruct
    public void onInit() {
        dollar = currencyRepository.findDollar();
    }

    private CentralBankExchangeRate centralBankExchangeRateOf(LocalDate date, BigDecimal amount) {
        CentralBankExchangeRate centralBankExchangeRate = new CentralBankExchangeRate();
        centralBankExchangeRate.setCurrency(dollar);
        centralBankExchangeRate.setDate(date);
        centralBankExchangeRate.setAmount(amount);
        centralBankExchangeRate.calculateId();
        return centralBankExchangeRate;
    }

    private boolean centralBankDataImported(YearMonth startPeriod, YearMonth endPeriod) {
        LocalDate start = startPeriod.atDay(1);
        LocalDate end = endPeriod.atEndOfMonth();

        long elapsedDays = ChronoUnit.DAYS.between(start, end) + 1;
        Long recordCount = centralBankExchangeRateRepository.countByDateBetween(start, end);

        boolean imported = elapsedDays == recordCount;

        if (imported) {
            LOGGER.info("BCN - {} tasas de cambio desde {} hasta {} fueron importadas en una ejecucion previa", elapsedDays, start, end);
        }

        return imported;
    }

    private boolean centralBankDataImported(YearMonth period) {
        return centralBankDataImported(period, period);
    }

    public void importHistoricalCentralBankDataUntilNow() {
        YearMonth processingPeriod = YearMonth.of(NiCentralBankExchangeRateClient.MINIMUM_YEAR, 1);
        YearMonth endPeriod = YearMonth.now();

        if (!centralBankDataImported(processingPeriod, endPeriod)) {
            ExchangeRateClient client = new ExchangeRateClient();
            while (processingPeriod.compareTo(endPeriod) <= 0) {
                LOGGER.info("BCN - Importando periodo {}", processingPeriod);

                MonthlyExchangeRate monthlyExchangeRate = client.getOfficialMonthlyExchangeRate(processingPeriod);
                for (Map.Entry<LocalDate, BigDecimal> exchangeRate : monthlyExchangeRate) {
                    CentralBankExchangeRate centralBankExchangeRate = centralBankExchangeRateOf(exchangeRate.getKey(),
                            exchangeRate.getValue());
                    centralBankExchangeRateRepository.save(centralBankExchangeRate);
                }

                processingPeriod = processingPeriod.plusMonths(1);
            }
        }
    }

    public void importListOfSupportedBanks() {
        Map<String, Bank> banks = StreamSupport.stream(bankRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(bank -> bank.getDescription().getShortDescription(), Function.identity()));

        for (CommercialBank commercialBank : ExchangeRateClient.commercialBanks()) {
            Bank bank;

            if (banks.containsKey(commercialBank.getId())) {
                LOGGER.info("Banco Comercial {} - Actualizando catalogo", commercialBank.getId());

                bank = banks.get(commercialBank.getId());
                bank.getDescription().setDescription(commercialBank.getDescription());
                bank.setUrl(commercialBank.getUrl());
                bank.setActive(Boolean.TRUE);
            } else {
                LOGGER.info("Banco Comercial {} - Importando catalogo", commercialBank.getId());

                bank = new Bank();
                bank.getDescription().setShortDescription(commercialBank.getId());
                bank.getDescription().setDescription(commercialBank.getDescription());
                bank.setUrl(commercialBank.getUrl());
                bank.setActive(Boolean.TRUE);
                bankRepository.save(bank);
            }
        }

        List<String> supportedBanks = ExchangeRateClient.commercialBanks().stream()
                .map(CommercialBank::getId)
                .collect(Collectors.toList());

        for (Map.Entry<String, Bank> bankEntry : banks.entrySet()) {
            if (!supportedBanks.contains(bankEntry.getKey())) {
                LOGGER.info("Inactivando Banco Comercial {} - La libreria de scraping ya no posee el scraper de este banco",
                        bankEntry.getKey());

                Bank bank = bankEntry.getValue();
                bank.setActive(Boolean.FALSE);
            }
        }
    }

    @Scheduled(cron = "0 15 6,16,22 21-31 * ?")
    public void importCentralBankDataForNextPeriod() {
        YearMonth nextPeriod = YearMonth.now().plusMonths(1);

        if (!centralBankDataImported(nextPeriod)) {
            ExchangeRateClient client = new ExchangeRateClient();
            MonthlyExchangeRate monthlyExchangeRate = client.getOfficialMonthlyExchangeRate(nextPeriod);

            LOGGER.info("BCN - Importando proximo periodo {}", nextPeriod);

            for (Map.Entry<LocalDate, BigDecimal> exchangeRate : monthlyExchangeRate) {
                CentralBankExchangeRate centralBankExchangeRate = centralBankExchangeRateOf(exchangeRate.getKey(),
                        exchangeRate.getValue());
                centralBankExchangeRateRepository.save(centralBankExchangeRate);
            }
        }
    }

    @Scheduled(cron = "15 0 4,6,16,20 * * *")
    public void importCurrentCommercialBankData() {
        Map<String, Bank> banks = StreamSupport.stream(bankRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(b -> b.getDescription().getShortDescription(), Function.identity()));

        Map<String, CommercialBankExchangeRate> importedBanks = commercialBankExchangeRateRepository.findByDate(LocalDate.now())
                .stream()
                .collect(Collectors.toMap(c -> c.getBank().getDescription().getShortDescription(), Function.identity()));

        for (Map.Entry<String, Bank> bankEntry : banks.entrySet()) {
            for (Cookie cookie : bankEntry.getValue().getCookies()) {
                ExecutionContext.getInstance().addOrReplaceCookie(bankEntry.getKey(), cookie.getName(), cookie.getValue());
            }
        }

        ExchangeRateClient client = new ExchangeRateClient();
        for (ExchangeRateTrade trade : client.commercialBanktrades()) {
            CommercialBankExchangeRate cbExchangeRate;

            if (!importedBanks.containsKey(trade.bank())) {
                LOGGER.info("Banco Comercial - Importando los datos de compra/venta de {}", trade.bank());

                cbExchangeRate = new CommercialBankExchangeRate();
                cbExchangeRate.setCurrency(dollar);
                cbExchangeRate.setBank(banks.get(trade.bank()));
                cbExchangeRate.setDate(LocalDate.now());
                cbExchangeRate.setSell(trade.sell());
                cbExchangeRate.setBuy(trade.buy());
                cbExchangeRate.setBestSellPrice(trade.isBestSellPrice());
                cbExchangeRate.setBestBuyPrice(trade.isBestBuyPrice());

                commercialBankExchangeRateRepository.save(cbExchangeRate);
            } else {
                LOGGER.info("Banco Comercial - Actualizando los datos de compra/venta de {}", trade.bank());

                cbExchangeRate = importedBanks.get(trade.bank());
                cbExchangeRate.setSell(trade.sell());
                cbExchangeRate.setBuy(trade.buy());
                cbExchangeRate.setBestSellPrice(trade.isBestSellPrice());
                cbExchangeRate.setBestBuyPrice(trade.isBestBuyPrice());
            }
        }
    }

}
