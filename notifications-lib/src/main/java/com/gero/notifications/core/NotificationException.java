package com.gero.notifications.core;

/**
 * Base unchecked exception representing operational, infrastructural, or delivery failures
 * encountered during notification processing.
 *
 * <p>Serves as the root of the notification domain exception hierarchy, ensuring that
 * callers can establish a single catch boundary for all library-specific runtime exceptions
 * without exposing internal or third-party provider dependencies.</p>
 */
public class NotificationException extends RuntimeException {

    /**
     * Constructs a new {@code NotificationException} with the specified error detail message.
     *
     * @param message Explanatory text detailing the nature of the failure.
     */
    public NotificationException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code NotificationException} with the specified error detail message
     * and underlying root cause.
     *
     * @param message Explanatory text detailing the nature of the failure.
     * @param cause   The underlying exception or upstream provider error that triggered this failure.
     */
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}