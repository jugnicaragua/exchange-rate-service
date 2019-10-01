package ni.org.jug.exchangerate;

import ni.org.jug.exchangerate.logic.ExchangeRateImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories("ni.org.jug.exchangerate.repository")
@EntityScan("ni.org.jug.exchangerate.model")
@EnableScheduling
public class ExchangeRateApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateApplication.class);

    private static final int MAX_COUNT = 3;

    public static void main(String[] args) {
        SpringApplication.run(ExchangeRateApplication.class, args);
    }

    @Bean
    public CommandLineRunner importInitialData(ExchangeRateImporter importer) {
        return args -> {
            int count = 1;
            boolean error = false;

            do {
                try {
                    importer.importHistoricalCentralBankDataUntilNow();
                    importer.importListOfSupportedBanks();
                } catch (IllegalArgumentException ex) {
                    LOGGER.debug("Ocurrio un error durante la importacion de los datos del BCN y Catalogo de Bancos", ex);
                    LOGGER.debug("Importacion de Datos: intento {} de {}", count, MAX_COUNT);
                    error = true;
                }
            } while (error && ++count <= MAX_COUNT);

            try {
                importer.importCentralBankDataForNextPeriod();
            } catch (IllegalArgumentException ex) {
                LOGGER.error("Ocurrio un error durante la importacion de las tasas del BCN del proximo periodo", ex);
            }

            try {
                importer.importCurrentCommercialBankData();
            } catch (IllegalArgumentException ex) {
                LOGGER.error("Ocurrio un error durante la importacion de la compra/venta del dia de hoy", ex);
            }
        };
    }

}
