# Implementation Plan: Bitcoin Wallet App

This document outlines the technical strategy, architectural decisions, and problem-solving approach taken during the development of the Bitcoin Wallet application.

---

## 1. Requirement Analysis & Challenges

### Core Objectives:
*   **BTC as Base**: The application must display the user's wallet value in multiple currencies, using Bitcoin (BTC) as the functional baseline.
*   **Real-time Accuracy**: Integrate with the Fixer API for live market rates.
*   **Persistence**: User balance must persist across app restarts using Jetpack DataStore.
*   **Constraint Management**: Navigate the strict limitations of the Fixer API Free Tier (100 requests/month, EUR base-lock, blocked fluctuation endpoint).

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

## 3. Technical Solutions for API Constraints

### The "EUR Bridge" Calculation
The Fixer Free Tier locks the base currency to **EUR**. Since the requirement was **BTC base**, I implemented a mathematical bridge:
1.  Fetch `BTC_in_EUR` and `ZAR_in_EUR`.
2.  Calculate `1 BTC = ZAR_rate / BTC_rate`.
3.  This allows the app to satisfy requirements without needing a paid API key.

### Dynamic Fluctuation Recovery
Fixer blocks the `/fluctuation` endpoint on free accounts. 
*   **Approach**: I implemented a fallback that fetches current rates and historical rates (from 2 days ago) and calculates the percentage delta manually: `((Today - Past) / Past) * 100`.

---

## 4. Security Implementation

### Secret Management
*   **Local**: API keys are moved out of the code and into `local.properties`.
*   **BuildConfig**: Keys are injected via Gradle's `buildConfigField`, keeping them out of the compiled source code.
*   **CI/CD**: Configured GitHub Actions to use **GitHub Secrets** for the `FIXER_API_KEY`, ensuring builds pass without exposing sensitive tokens in the public repository.

---

## 5. UI/UX Design Decisions

*   **Typography**: Integrated **Poppins** (SemiBold/Bold) to provide a premium, fintech-style appearance.
*   **Layout**: Refactored the main screen into a single `LazyColumn` to support smooth scrolling in **Landscape Mode**.
*   **Feedback**: Created custom `LoadingDialog` and `ErrorDialog` (with Retry logic) to handle asynchronous network states gracefully.
*   **Precision**: Implemented `BigDecimal` formatting for BTC values to prevent "Scientific Notation" (e.g., `5.0E-5`) and ensure 8-decimal accuracy.

---

## 6. Verification & Testing Strategy

*   **Unit Testing**: Used **MockK** and **Kotlinx Coroutines Test** to verify the math logic in `GetWalletDataUseCase` and state transitions in `WalletViewModel`.
*   **Code Quality**: Enforced strict styling using **KtLint**.
*   **CI/CD Pipeline**: Optimized GitHub Actions with **JDK 17** and isolated compilation tasks (`assembleDebug`) to bypass unhandled Java 25 issues in the Android Gradle Plugin.

---

## 7. Future Production Roadmap

If this were a production release, the next steps would be:
1.  **App Signing**: Configuring standard Google Play signing for secure, authentic APK and App Bundle (AAB) distribution.
2.  **Crashlytics & Analytics**: Integrating Firebase Crashlytics for real-time crash reporting and Google Analytics to understand user interaction patterns.
3.  **Certificate Pinning**: Hardening OkHttp against MitM attacks.
4.  **Biometric Lock**: Securing the wallet view behind Fingerprint/FaceID.
5.  **Encrypted Storage**: Moving from standard DataStore to `EncryptedSharedPreferences`.
6.  **Baseline Profiles**: Optimizing app startup speed via R8.
