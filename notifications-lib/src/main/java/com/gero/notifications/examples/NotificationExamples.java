package com.gero.notifications.examples;

import com.gero.notifications.core.NotificationResult;
import com.gero.notifications.core.NotificationService;
import com.gero.notifications.email.EmailMessage;
import com.gero.notifications.email.SendGridEmailChannel;
import com.gero.notifications.push.FirebasePushChannel;
import com.gero.notifications.push.PushNotificationMessage;
import com.gero.notifications.sms.SmsMessage;
import com.gero.notifications.sms.TwilioSmsChannel;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * End-to-end usage demonstration and executable entry point for the notification library.
 *
 * <p>Illustrates programmatic provider registration without framework dependency injection,
 * strongly-typed message composition via the Builder pattern, synchronous delivery flows,
 * non-blocking asynchronous dispatching backed by {@link CompletableFuture}, and graceful
 * thread pool teardown.</p>
 */
public class NotificationExamples {

    /**
     * Executes end-to-end sample scenarios across Email, SMS, and Push notification channels.
     *
     * @param args Command-line execution arguments (not utilized).
     */
    public static void main(String[] args) {
        System.out.println("=== Initializing Notification Service ===");

        // 1. Programmatically configure and register providers into the service registry
        NotificationService service = new NotificationService()
                .registerChannel(new SendGridEmailChannel("SG.mock_api_key"))
                .registerChannel(new TwilioSmsChannel("AC_mock_sid", "mock_token", "+1234567890"))
                .registerChannel(new FirebasePushChannel("mock_fcm_server_key"));

        // 2. Dispatch a synchronous Email notification
        System.out.println("\n--- Sending Synchronous Email ---");
        EmailMessage email = EmailMessage.builder()
                .recipient("user@example.com")
                .subject("Welcome!")
                .body("Thanks for joining our platform.")
                .build();
        NotificationResult emailResult = service.send("EMAIL_SENDGRID", email);
        System.out.println("Email Success: " + emailResult.success() + " | ID: " + emailResult.messageId());

        // 3. Dispatch a synchronous SMS notification
        System.out.println("\n--- Sending Synchronous SMS ---");
        SmsMessage sms = SmsMessage.builder()
                .recipient("+19876543210")
                .content("Your OTP is 4567")
                .build();
        NotificationResult smsResult = service.send("SMS_TWILIO", sms);
        System.out.println("SMS Success: " + smsResult.success() + " | ID: " + smsResult.messageId());

        // 4. Dispatch an asynchronous Push Notification using non-blocking execution
        System.out.println("\n--- Sending Asynchronous Push Notification ---");
        PushNotificationMessage push = PushNotificationMessage.builder()
                .recipient("device_token_xyz")
                .title("New Update")
                .body("Version 2.0 is now available.")
                .data(Map.of("action", "update_app"))
                .build();

        CompletableFuture<NotificationResult> pushFuture = service.sendAsync("PUSH_FIREBASE", push);

        // Perform concurrent tasks on the caller thread while transmission resolves
        System.out.println("Doing other work on the main thread...");

        // Await asynchronous completion (blocking join for demonstration purposes only)
        pushFuture.thenAccept(result ->
                System.out.println("Async Push Success: " + result.success() + " | ID: " + result.messageId())
        ).join();

        // 5. Gracefully terminate worker threads and release platform resources
        System.out.println("\n=== Shutting down service ===");
        service.shutdown();
    }
}