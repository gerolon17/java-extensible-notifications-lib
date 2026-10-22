package com.gero.notifications.email;

import com.gero.notifications.core.NotificationChannel;
import com.gero.notifications.core.NotificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Concrete implementation of {@link NotificationChannel} targeting the SendGrid Email API.
 *
 * <p>Simulates upstream HTTP communication with SendGrid's v3 Mail Send endpoints.
 * Operates without external enterprise framework couplings, configuring authentication
 * credentials strictly in pure Java code via constructor injection.</p>
 *
 * <p>Validates destination parameters locally before simulating network latency,
 * authorization headers, and provider response envelopes.</p>
 */
public class SendGridEmailChannel implements NotificationChannel<EmailMessage> {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailChannel.class);

    private final String apiKey;

    /**
     * Initializes the SendGrid channel with provider authentication credentials.
     *
     * @param apiKey The SendGrid API Bearer token required for upstream request authorization.
     * @throws IllegalArgumentException if {@code apiKey} is {@code null} or contains only whitespace.
     */
    public SendGridEmailChannel(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("SendGrid API key cannot be null or empty");
        }
        this.apiKey = apiKey;
    }

    /**
     * Simulates the transmission of an {@link EmailMessage} via the SendGrid REST API.
     *
     * <p>Performs basic synthetic validation on the recipient's format, writes simulated
     * network operations to the SLF4J logging subsystem, and generates an upstream tracking
     * identifier mimicking SendGrid's message format.</p>
     *
     * @param message The email payload containing recipient, subject, and content body.
     * @return A {@link NotificationResult} encapsulating successful delivery with a generated message ID,
     *         or a failure result containing the diagnostic error reason.
     */
    @Override
    public NotificationResult send(EmailMessage message) {
        try {
            logger.info("[{}] Authenticating request using API Key...", getProviderName());

            // Validate email address format
            if (message.getRecipient() == null || !message.getRecipient().contains("@")) {
                return NotificationResult.failure("Invalid email format for recipient: " + message.getRecipient());
            }

            // Simulate upstream network dispatch
            logger.info("[{}] Sending email to [{}] with subject [{}]", 
                    getProviderName(), message.getRecipient(), message.getSubject());

            // Simulate upstream response envelope with unique message identifier
            String simulatedMessageId = "sg.msg." + UUID.randomUUID().toString();
            logger.info("[{}] Email dispatched successfully. Generated Message ID: {}", 
                    getProviderName(), simulatedMessageId);

            return NotificationResult.success(simulatedMessageId);

        } catch (Exception e) {
            logger.error("[{}] Unexpected error during email dispatch", getProviderName(), e);
            return NotificationResult.failure("Internal provider error: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return The constant provider identifier {@code "EMAIL_SENDGRID"}.
     */
    @Override
    public String getProviderName() {
        return "EMAIL_SENDGRID";
    }
}