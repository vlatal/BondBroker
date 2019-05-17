package com.bonds4all.models;

import java.math.BigDecimal;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Slf4j
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@JsonPropertyOrder({"id", "client", "interestRate", "term", "amount"})
public class Bond extends ResourceSupport {
    public static final BigDecimal DEFAULT_INTEREST_RATE = BigDecimal.valueOf(5);
    public static final Period DEFAULT_MINIMAL_TERM = Period.ofYears(5);

    @Id
    @GeneratedValue
    @Column(name="id")
    @JsonProperty("id")
    private Long bondId;

    @ManyToOne
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Client client;

    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal interestRate = DEFAULT_INTEREST_RATE;
    private Period term = DEFAULT_MINIMAL_TERM;

    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal amount;

    @OneToMany(mappedBy = "bond")
    @JsonIgnore
    @ToString.Exclude
    private List<Record> records;
    private ZonedDateTime boughtDate = ZonedDateTime.now();
    private ZonedDateTime maturityDate;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    /**
     *
     * @param client
     * @param interestRate
     * @param term
     * @param amount
     */
    @JsonCreator
    public Bond(@JsonProperty("client") Client client, @JsonProperty("interestRate") BigDecimal interestRate, @JsonProperty("term") Period term, @JsonProperty("amount") BigDecimal amount) {
        this.client = client;
        this.interestRate = interestRate;
        this.term = term;
        this.amount = amount;
        this.maturityDate = boughtDate.plus(this.term);
        log.info("Creating " + this);
    }

    public static Bond createWithDefaultInterestRate(Client client, Period term, BigDecimal amount) {
        return new Bond(client, DEFAULT_INTEREST_RATE, term, amount);
    }

    public static Bond createWithDefaultMinimalTerm(Client client, BigDecimal interestRate, BigDecimal amount) {
        return new Bond(client, interestRate, DEFAULT_MINIMAL_TERM, amount);
    }

    public static Bond createWithDefaults(Client client, BigDecimal amount) {
        return new Bond(client, DEFAULT_INTEREST_RATE, DEFAULT_MINIMAL_TERM, amount);
    }

    public void setTerm(Period termToBe) {
        int termComparison = Long.compare(termToBe.toTotalMonths(), this.term.toTotalMonths());
        switch (termComparison) {
            case 0: // Terms are equal
                // no need to do anything else
                return;

            case 1: // Term extension
                // Each term extension results in coupon decreased by 10% of its value.
                interestRate = interestRate.multiply(new BigDecimal("0.9"));
                break;

            case -1: // Shorten term
                // Shortening the term doesn’t affect the coupon.
                break;
        }
        this.term = termToBe;
        this.maturityDate = boughtDate.plus(this.term);
    }
}
