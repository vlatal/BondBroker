package com.bonds4all.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"message", "date"})
public class Message {
    private String message;
    private ZonedDateTime date;

    public Message(String message, ZonedDateTime date) {
        this.message = message;
        this.date = date;
    }

    public static Message createWithNow(String message) {
        return new Message(message, ZonedDateTime.now());
    }
}
