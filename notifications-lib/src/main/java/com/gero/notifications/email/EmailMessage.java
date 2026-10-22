package com.gero.notifications.email;

import com.gero.notifications.core.NotificationMessage;
import lombok.Builder;
import lombok.Getter;

/**
 * Immutable data transfer object representing an email notification payload.
 *
 * <p>Encapsulates standard email envelope and body parameters required by email delivery
 * providers such as SendGrid, Mailgun, or Amazon SES. Instances are constructed using
 * the Builder pattern via Lombok annotations placed on the explicit constructor.</p>
 */
@Getter
public class EmailMessage implements NotificationMessage {

    /**
     * The destination email address of the recipient (e.g., "user@example.com").
     */
    private final String recipient;

    /**
     * The header subject line summarizing the contents of the email message.
     */
    private final String subject;

    /**
     * The plain-text or HTML-formatted body content of the email message.
     */
    private final String body;

    /**
     * The originating sender email address or alias (e.g., "no-reply@platform.com").
     * Optional depending on provider-level default sender configuration.
     */
    private final String sender;

    /**
     * Constructs a new {@code EmailMessage} instance using the Builder pattern.
     *
     * @param recipient The target destination email address.
     * @param subject   The email subject line.
     * @param body      The main text or HTML message content.
     * @param sender    The originating sender email address or display alias.
     */
    @Builder
    public EmailMessage(String recipient, String subject, String body, String sender) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.sender = sender;
    }
}