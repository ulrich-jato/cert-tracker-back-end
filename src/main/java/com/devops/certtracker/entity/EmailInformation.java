package com.devops.certtracker.entity;

import org.springframework.context.annotation.Bean;

public class EmailInformation {
    private String recipient;
    private String subject;
    private String messageBody;

    /**
     * Default constructor.
     */
    public EmailInformation() {
    }
    /**
     * Constructor.
     * @param recipient The recipient of the email.
     * @param subject The subject of the email.
     * @param messageBody The message body of the email.
     */
    public EmailInformation(String recipient, String subject, String messageBody) {
        this.recipient = recipient;
        this.subject = subject;
        this.messageBody = messageBody;
    }
    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

}

