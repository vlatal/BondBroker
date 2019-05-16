package com.bonds4all.models;

import java.math.BigDecimal;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class Bond extends ResourceSupport {
    public static final BigDecimal DEFAULT_INTEREST_RATE = BigDecimal.valueOf(5);
    public static final Period DEFAULT_MINIMAL_TERM = Period.ofYears(5);

    @Id
    @GeneratedValue
    @Column(name="id")
    @JsonProperty("id")
    private Long bondId;
    @ManyToOne(targetEntity = Client.class)
    private Long clientId;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal interestRate = DEFAULT_INTEREST_RATE;
    private Period term = DEFAULT_MINIMAL_TERM;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal amount;
    @OneToMany(mappedBy = "bondId")
    @JsonIgnore
    private List<Record> records;
    private ZonedDateTime boughtDate = ZonedDateTime.now();
    private ZonedDateTime maturityDate;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    /**
     *
     * @param clientId
     * @param interestRate
     * @param term
     * @param amount
     */
    @JsonCreator
    public Bond(Long clientId, BigDecimal interestRate, Period term, BigDecimal amount) {
        this.clientId = clientId;
        this.interestRate = interestRate;
        this.term = term;
        this.amount = amount;
        this.maturityDate = boughtDate.plus(this.term);
    }

    public static Bond createWithDefaultInterestRate(Long clientId, Period term, BigDecimal amount) {
        return new Bond(clientId, DEFAULT_INTEREST_RATE, term, amount);
    }

    public static Bond createWithDefaultMinimalTerm(Long clientId, BigDecimal interestRate, BigDecimal amount) {
        return new Bond(clientId, interestRate, DEFAULT_MINIMAL_TERM, amount);
    }

    public static Bond createWithDefaults(Long clientId, BigDecimal amount) {
        return new Bond(clientId, DEFAULT_INTEREST_RATE, DEFAULT_MINIMAL_TERM, amount);
    }

    public void setTerm(Period termToBe) {
        int termComparison = Long.compare(termToBe.toTotalMonths(), this.term.toTotalMonths());
        switch (termComparison) {
            case 0: // Terms are equal
                // no need to do anything else
                return;

            case 1: // Term extension
                // Each term extension results in coupon decreased by 10% of its value.
                // TODO decrease coupon by 10% of its value
                break;

            case -1: // Shorten term
                // Shortening the term doesn’t affect the coupon.
                break;
        }
        this.term = termToBe;
        this.maturityDate = boughtDate.plus(this.term);
    }
}
