package com.gero.notifications.core;

/**
 * Marker interface and base contract for all notification message payloads.
 *
 * <p>Enforces the presence of a primary target destination (e.g., an email address,
 * an E.164 phone number, or a mobile device token) across all channel implementations,
 * while allowing domain-specific subclasses to encapsulate additional delivery metadata.</p>
 */
public interface NotificationMessage {

    /**
     * Retrieves the primary recipient identifier for the notification.
     *
     * @return The target destination address, phone number, or registration token.
     */
    String getRecipient();
}