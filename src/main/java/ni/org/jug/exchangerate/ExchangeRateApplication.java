package ni.org.jug.exchangerate;

import ni.jug.exchangerate.ExchangeRateException;
import ni.org.jug.exchangerate.logic.ExchangeRateImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;

@SpringBootApplication
@EnableJpaRepositories("ni.org.jug.exchangerate.repository")
@EntityScan("ni.org.jug.exchangerate.model")
@EnableScheduling
@EnableAsync
public class ExchangeRateApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateApplication.class);

    private static final int MAX_RETRY_COUNT = 3;

    @Autowired
    ExchangeRateImporter importer;

    public static void main(String[] args) {
        SpringApplication.run(ExchangeRateApplication.class, args);
    }

    @Bean
    public CommandLineRunner importInitialData() {
        return args -> {
            doImportCentralBankData();
            doImportCommercialBankData();
        };
    }

    private void doImportCentralBankData() {
        int count = 1;
        boolean error = true;

        do {
            try {
                importer.importHistoricalCentralBankDataUntilNow();
                error = false;
            } catch (ExchangeRateException ex) {
                LOGGER.debug("Ocurrio un error durante la importacion de los datos historicos del BCN", ex);
                LOGGER.debug("Importacion de Datos: intento {} de {}", count, MAX_RETRY_COUNT);
            }
        } while (error && ++count <= MAX_RETRY_COUNT);

        LocalDate now = LocalDate.now();
        LocalDate referenceDate = now.withDayOfMonth(25);
        if (now.compareTo(referenceDate) >= 0) {
            try {
                importer.importCentralBankDataForNextPeriod();
            } catch (ExchangeRateException ex) {
                LOGGER.error("Ocurrio un error durante la importacion de las tasas del BCN del proximo periodo", ex);
            }
        }
    }

    private void doImportCommercialBankData() {
        importer.importListOfSupportedBanks();

        try {
            importer.importCurrentCommercialBankData();
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Ocurrio un error durante la importacion de la compra/venta del dia de hoy [{}]", LocalDate.now());
        }
    }
}
