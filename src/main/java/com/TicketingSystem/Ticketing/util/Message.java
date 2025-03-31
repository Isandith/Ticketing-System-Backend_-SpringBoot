package com.TicketingSystem.Ticketing.util;

public class Message {
    private String content;
    private String sender;

    public Message(String customer, String s) {
        this.content = s;
        this.sender = customer;
    }

    public Message(String customer) {
        this.sender = customer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
