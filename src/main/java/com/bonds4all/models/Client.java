package com.bonds4all.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
import javax.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@JsonPropertyOrder({"id", "givenName", "familyName", "otherPersonalData"})
public class Client extends ResourceSupport {
    @Id
    @GeneratedValue
    @Column(name="id")
    @JsonProperty("id")
    private Long clientId;
    private String givenName;
    private String familyName;
    private String otherPersonalData;

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    @ToString.Exclude
    private List<Bond> bonds;

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    @ToString.Exclude
    private List<Record> records;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    @JsonCreator
    public Client(@JsonProperty("givenName") String givenName, @JsonProperty("familyName") String familyName, @JsonProperty("otherPersonalData") String otherPersonalData) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.otherPersonalData = otherPersonalData;
        log.info("Creating " + this);
    }
}
