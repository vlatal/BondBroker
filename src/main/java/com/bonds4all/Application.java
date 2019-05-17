package com.bonds4all;

import com.bonds4all.models.ActionType;
import com.bonds4all.models.Bond;
import com.bonds4all.models.Client;
import com.bonds4all.models.Record;
import com.bonds4all.repositories.BondRepository;
import com.bonds4all.repositories.ClientRepository;
import com.bonds4all.repositories.RecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.time.Period;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Inits database for testing purposes.
     *
     * Do not use for production.
     *
     * @param clientRepository
     * @param bondRepository
     * @param recordRepository
     * @return
     */
    @Bean
    CommandLineRunner initDatabase(ClientRepository clientRepository, BondRepository bondRepository, RecordRepository recordRepository) {
        return args -> {
            log.info("Preloading " + clientRepository.save(new Client("Jan Amos", "Komenský", "učitel národů")));
            Client client = clientRepository.save(new Client("Pavel", "Nedvěd", "fotbalista"));
            log.info("Preloaded " + client);

            Bond tmpBond = new Bond(client, BigDecimal.valueOf(10), Period.ofYears(5), BigDecimal.valueOf(10));
            Bond bond = bondRepository.save(tmpBond);
            log.info("Preloaded " + bond);

            Record tmpRecord = new Record(InetAddress.getLoopbackAddress().getHostAddress(), client, ActionType.BUY_BOND, bond);
            Record record = recordRepository.save(tmpRecord);
            log.info("Preloaded " + record);
        };
    }
}