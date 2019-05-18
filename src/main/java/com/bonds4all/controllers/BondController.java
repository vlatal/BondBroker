package com.bonds4all.controllers;

import com.bonds4all.exceptions.BondBrokerException;
import com.bonds4all.exceptions.BondNotFoundException;
import com.bonds4all.exceptions.ClientNotFoundException;
import com.bonds4all.models.ActionType;
import com.bonds4all.models.Bond;
import com.bonds4all.models.Client;
import com.bonds4all.models.Record;
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
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class BondController {
    public static final BigDecimal RESTRICTED_AMOUNT = BigDecimal.valueOf(1000);
    public static final int RESTRICTED_HOURS_BEGIN = 1;
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

    /**
     * Used for creating new Bond.
     *
     * @param httpRequest
     * @param newBond
     * @param clientId
     * @return
     */
    @PostMapping("/clients/{clientId}/bonds")
    Resource<Bond> newBond(HttpServletRequest httpRequest, @RequestBody Bond newBond,  @PathVariable("clientId") long clientId) {

        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));

        isEligibleToBuy(ZonedDateTime.now(), clientId, httpRequest.getRemoteAddr(), newBond.getAmount());

        newBond.setClient(client);
        Bond bond = repository.save(newBond);

        // Add change to history
        recordRepository.save(new Record(httpRequest.getRemoteAddr(), client, ActionType.BUY_BOND, bond));

        return assembler.toResource(bond);
    }

    /**
     * Replaces Bond object on specified id.
     *
     * Client should be able to adjust the term of his bond.
     * Each term extension results in coupon decreased by 10% of its value.
     * Shortening the term doesn’t affect the coupon.
     * 
     * Only Term may be adjusted.
     *
     * @param newBond
     * @param id
     * @return
     */
    @PutMapping("/bonds/{id}")
    Resource<Bond> replaceBond(HttpServletRequest httpRequest, @RequestBody Bond newBond, @PathVariable long id) {

        Bond savedBond = repository.findById(id)
                .map(bond -> {
                    //bond.setAmount(newBond.getAmount());
                    //bond.setBoughtDate(newBond.getBoughtDate());
                    //bond.setClient(newBond.getClient());
                    //bond.setInterestRate(newBond.getInterestRate());
                    //bond.setMaturityDate(newBond.getMaturityDate());
                    //bond.setCreatedAt(newBond.getCreatedAt());
                    bond.setTerm(newBond.getTerm());
                    return repository.save(bond);
                })
                .orElseGet(() -> {
                    newBond.setBondId(id);
                    return repository.save(newBond);
                });

        // Add change to history
        recordRepository.save(new Record(httpRequest.getRemoteAddr(), savedBond.getClient(), ActionType.ADJUST_TERM, savedBond));

        return assembler.toResource(savedBond);
    }

    @GetMapping("/clients/{clientId}/bonds")
    Resources<Resource<Bond>> clientBonds(@PathVariable long clientId) {
        List<Resource<Bond>> bonds = repository.findAll().stream()
                .filter(bond -> bond.getClient().getClientId().longValue() == clientId)
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(bonds,
                linkTo(methodOn(BondController.class).all()).withSelfRel());
    }

    // Single item
    @GetMapping("/bonds/{id}")
    Resource<Bond> one(@PathVariable long id) {

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
    private boolean isEligibleToBuy(ZonedDateTime now, long clientId, String ip, BigDecimal amount) {
        ZonedDateTime today = now.truncatedTo(ChronoUnit.DAYS);
        String notEligibleMsg = "Client is not eligible to buy Bond.";

        // Is the application made between 10:00 PM and 06:00 AM _with_ an amount _higher than_ 1000?
        if (amount.compareTo(RESTRICTED_AMOUNT) > 0 && isRestrictedTime(now, RESTRICTED_HOURS_BEGIN, RESTRICTED_HOURS_END)) {

            throw new BondBrokerException(
                    String.format(
                            notEligibleMsg +
                            " Bond sells with Amounts above %d are restricted in between %d and %d hours." +
                            " Please try again after restriction period or with lower amount.",
                            RESTRICTED_AMOUNT.longValue(), RESTRICTED_HOURS_BEGIN, RESTRICTED_HOURS_END));
        }

        // Has reached max number of sold bonds (e.g. 5) per day from a single IP address?
        Long alreadyBoughtTodayCount = recordRepository.findByClient_ClientIdAndIp(clientId, ip).stream()
                .filter(record ->
                        record.getCreatedAt().isAfter(today) &&
                        record.getActionType().equals(ActionType.BUY_BOND))
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

    /**
     * Tells whether it is restricted time.
     *
     * @param now
     * @return
     */
    public static boolean isRestrictedTime(ZonedDateTime now, int begin, int end) {
        int hour = now.getHour();
        if (begin > end) {
            return (hour >= begin || hour < end);
        } else {
            return (hour >= begin && hour < end);
        }
    }
}
