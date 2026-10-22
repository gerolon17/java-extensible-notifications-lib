package com.gero.notifications.core;

/**
 * Common abstraction contract for all notification delivery channels.
 * 
 * Implementations of this interface encapsulate provider-specific integration logic
 * (e.g., SendGrid, Twilio, Firebase Cloud Messaging) while exposing a uniform API.
 * Adheres to the Dependency Inversion and Open/Closed principles by allowing new
 * communication channels to be added without modifying existing client code.
 *
 * @param <T> The specific subtype of {@link NotificationMessage} supported by this channel.
 */
public interface NotificationChannel<T extends NotificationMessage> {
    
    /**
     * Dispatches the specified notification message through the underlying provider.
     *
     * @param message The strongly-typed notification payload to be delivered.
     * @return A {@link NotificationResult} encapsulating the outcome (success/failure),
     *         message ID, and diagnostic error details if applicable.
     */
    NotificationResult send(T message);
    
    /**
     * Retrieves the unique identifier of the notification provider.
     * Used for channel registration and dynamic lookup within the service registry.
     *
     * @return A non-null string representing the provider key (e.g., "EMAIL_SENDGRID", "SMS_TWILIO").
     */
    String getProviderName();
}