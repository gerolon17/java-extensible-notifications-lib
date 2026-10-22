package com.gero.notifications.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Central facade and service registry responsible for managing notification channels
 * and dispatching messages across diverse communication providers.
 *
 * <p>Designed in compliance with the Open/Closed Principle (OCP) and Single Responsibility
 * Principle (SRP). It decouples message dispatching from concrete provider implementations,
 * offering both synchronous dispatching and non-blocking asynchronous execution via
 * {@link CompletableFuture}.</p>
 *
 * <p>This service is thread-safe and completely agnostic to external enterprise frameworks,
 * making it suitable for standalone applications, microservices, or custom runtime environments.</p>
 */
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final Map<String, NotificationChannel<? extends NotificationMessage>> channels;
    private final ExecutorService executorService;

    /**
     * Initializes the notification service with a default unbounded cached thread pool.
     * Suitable for standard workloads and low-to-medium volume deployments.
     */
    public NotificationService() {
        this(Executors.newCachedThreadPool());
    }

    /**
     * Initializes the notification service with a custom {@link ExecutorService}.
     * Allows consumers to configure custom thread pools, bounds, and rejection policies.
     *
     * @param executorService The thread execution pool dedicated to asynchronous dispatching.
     * @throws IllegalArgumentException if the provided {@code executorService} is {@code null}.
     */
    public NotificationService(ExecutorService executorService) {
        if (executorService == null) {
            throw new IllegalArgumentException("ExecutorService cannot be null");
        }
        this.channels = new ConcurrentHashMap<>();
        this.executorService = executorService;
    }

    /**
     * Registers a new provider channel implementation into the internal thread-safe registry.
     *
     * @param channel The concrete {@link NotificationChannel} instance to register.
     * @param <T>     The notification message type accepted by the channel.
     * @return The current {@code NotificationService} instance to facilitate fluent method chaining.
     * @throws IllegalArgumentException if the {@code channel} is {@code null}.
     */
    public <T extends NotificationMessage> NotificationService registerChannel(NotificationChannel<T> channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null");
        }
        channels.put(channel.getProviderName(), channel);
        logger.info("Registered notification provider: [{}]", channel.getProviderName());
        return this;
    }

    /**
     * Dispatches a notification payload synchronously via the designated provider.
     *
     * @param providerName The unique provider key identifying the registered target channel.
     * @param message      The strongly-typed message payload to deliver.
     * @param <T>          The type of the message payload.
     * @return A {@link NotificationResult} detailing the outcome of the delivery attempt.
     * @throws ChannelNotFoundException If no provider is registered under {@code providerName}.
     * @throws NotificationException    If an unexpected runtime failure occurs during message delivery.
     */
    @SuppressWarnings("unchecked")
    public <T extends NotificationMessage> NotificationResult send(String providerName, T message) {
        NotificationChannel<T> channel = (NotificationChannel<T>) channels.get(providerName);
        if (channel == null) {
            throw new ChannelNotFoundException(providerName);
        }

        try {
            return channel.send(message);
        } catch (Exception e) {
            logger.error("Failed to deliver message via provider [{}]", providerName, e);
            throw new NotificationException("Unexpected error sending message via " + providerName, e);
        }
    }

    /**
     * Dispatches a notification payload asynchronously using the configured {@link ExecutorService}.
     *
     * @param providerName The unique provider key identifying the registered target channel.
     * @param message      The strongly-typed message payload to deliver.
     * @param <T>          The type of the message payload.
     * @return A {@link CompletableFuture} that completes with the {@link NotificationResult}
     *         once the transmission task concludes.
     */
    public <T extends NotificationMessage> CompletableFuture<NotificationResult> sendAsync(String providerName, T message) {
        return CompletableFuture.supplyAsync(() -> send(providerName, message), executorService);
    }

    /**
     * Initiates an orderly shutdown of the internal worker pool.
     * Previously submitted tasks are executed, but no new tasks will be accepted.
     */
    public void shutdown() {
        executorService.shutdown();
    }
}