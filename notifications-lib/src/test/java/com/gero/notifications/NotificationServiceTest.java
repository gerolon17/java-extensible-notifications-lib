package com.gero.notifications;

import com.gero.notifications.core.ChannelNotFoundException;
import com.gero.notifications.core.NotificationResult;
import com.gero.notifications.core.NotificationService;
import com.gero.notifications.email.EmailMessage;
import com.gero.notifications.email.SendGridEmailChannel;
import com.gero.notifications.push.FirebasePushChannel;
import com.gero.notifications.push.PushNotificationMessage;
import com.gero.notifications.sms.SmsMessage;
import com.gero.notifications.sms.TwilioSmsChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite verifying core channel behaviors, validation boundaries,
 * error handling scenarios, and asynchronous execution across all supported providers.
 *
 * <p>Validates that {@link NotificationService} correctly orchestrates provider lookups,
 * dispatches typed payloads, handles channel-level validation failures gracefully,
 * and manages background execution via {@link CompletableFuture}.</p>
 */
class NotificationServiceTest {

    private NotificationService notificationService;

    /**
     * Initializes the service facade and registers mock channel implementations before each test execution.
     */
    @BeforeEach
    void setUp() {
        notificationService = new NotificationService();
        // Register mock-configured channel implementations
        notificationService.registerChannel(new SendGridEmailChannel("SG.valid_mock_token_12345"));
        notificationService.registerChannel(new TwilioSmsChannel("AC123456", "auth_token_xyz", "+12025550199"));
        notificationService.registerChannel(new FirebasePushChannel("fcm_server_key_secure_mock"));
    }

    /**
     * Tears down resources and gracefully terminates thread pool executors after each test execution.
     */
    @AfterEach
    void tearDown() {
        notificationService.shutdown();
    }

    /**
     * Verifies that a valid {@link EmailMessage} is successfully dispatched through SendGrid,
     * returning a positive status and an upstream tracking message ID.
     */
    @Test
    @DisplayName("Should successfully dispatch an email via SendGrid")
    void shouldSendEmailSuccessfully() {
        EmailMessage email = EmailMessage.builder()
                .recipient("test@example.com")
                .subject("Welcome to our platform")
                .body("Your verification code is 123456.")
                .sender("no-reply@platform.com")
                .build();

        NotificationResult result = notificationService.send("EMAIL_SENDGRID", email);

        assertNotNull(result);
        assertTrue(result.success());
        assertNotNull(result.messageId());
        assertTrue(result.messageId().startsWith("sg.msg."));
        assertNull(result.errorMessage());
    }

    /**
     * Verifies that the SendGrid channel rejects malformed email destination addresses
     * and produces a failure result containing descriptive diagnostics.
     */
    @Test
    @DisplayName("Should fail when email recipient format is invalid")
    void shouldFailWhenEmailRecipientIsInvalid() {
        EmailMessage email = EmailMessage.builder()
                .recipient("invalid-email-address")
                .subject("Test Subject")
                .body("Hello World")
                .build();

        NotificationResult result = notificationService.send("EMAIL_SENDGRID", email);

        assertNotNull(result);
        assertFalse(result.success());
        assertNull(result.messageId());
        assertEquals("Invalid email format for recipient: invalid-email-address", result.errorMessage());
    }

    /**
     * Verifies that an SMS conforming to E.164 phone numbering conventions
     * is successfully dispatched through the Twilio channel.
     */
    @Test
    @DisplayName("Should successfully dispatch an SMS via Twilio")
    void shouldSendSmsSuccessfully() {
        SmsMessage sms = SmsMessage.builder()
                .recipient("+14155552671")
                .content("Your authentication OTP code is 9876.")
                .build();

        NotificationResult result = notificationService.send("SMS_TWILIO", sms);

        assertNotNull(result);
        assertTrue(result.success());
        assertNotNull(result.messageId());
        assertTrue(result.messageId().startsWith("SM"));
    }

    /**
     * Verifies that an SMS missing the mandatory international '+' prefix
     * fails local validation with an informative error message.
     */
    @Test
    @DisplayName("Should fail when SMS recipient does not conform to E.164 format")
    void shouldFailWhenSmsRecipientLacksPlusPrefix() {
        SmsMessage sms = SmsMessage.builder()
                .recipient("573001234567") // Missing leading '+' prefix
                .content("Test message")
                .build();

        NotificationResult result = notificationService.send("SMS_TWILIO", sms);

        assertNotNull(result);
        assertFalse(result.success());
        assertTrue(result.errorMessage().contains("Must use E.164 format"));
    }

    /**
     * Verifies that a valid push notification payload is successfully processed
     * and delivered via Firebase Cloud Messaging.
     */
    @Test
    @DisplayName("Should successfully dispatch a Push Notification via Firebase Cloud Messaging")
    void shouldSendPushNotificationSuccessfully() {
        PushNotificationMessage push = PushNotificationMessage.builder()
                .recipient("device_token_abc_123_xyz")
                .title("New Direct Message")
                .body("You received a message from Alice.")
                .data(Map.of("conversationId", "conv_99", "badge", "1"))
                .build();

        NotificationResult result = notificationService.send("PUSH_FIREBASE", push);

        assertNotNull(result);
        assertTrue(result.success());
        assertNotNull(result.messageId());
        assertTrue(result.messageId().startsWith("projects/my-app/messages/"));
    }

    /**
     * Verifies that the push notification channel returns a failure result
     * when the target registration token is blank.
     */
    @Test
    @DisplayName("Should fail when push recipient token is empty")
    void shouldFailWhenPushRecipientTokenIsEmpty() {
        PushNotificationMessage push = PushNotificationMessage.builder()
                .recipient("")
                .title("Test title")
                .body("Test message")
                .build();

        NotificationResult result = notificationService.send("PUSH_FIREBASE", push);

        assertNotNull(result);
        assertFalse(result.success());
        assertEquals("Device token cannot be empty", result.errorMessage());
    }

    /**
     * Verifies that attempting to send a message using an unregistered provider key
     * immediately throws a {@link ChannelNotFoundException}.
     */
    @Test
    @DisplayName("Should throw ChannelNotFoundException when provider key is not registered")
    void shouldThrowExceptionWhenProviderNotRegistered() {
        EmailMessage email = EmailMessage.builder()
                .recipient("test@example.com")
                .subject("Hello")
                .body("Body")
                .build();

        assertThrows(ChannelNotFoundException.class, () ->
                notificationService.send("UNKNOWN_PROVIDER", email)
        );
    }

    /**
     * Verifies that asynchronous message dispatching returns an uncompleted {@link CompletableFuture}
     * that executes on a background worker thread and resolves with a valid {@link NotificationResult}.
     *
     * @throws ExecutionException   If the asynchronous computation threw an unhandled exception.
     * @throws InterruptedException If the caller thread was interrupted while waiting for the future to complete.
     */
    @Test
    @DisplayName("Should dispatch email asynchronously using CompletableFuture")
    void shouldSendEmailAsynchronously() throws ExecutionException, InterruptedException {
        EmailMessage email = EmailMessage.builder()
                .recipient("async_recipient@example.com")
                .subject("Async Execution Test")
                .body("Background thread processing.")
                .build();

        CompletableFuture<NotificationResult> futureResult =
                notificationService.sendAsync("EMAIL_SENDGRID", email);

        // Blocking await solely to assert completion within the test harness lifecycle
        NotificationResult result = futureResult.get();

        assertNotNull(result);
        assertTrue(result.success());
        assertNotNull(result.messageId());
    }
}