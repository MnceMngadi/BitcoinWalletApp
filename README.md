# Bitcoin Wallet App ₿

A modern Android application built with **Kotlin**, **Jetpack Compose**, and **Clean Architecture** to track the real-time value of your Bitcoin assets across multiple currencies.

## 🚀 Project Overview
This project was developed as part of a technical assessment. It demonstrates the implementation of a robust, scalable architecture capable of handling real-time data, local persistence, and complex API constraints.

### Key Features
- **Dynamic Bitcoin Tracking**: Input any BTC amount (including fractions) and see its value instantly in ZAR, USD, and AUD.
- **Persistent Storage**: Uses **Jetpack DataStore** to remember your Bitcoin balance even after closing the app.
- **Real-time Market Data**: Integrates with the **Fixer API** for live exchange rates.
- **Price Fluctuation Indicators**: Visual cues (Up/Down arrows) indicating currency movement over the last 24 hours.
- **Advanced Error Handling**: Custom `Either` type implementation for safe, functional handling of network failures and API rate limits.
- **Custom Material 3 UI**: Clean, modern interface using the **Poppins** font family and custom styled components.
- **Secure Secret Management**: API keys are securely managed outside of the version control system.

---

## 🛠 Tech Stack & Architecture
The app follows **Clean Architecture** principles to ensure a clear separation of concerns, making the codebase highly testable and maintainable.

### Layers:
1. **Domain Layer**: The core of the app. Contains pure Kotlin business logic, Use Cases, Repository interfaces, and Domain models.
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

## 📖 Solution Walkthrough

### 1. Data Synchronization & Optimization
To ensure a smooth user experience while respecting the Fixer API's free-tier limitations (100 requests/month):
- **In-memory Caching**: A 10-minute cache mechanism in the Repository prevents redundant network calls.
- **Request Decoupling**: API calls are triggered independently from user typing, ensuring we don't fire requests on every keystroke.
- **Selective Fetching**: The app specifically requests only the required symbols (`BTC,ZAR,USD,AUD`) to minimize payload size and server load.

### 2. Dynamic Fluctuation Calculation
Since the official `/fluctuation` endpoint is restricted to paid tiers:
- **The Solution**: The app fetches current rates and historical rates from 2 days ago, then calculates the percentage change manually. This provides the user with real, live market movement data without requiring a premium API key.

### 3. Data Flow & Calculations
The core "brain" of the app resides in the **Domain Layer**, specifically within the `GetWalletDataUseCase`:
- **Reactive Stream**: The app combines a local `btcAmount` stream (from DataStore) with a remote `rates` stream (from API) using the Kotlin `combine` operator.
- **Mathematical Conversion**: Since exchange rates can be volatile, the app fetches live rates relative to a base currency and converts them to be Bitcoin-centric: `Value = User_BTC_Amount * (Target_Rate / BTC_Rate)`.
- **Threading Optimization**: 
    - **I/O Operations**: Network fetching and disk access are offloaded to `Dispatchers.IO`.
    - **Computational Work**: The mathematical multiplication for all currency cards is handled by `Dispatchers.Default`, ensuring zero impact on UI frame rates.

### 4. Visual Feedback & UX
- **Custom Typography**: Integrated the **Poppins** font family for a modern, professional look.
- **Styled Components**: 
    - **MyTopAppBar**: A custom header with a manual refresh action.
    - **BtcInputSection**: A refined input field with rounded corners and distinct labeling.
- **Loading States**: A custom `LoadingDialog` centers a progress indicator while data is being fetched.
- **Error Resilience**: Detailed error messages (e.g., Network Connection, API Rate Limits, or Server Errors) are parsed and displayed via a styled `ErrorDialog` with a **Retry** option.

### 4. Automated Testing
The project includes a suite of unit tests to ensure the reliability of core components:
- **UseCase Tests**: Validates the mathematical conversion logic and data combination in `GetWalletDataUseCaseTest`.
- **ViewModel Tests**: Ensures the UI state updates correctly in response to data changes and user interactions in `WalletViewModelTest`.
- **Mocking**: Uses **MockK** for efficient dependency mocking and **Kotlinx Coroutines Test** for handling asynchronous logic.

---

## 🛡️ Security & Deployment

### 1. Secure Secret Management
To follow production security standards:
- **Local**: The API Key is stored in `local.properties` (git-ignored) and injected into the app via `BuildConfig`.
- **CI/CD**: For GitHub Actions, the key is stored as a **GitHub Secret** (`FIXER_API_KEY`) and injected during the build process, ensuring it's never exposed in the public codebase.

### 2. CI/CD Pipeline
The project includes a robust **GitHub Actions** workflow (`android.yml`) that:
- Runs **KtLint** checks to enforce code style.
- Builds the debug APK (`assembleDebug`).
- Runs **Unit Tests** to ensure logic integrity.
- Optimized to handle Java 25 compatibility issues by pinning the environment to **JDK 17**.

### 3. Extra Production Steps (Future Enhancements)
If I were to take this app to a full production environment, I would implement the following:

- **Comprehensive Testing Suite**:
    - **Unit Tests**: Full coverage for Use Cases and Mappers using MockK and JUnit5.
    - **Integration Tests**: Verification of the Repository layer with MockWebServer.
    - **UI Tests**: Automated screen flow validation using Compose Testing library.
- **Security Hardening**:
    - **Encrypted Data Persistence**: Migrate from standard DataStore to `EncryptedSharedPreferences` for sensitive data.
    - **Certificate Pinning**: Prevent Man-in-the-Middle attacks by pinning the API server's certificate.
    - **Biometric Lock**: Add a fingerprint/face-ID layer to secure the wallet balance.
- **Performance & Optimization**:
    - **Obfuscation**: Enable R8 shrinking and obfuscation to protect the source code from reverse engineering.
    - **Baseline Profiles**: To improve app startup time and eliminate jank during the first run.
- **Analytics & Crashlytics**:
    - Integration of **Firebase Crashlytics** for real-time error tracking and **Google Analytics** to understand user interaction patterns.

---

## 🛠️ How to Build & Run
1. Clone the repository.
2. Open in the latest version of **Android Studio**.
3. **API Key Setup (Local)**:
    - Open your `local.properties` file in the project root.
    - Add the following line: `FIXER_API_KEY=YOUR_ACTUAL_API_KEY`
4. **API Key Setup (GitHub Actions)**:
    - If you are running the CI/CD pipeline, add your API key as a repository secret named `FIXER_API_KEY`.
5. Run `./gradlew assembleDebug` to build or simply press **Run** in Android Studio.
6. **Run Tests**: Execute `./gradlew test` to run all unit tests.
7. (Optional) Run `./gradlew ktlintCheck` to verify code styling.
