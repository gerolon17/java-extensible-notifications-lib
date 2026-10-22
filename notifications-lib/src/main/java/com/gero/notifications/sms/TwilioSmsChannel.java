package com.gero.notifications.sms;

import com.gero.notifications.core.NotificationChannel;
import com.gero.notifications.core.NotificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Concrete implementation of {@link NotificationChannel} targeting the Twilio Programmable SMS API.
 *
 * <p>Simulates downstream HTTP requests to Twilio's Messages resource. Provider credentials
 * (Account SID, Auth Token, and originating phone number) are supplied programmatically
 * via constructor injection without relying on external configuration files or Spring contexts.</p>
 *
 * <p>Enforces local structural validation ensuring destination phone numbers conform to the
 * international E.164 telecommunications standard before logging the simulated dispatch and
 * generating an upstream Message SID.</p>
 */
public class TwilioSmsChannel implements NotificationChannel<SmsMessage> {

    private static final Logger logger = LoggerFactory.getLogger(TwilioSmsChannel.class);

    private final String accountSid;
    private final String authToken;
    private final String senderPhoneNumber;

    /**
     * Constructs a new {@code TwilioSmsChannel} initialized with explicit provider credentials.
     *
     * @param accountSid        The Twilio Account SID used as the API username for HTTP Basic Auth.
     * @param authToken         The Twilio Auth Token used as the API password for authentication.
     * @param senderPhoneNumber The registered Twilio phone number or shortcode from which SMS messages originate.
     * @throws IllegalArgumentException if {@code accountSid} or {@code authToken} is {@code null} or blank.
     */
    public TwilioSmsChannel(String accountSid, String authToken, String senderPhoneNumber) {
        if (accountSid == null || accountSid.isBlank() || authToken == null || authToken.isBlank()) {
            throw new IllegalArgumentException("Twilio credentials cannot be null or empty");
        }
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.senderPhoneNumber = senderPhoneNumber;
    }

    /**
     * Simulates transmitting an {@link SmsMessage} through Twilio's messaging infrastructure.
     *
     * <p>Validates destination phone number format (requiring a leading {@code '+'} for E.164 compliance)
     * and content payload presence. Logs delivery details and outputs a synthesized 34-character
     * Twilio Message SID prefixed with {@code "SM"}.</p>
     *
     * @param message The SMS notification payload containing recipient phone number and text content.
     * @return A {@link NotificationResult} indicating whether delivery succeeded with an upstream SID,
     *         or failed with a diagnostic error message.
     */
    @Override
    public NotificationResult send(SmsMessage message) {
        try {
            logger.info("[{}] Authenticating account SID: {}", getProviderName(), accountSid);

            // Enforce E.164 format and content constraints
            if (message.getRecipient() == null || !message.getRecipient().startsWith("+")) {
                return NotificationResult.failure("Invalid phone number format. Must use E.164 format starting with '+'");
            }

            if (message.getContent() == null || message.getContent().isBlank()) {
                return NotificationResult.failure("SMS content cannot be empty");
            }

            logger.info("[{}] Dispatching SMS from [{}] to [{}] with payload: \"{}\"",
                    getProviderName(), senderPhoneNumber, message.getRecipient(), message.getContent());

            String simulatedSid = "SM" + UUID.randomUUID().toString().replace("-", "");
            logger.info("[{}] SMS dispatched successfully. SID: {}", getProviderName(), simulatedSid);

            return NotificationResult.success(simulatedSid);

        } catch (Exception e) {
            logger.error("[{}] Error dispatching SMS", getProviderName(), e);
            return NotificationResult.failure("Twilio delivery failure: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return The unique provider key identifier {@code "SMS_TWILIO"}.
     */
    @Override
    public String getProviderName() {
        return "SMS_TWILIO";
    }
}