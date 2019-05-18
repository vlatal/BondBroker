package com.bonds4all.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.ZonedDateTime;

@Slf4j
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@JsonPropertyOrder({"id", "client", "bond", "actionType", "ip"})
public class Record extends ResourceSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    @JsonProperty("id")
    private Long recordId;
    private String ip;

    @ManyToOne
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Client client;
    private ActionType actionType;

    @ManyToOne
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Bond bond;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @JsonCreator
    public Record(@JsonProperty("ip") String ip, @JsonProperty("client") Client client, @JsonProperty("actionType") ActionType actionType, @JsonProperty("bond") Bond bond) {
        this.ip = ip;
        this.client = client;
        this.actionType = actionType;
        this.bond = bond;
        log.info("Creating " + this);
    }
}
