package com.gero.notifications.push;

import com.gero.notifications.core.NotificationChannel;
import com.gero.notifications.core.NotificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Concrete implementation of {@link NotificationChannel} targeting the Firebase Cloud Messaging (FCM) platform.
 *
 * <p>Simulates downstream transmission of push notification messages to mobile or web clients via
 * Google's FCM v1 HTTP API. Authentication credentials are configured programmatically via
 * constructor injection in pure Java, avoiding external properties or configuration files.</p>
 *
 * <p>Performs client-side validation on device registration tokens before dispatching simulated
 * network calls and generating upstream project-scoped message identifiers.</p>
 */
public class FirebasePushChannel implements NotificationChannel<PushNotificationMessage> {

    private static final Logger logger = LoggerFactory.getLogger(FirebasePushChannel.class);

    private final String serverKey;

    /**
     * Constructs a new {@code FirebasePushChannel} configured with the specified server credentials.
     *
     * @param serverKey The Firebase server key or service account bearer token used for authorization.
     * @throws IllegalArgumentException if {@code serverKey} is {@code null} or contains only whitespace.
     */
    public FirebasePushChannel(String serverKey) {
        if (serverKey == null || serverKey.isBlank()) {
            throw new IllegalArgumentException("Firebase server key cannot be null or empty");
        }
        this.serverKey = serverKey;
    }

    /**
     * Simulates dispatching a {@link PushNotificationMessage} to the FCM endpoint.
     *
     * <p>Validates the presence of a target device registration token, logs simulated transmission
     * details via SLF4J, and returns an upstream tracking identifier formatted according to FCM standards.</p>
     *
     * @param message The push notification message containing recipient token, title, body, and payload data.
     * @return A {@link NotificationResult} indicating whether delivery succeeded with an FCM message ID,
     *         or failed with a diagnostic error message.
     */
    @Override
    public NotificationResult send(PushNotificationMessage message) {
        try {
            logger.info("[{}] Authenticating FCM request with server key...", getProviderName());

            if (message.getRecipient() == null || message.getRecipient().isBlank()) {
                return NotificationResult.failure("Device token cannot be empty");
            }

            logger.info("[{}] Sending push notification to device token [{}] with title: \"{}\"",
                    getProviderName(), message.getRecipient(), message.getTitle());

            String simulatedFcmMessageId = "projects/my-app/messages/" + UUID.randomUUID().toString();
            logger.info("[{}] Push notification delivered. FCM ID: {}", getProviderName(), simulatedFcmMessageId);

            return NotificationResult.success(simulatedFcmMessageId);

        } catch (Exception e) {
            logger.error("[{}] Error delivering push notification", getProviderName(), e);
            return NotificationResult.failure("Firebase delivery failure: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return The unique provider key identifier {@code "PUSH_FIREBASE"}.
     */
    @Override
    public String getProviderName() {
        return "PUSH_FIREBASE";
    }
}