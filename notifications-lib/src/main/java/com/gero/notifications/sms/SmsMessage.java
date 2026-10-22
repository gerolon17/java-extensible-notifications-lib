package com.gero.notifications.sms;

import com.gero.notifications.core.NotificationMessage;
import lombok.Builder;
import lombok.Getter;

/**
 * Immutable data transfer object representing a Short Message Service (SMS) notification payload.
 *
 * <p>Encapsulates destination phone numbering and plain-text message contents required
 * by SMS aggregators and telecom gateways (e.g., Twilio, AWS SNS, MessageBird). Instances
 * are constructed using the Builder pattern applied directly to the explicit constructor.</p>
 */
@Getter
public class SmsMessage implements NotificationMessage {

    /**
     * The destination recipient phone number, expected in the international
     * E.164 format (e.g., "+12025550199").
     */
    private final String recipient;

    /**
     * The plain-text message content to be transmitted via SMS.
     */
    private final String content;

    /**
     * Constructs a new {@code SmsMessage} instance using the Builder pattern.
     *
     * @param recipient The target recipient mobile phone number.
     * @param content   The plain-text message body to send.
     */
    @Builder
    public SmsMessage(String recipient, String content) {
        this.recipient = recipient;
        this.content = content;
    }
}