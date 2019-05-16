package com.bonds4all.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.net.InetAddress;
import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class Record extends ResourceSupport {
    @Id
    @GeneratedValue
    @Column(name="id")
    @JsonProperty("id")
    private Long recordId;
    private InetAddress ip;
    @ManyToOne(targetEntity = Client.class)
    private Long clientId;
    private ActionType actionType;
    @ManyToOne(targetEntity = Bond.class)
    private Long bondId;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @JsonCreator
    public Record(InetAddress ip, Long clientId, ActionType actionType, Long bondId) {
        this.ip = ip;
        this.clientId = clientId;
        this.actionType = actionType;
        this.bondId = bondId;
    }
}
