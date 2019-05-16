package com.bonds4all;

import com.bonds4all.models.Bond;
import com.bonds4all.repositories.BondRepository;
import com.bonds4all.repositories.ClientRepository;
import com.bonds4all.models.Client;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(ClientRepository clientRepository, BondRepository bondRepository) {
        return args -> {
            log.info("Preloading " + clientRepository.save(new Client("Jan Amos", "Komenský", "učitel národů")));
            Client tmp = clientRepository.save(new Client("Pavel", "Nedvěd", "fotbalista"));
            log.info("Preloading " + tmp);
            log.info(" * bond");
            List<Bond> bonds = new ArrayList<Bond>();
            Bond bond = new Bond(tmp.getClientId(), BigDecimal.valueOf(10), Period.ofYears(5), BigDecimal.valueOf(10));
            //bonds.add(bond);
            //tmp.setBonds(bonds);
            //log.info("Preloading " + tmp);
            //clientRepository.save(tmp);
            //log.info("Preloading " + bond);
            //bondRepository.save(bond);
            //bondRepository.save(new Bond(tmp.getClientId(), BigDecimal.valueOf(10), Period.ofYears(5), BigDecimal.valueOf(10)));
            //log.info("Preloading " + bondRepository.save(new Bond(tmp.getClientId(), BigDecimal.valueOf(10), Period.ofYears(5), BigDecimal.valueOf(10))));

        };
    }
}