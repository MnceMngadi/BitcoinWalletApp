# Implementation Plan: Bitcoin Wallet App

This document outlines the technical strategy, architectural decisions, and problem-solving approach taken during the development of the Bitcoin Wallet application.

---

## 1. Requirement Analysis & Challenges

### Core Objectives:
*   **BTC as Base**: The application must display the user's wallet value in multiple currencies, using Bitcoin (BTC) as the functional baseline.
*   **Real-time Accuracy**: Integrate with the Fixer API for live market rates.
*   **Persistence**: User balance must persist across app restarts using Jetpack DataStore.
---

## 2. Architectural Strategy (Clean Architecture)

I chose **Clean Architecture** combined with **MVVM** to ensure the app is scalable, testable, and maintainable.

### Layer Breakdown:
*   **Domain Layer (The Brain)**: 
    *   Contains pure Kotlin logic (Use Cases and Models).
    *   **Decision**: Centralized the BTC conversion math here (`GetWalletDataUseCase`) to keep the UI "dumb" and easy to test.
*   **Data Layer (The Source)**:
    *   Manages network (Retrofit) and local storage (DataStore).
    *   **Decision**: Implemented a **10-minute in-memory cache** in the Repository to protect the limited API quota.
*   **Presentation Layer (The UI)**:
    *   Uses **Jetpack Compose** for a modern, reactive interface.
    *   Follows the **MVVM** pattern using Hilt for Dependency Injection.

---

## 3. Data Flow & Mathematical Logic

### Reactive Stream Architecture
The application core utilizes the Kotlin `combine` operator to merge two distinct data streams into a unified state:
1.  **Local Stream**: A `Flow<Double>` from DataStore providing the user's saved Bitcoin amount.
2.  **Remote Stream**: A `Flow` that fetches and maps the latest exchange rates from the API.

### High-Precision Calculations
To ensure financial accuracy, all mathematical conversions are performed in the Domain layer:
*   **Conversion Formula**: `Total_Value = User_BTC_Amount * Rate_per_BTC`.
*   **Decimal Safety**: Utilized `BigDecimal` for formatting large values and small fractions to prevent loss of precision and avoid scientific notation in the UI.

### Threading Strategy
To maintain a buttery-smooth 60fps UI, the app follows a strict threading model:
*   **I/O Bound**: All network and disk operations are offloaded to `Dispatchers.IO`.
*   **CPU Bound**: The mathematical mapping and list transformations are handled by `Dispatchers.Default`.
*   **Main Thread**: Strictly reserved for rendering and UI state observation.

---

## 4. Technical Solutions for API Constraints

### Direct BTC Base Fetching
The application is configured to request data from the Fixer API using **BTC as the base currency** directly. This ensures the exchange rates received are immediately relevant to the user's Bitcoin portfolio, satisfying the core requirement for BTC-centric valuation.

### Price Fluctuation Integration
The app integrates the official Fixer `/fluctuation` endpoint to fetch real-time market movement data between specified dates (yesterday to today). This allows the UI to display precise percentage changes and trend indicators (Up/Down arrows) for each currency.

---

## 5. Security Implementation

### Secret Management
*   **Local**: API keys are moved out of the code and into `local.properties`.
*   **BuildConfig**: Keys are injected via Gradle's `buildConfigField`, keeping them out of the compiled source code.
*   **CI/CD**: Configured GitHub Actions to use **GitHub Secrets** for the `FIXER_API_KEY`, ensuring builds pass without exposing sensitive tokens in the public repository.

### Header-based Authentication
To adhere to security best practices, the application utilizes an **OkHttp Interceptor** (`AuthInterceptor`) to inject the API key into the HTTP headers of every outgoing request. This prevents sensitive credentials from being exposed in plain text within request URLs, which are often susceptible to logging by servers and network intermediaries.

---

## 6. UI/UX Design Decisions

*   **Typography**: Integrated **Poppins** (SemiBold/Bold) to provide a premium, fintech-style appearance.
*   **Layout**: Refactored the main screen into a single `LazyColumn` to support smooth scrolling in **Landscape Mode**.
*   **Feedback**: Created custom `LoadingDialog` and `ErrorDialog` (with Retry logic) to handle asynchronous network states gracefully.
*   **Precision**: Implemented `BigDecimal` formatting for BTC values to prevent "Scientific Notation" (e.g., `5.0E-5`) and ensure 8-decimal accuracy.

---

## 7. Verification & Testing Strategy

*   **Unit Testing**: Used **MockK** and **Kotlinx Coroutines Test** to verify the math logic in `GetWalletDataUseCase` and state transitions in `WalletViewModel`.
*   **Code Quality**: Enforced strict styling using **KtLint**.
*   **CI/CD Pipeline**: Optimized GitHub Actions with **JDK 17** and isolated compilation tasks (`assembleDebug`) to bypass unhandled Java 25 issues in the Android Gradle Plugin.

---

## 8. Future Production Roadmap

If this were a production release, the next steps would be:
1.  **App Signing**: Configuring standard Google Play signing for secure, authentic APK and App Bundle (AAB) distribution.
2.  **Crashlytics & Analytics**: Integrating Firebase Crashlytics for real-time crash reporting and Google Analytics to understand user interaction patterns.
3.  **Certificate Pinning**: Hardening OkHttp against MitM attacks.
4.  **Biometric Lock**: Securing the wallet view behind Fingerprint/FaceID.
5.  **Encrypted Storage**: Moving from standard DataStore to `EncryptedSharedPreferences`.
6.  **Baseline Profiles**: Optimizing app startup speed via R8.
