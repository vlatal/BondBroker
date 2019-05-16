package com.bonds4all.controllers;

import com.bonds4all.exceptions.BondBrokerException;
import com.bonds4all.exceptions.BondNotFoundException;
import com.bonds4all.models.ActionType;
import com.bonds4all.models.Bond;
import com.bonds4all.models.Client;
import com.bonds4all.repositories.BondRepository;
import com.bonds4all.repositories.ClientRepository;
import com.bonds4all.repositories.RecordRepository;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class BondController {
    public static final BigDecimal RESTRICTED_AMOUNT = BigDecimal.valueOf(1000);
    public static final int RESTRICTED_HOURS_BEGIN = 22;
    public static final int RESTRICTED_HOURS_END = 6;
    public static final int MAX_BONDS_PER_DAY_PER_CLIENT = 5;

    private final BondRepository repository;
    private final BondResourceAssembler assembler;
    private final ClientRepository clientRepository;
    private final RecordRepository recordRepository;

    BondController(BondRepository repository, BondResourceAssembler assembler, ClientRepository clientRepository, RecordRepository recordRepository) {
        this.repository = repository;
        this.assembler = assembler;
        this.clientRepository = clientRepository;
        this.recordRepository = recordRepository;
    }

    // Aggregate root
    @GetMapping("/bonds")
    Resources<Resource<Bond>> all() {
        List<Resource<Bond>> bonds = repository.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(bonds,
                linkTo(methodOn(BondController.class).all()).withSelfRel());
    }

    @PostMapping("/bonds")
    @Transactional
    Resource<Bond> newBond(HttpServletRequest httpRequest, @RequestBody Bond newBond) {
        InetAddress ip;
        try {
            // reliable
            ip = InetAddress.getByName(httpRequest.getRemoteAddr());
        } catch (UnknownHostException e) {
            // won't happen
            ip = InetAddress.getLoopbackAddress();
        }

        isEligibleToBuy(ZonedDateTime.now(), newBond.getClientId(), ip, newBond.getAmount());

        Bond bond = repository.save(newBond);
        return assembler.toResource(bond);
    }

    @PutMapping("/{id}")
    Bond replaceBond(@RequestBody Bond newBond, @PathVariable Long id) {

        return repository.findById(id)
                .map(Bond -> {
                    Bond.setAmount(newBond.getAmount());
                    Bond.setBoughtDate(newBond.getBoughtDate());
                    Bond.setClientId(newBond.getClientId());
                    Bond.setInterestRate(newBond.getInterestRate());
                    Bond.setMaturityDate(newBond.getMaturityDate());
                    Bond.setCreatedAt(newBond.getCreatedAt());
                    Bond.setTerm(newBond.getTerm());
                    return repository.save(Bond);
                })
                .orElseGet(() -> {
                    newBond.setBondId(id);
                    return repository.save(newBond);
                });
    }

    @GetMapping("/clients/{clientId}/bonds")
    Resources<Resource<Bond>> clientBonds(@PathVariable Long clientId) {
        List<Resource<Bond>> bonds = repository.findAll().stream()
                .filter(bond -> bond.getClientId() == clientId)
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(bonds,
                linkTo(methodOn(BondController.class).all()).withSelfRel());
    }

    // Single item
    @GetMapping("/bonds/{id}")
    Resource<Bond> one(@PathVariable Long id) {

        Bond bond = repository.findById(id)
                .orElseThrow(() -> new BondNotFoundException(id));

        return assembler.toResource(bond);
    }

    /**
     * Determines whether the Client is eligible to buy Bond.
     *
     * The application follows regulations that have to be validated in order to preserve fair competition and
     * to prevent potentially illegal operations. A bond can’t be sold if:
     *  * the application is made between 10:00 PM and 06:00 AM with an amount higher than 1000
     *  * reached max number of sold bonds (e.g. 5) per day from a single IP address
     *
     * @param now
     * @param clientId
     * @param ip
     * @param amount
     * @return whether Client is eligible to buy Bond
     */
    private boolean isEligibleToBuy(ZonedDateTime now, Long clientId, InetAddress ip, BigDecimal amount) {
        ZonedDateTime today = now.truncatedTo(ChronoUnit.DAYS);
        String notEligibleMsg = "Client is not eligible to buy Bond.";

        // Is the application made between 10:00 PM and 06:00 AM _with_ an amount _higher than_ 1000?
        if (amount.compareTo(RESTRICTED_AMOUNT) > 0 && (
                now.getHour() >= RESTRICTED_HOURS_BEGIN ||
                now.getHour() < RESTRICTED_HOURS_END)) {

            throw new BondBrokerException(
                    String.format(
                            notEligibleMsg +
                            " Bond sells with Amounts above %d are restricted in between %d and %d hours." +
                            " Please try again after restriction period or with lower amount.",
                            RESTRICTED_AMOUNT, RESTRICTED_HOURS_BEGIN, RESTRICTED_HOURS_END));
        }

        try (Long alreadyBoughtTodayCount = recordRepository.streamAllForClientAndIp(clientId, ip)
                .filter(record -> record.getCreatedAt().isAfter(today)
                        && record.getActionType().equals(ActionType.SELL_BOND))
                .count()) {
            return alreadyBoughtTodayCount;
        }
        // Has reached max number of sold bonds (e.g. 5) per day from a single IP address?
        Long alreadyBoughtTodayCount = recordRepository.streamAllForClientAndIp(clientId, ip)
                .filter(record -> record.getCreatedAt().isAfter(today)
                                && record.getActionType().equals(ActionType.SELL_BOND))
                .count();

        if (alreadyBoughtTodayCount >= MAX_BONDS_PER_DAY_PER_CLIENT) {
            throw new BondBrokerException(
                    String.format(
                            notEligibleMsg +
                                    " Bond sells are limited to %d bonds per day from single IP address." +
                                    " Please try again the very next day.",
                            MAX_BONDS_PER_DAY_PER_CLIENT));
        }

        // Eligible
        return true;
    }
}
