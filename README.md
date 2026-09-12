# Bitcoin Wallet App ₿

A modern Android application built with **Kotlin**, **Jetpack Compose**, and **Clean Architecture** to track the real-time value of your Bitcoin assets across multiple currencies.

##  Project Overview
This project was developed as part of a technical assessment. It demonstrates the implementation of a robust, scalable architecture capable of handling real-time data, local persistence, and complex API constraints.

### Key Features
- **Dynamic Bitcoin Tracking**: Input any BTC amount (including fractions) and see its value instantly in ZAR, USD, and AUD.
- **Persistent Storage**: Uses **Jetpack DataStore** to remember your Bitcoin balance even after closing the app.
- **Real-time Market Data**: Integrates with the **Fixer API** for live exchange rates.
- **Price Fluctuation Indicators**: Visual cues (Up/Down arrows) indicating currency movement over the last 24 hours.
- **Advanced Error Handling**: Custom `Either` type implementation for safe, functional handling of network failures and API rate limits.
- **Material 3 UI**: Clean, modern interface using the latest Android design standards.

---

## Tech Stack & Architecture
The app follows **Clean Architecture** principles to ensure a clear separation of concerns, making the codebase highly testable and maintainable.

### Layers:
1. **Domain Layer**: The core of the app. Contains purely Kotlin business logic, Use Cases, Repository interfaces, and Domain models.
2. **Data Layer**: Responsible for data retrieval. Implements the Repository interface, handles API calls via **Retrofit**, and manages local storage via **Preferences DataStore**.
3. **Presentation Layer**: Built with **Jetpack Compose** using the **MVVM** pattern. State is managed via **Kotlin StateFlow** for reactive UI updates.

### Libraries Used:
- **Hilt**: Dependency Injection.
- **Retrofit & OkHttp**: Networking.
- **Kotlinx Serialization**: JSON parsing.
- **Jetpack DataStore**: Local persistence.
- **Compose Navigation**: App routing.
- **KtLint**: Code style enforcement.

---

## Solution Walkthrough

### 1. Data Synchronization Logic
To ensure a smooth user experience while respecting the Fixer API's free-tier limitations (100 requests/month), I implemented:
- **In-memory Caching**: A 10-minute cache mechanism in the Repository to prevent redundant network calls on every app foreground event.
- **Request Decoupling**: API calls are triggered independently from user typing, ensuring we don't fire requests on every keystroke.

### 2. Handling API Constraints
The Fixer API Free Tier has a strict "Base Currency" lock (EUR only). 
- **The "EUR Bridge" Solution**: To satisfy the requirement of having BTC as the base currency, the app fetches rates relative to EUR and mathematically converts them to a BTC-base locally within the `GetWalletDataUseCase`.

### 3. Visual Feedback
- **Loading States**: A custom `LoadingDialog` blocks interaction during initial data fetch.
- **Error Resilience**: If the API limit is reached (HTTP 429) or connection is lost, a styled `ErrorDialog` appears with a **Retry** option.

---

##  Deployment Readiness (Extra Steps)
If I were to take this app to a production environment, I would implement the following:

- **1. Comprehensive Testing Suite**:
    - **Unit Tests**: Full coverage for Use Cases and Mappers using MockK and JUnit5.
    - **Integration Tests**: Verification of the Repository layer with MockWebServer.
    - **UI Tests**: Automated screen flow validation using Compose Testing library.
- **2. Security Hardening**:
    - **Secret Management**: Moving the API Key from the codebase to a `secrets.properties` file (or CI environment variables) and using **BWS (Bitwarden Secrets)** or similar tools.
    - **Certificate Pinning**: To prevent Man-in-the-Middle (MitM) attacks.
- **3. Performance & Optimization**:
    - **R8/ProGuard**: Full obfuscation and code shrinking for the release build.
    - **Baseline Profiles**: To improve app startup time and eliminate jank during the first run.
- **4. Analytics & Crashlytics**:
    - Integration of **Firebase Crashlytics** for real-time error tracking and **Google Analytics** to understand user interaction patterns.

---

## How to Build & Run
1. Clone the repository.
2. Open in the latest version of **Android Studio**.
3. **API Key Setup**: The app currently uses a pre-configured key in `NetworkModule.kt`. To use your own, replace `API_KEY` in `com.mncemngadi.bitcoinwalletapp.di.NetworkModule`.
4. Run `./gradlew assembleDebug` to build or simply press **Run** in Android Studio.
5. (Optional) Run `./gradlew ktlintCheck` to verify code styling.
