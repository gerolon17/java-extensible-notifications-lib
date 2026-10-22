package com.gero.notifications.push;

import com.gero.notifications.core.NotificationMessage;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Immutable data transfer object representing a push notification payload.
 *
 * <p>Encapsulates standard mobile notification attributes, including the destination
 * device token, display title, message body, and arbitrary key-value custom data
 * intended for client-side processing (e.g., deep linking, custom routing). Instances
 * are constructed using the Builder pattern applied directly to the constructor.</p>
 */
@Getter
public class PushNotificationMessage implements NotificationMessage {

    /**
     * The target destination identifier, typically a device registration token
     * or notification topic.
     */
    private final String recipient;

    /**
     * The notification display header or alert title presented to the user.
     */
    private final String title;

    /**
     * The main message body or alert text displayed within the notification container.
     */
    private final String body;

    /**
     * Optional custom key-value payload data forwarded alongside the notification
     * for client-side background or application-level handling.
     */
    private final Map<String, String> data;

    /**
     * Constructs a new {@code PushNotificationMessage} using the Builder pattern.
     *
     * @param recipient The target device registration token or topic string.
     * @param title     The notification header title.
     * @param body      The descriptive alert body text.
     * @param data      A map containing custom key-value pairs, or {@code null} if no extra payload is needed.
     */
    @Builder
    public PushNotificationMessage(String recipient, String title, String body, Map<String, String> data) {
        this.recipient = recipient;
        this.title = title;
        this.body = body;
        this.data = data;
    }
}

