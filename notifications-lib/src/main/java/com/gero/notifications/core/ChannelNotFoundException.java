package com.gero.notifications.core;

/**
 * Unchecked domain exception thrown when an operation targets a notification provider
 * key that has not been registered in the {@link NotificationService}.
 * 
 * <p>Indicates a client configuration mistake or an unsupported provider request.</p>
 */
public class ChannelNotFoundException extends NotificationException {

    /**
     * Constructs a new {@code ChannelNotFoundException} indicating which provider identifier was missing.
     *
     * @param providerName The unresolved provider key (e.g., "SLACK_WEBHOOK").
     */
    public ChannelNotFoundException(String providerName) {
        super("No notification channel registered with provider name: " + providerName);
    }
}