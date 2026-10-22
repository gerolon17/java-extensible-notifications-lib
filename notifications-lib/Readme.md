# Java Notifications Library

A flexible, framework-agnostic Java library designed to unify notification delivery across multiple channels (Email, SMS, Push). Built with SOLID principles, it allows developers to swap providers seamlessly without altering client code.

## Key Features
* **Unified Abstraction:** Single interface for all notification types.
* **Framework Agnostic:** Pure Java 21 implementation (No Spring, Quarkus, or YAML files required).
* **Asynchronous Support:** Non-blocking message dispatching using `CompletableFuture`.
* **Extensible:** Easily add new channels by implementing the `NotificationChannel` interface.

## Supported Channels
1. **Email** (via SendGrid implementation)
2. **SMS** (via Twilio implementation)
3. **Push Notifications** (via Firebase Cloud Messaging)

## Installation
Build the library using Maven:
```bash
mvn clean install