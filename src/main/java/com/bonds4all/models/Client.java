package com.bonds4all.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class Client extends ResourceSupport {
    @Id
    @GeneratedValue
    @Column(name="id")
    @JsonProperty("id")
    private Long clientId;
    private String givenName;
    private String familyName;
    @Nullable
    private String otherPersonalData;

    @JsonIgnore
    @OneToMany(mappedBy = "clientId")
    private List<Bond> bonds;

    @JsonIgnore
    @OneToMany(mappedBy = "clientId")
    private List<Record> records;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    @JsonCreator
    public Client(String givenName, String familyName, String otherPersonalData) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.otherPersonalData = otherPersonalData;
    }
}
