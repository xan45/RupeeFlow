# RupeeFlow

Offline Personal Finance Tracker for Android

RupeeFlow is a 100% offline, privacy-first personal finance application for Android built with Kotlin, Jetpack Compose, Material 3, and Room Database. It parses Indian bank and UPI statements entirely on-device without internet access, third-party trackers, or cloud storage.

---

## Features

- **Bank statement import**: Intelligent parsing for major Indian banks including Kotak Mahindra Bank, HDFC Bank, State Bank of India (SBI), and ICICI Bank.
- **UPI statement import**: Full support for UPI statement exports from PhonePe, Google Pay (GPay), BHIM, and generic UPI narrations.
- **Offline analysis**: 100% on-device local analytics computing net cash flow, income trends, time bucketing (Day/Week/Month/Year), and category breakdown.
- **Expense & earnings tracking**: Automated categorization across Food, Groceries, Utilities, Rent, Subscriptions, Salary, and Transfers with rule-based pattern matching.
- **Two-way counterparty reconciliation**: Automatically correlates counterparties who both send and receive money, calculating net balance and complete transaction history.
- **Privacy by design**: Does not request the `android.permission.INTERNET` permission. No remote servers, no telemetry, no analytics SDKs.
- **Modern Material 3 UI**: Clean, responsive dark mode UI with edge-to-edge support and smooth transitions.
- **Storage Access Framework (SAF)**: Secure local document picking preserving file safety and user control.

---

## Architecture & Tech Stack

- **Language:** Kotlin 2.0 (JVM 17)
- **UI Toolkit:** Jetpack Compose + Material 3 (Material You)
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture Layering (Domain, Data, UI)
- **Local Persistence:** AndroidX Room 2.6.1 SQLite Database (ACID compliant, Flow streams)
- **Asynchrony:** Kotlin Coroutines & StateFlow for non-blocking UI rendering across 100,000+ records
- **Code Shrinking & Optimization:** R8 ProGuard rules enabled for release builds
- **Zero Internet Requirement:** Completely offline; zero network calls

---

## Project Structure

```text
RupeeFlow/
├── app/
│   ├── build.gradle.kts           # App module configuration & release signing
│   ├── proguard-rules.pro        # Proguard / R8 optimization & stripping rules
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml # Zero-internet offline manifest
│       │   ├── java/com/rupeeflow/app/
│       │   │   ├── MainActivity.kt
│       │   │   ├── RupeeFlowApp.kt
│       │   │   ├── di/             # Manual dependency injection container
│       │   │   ├── data/           # Local Room DB, DAOs, entities, & statement parsers
│       │   │   ├── domain/         # Models & repository contracts
│       │   │   ├── ui/             # Jetpack Compose UI screens, components, theme
│       │   │   └── util/           # Indian currency & date formatters
│       │   └── res/                # Vector drawables, themes, & app icons
│       └── test/                   # Comprehensive unit tests for statement parsers
├── build.gradle.kts               # Root build script
├── settings.gradle.kts            # Project settings
└── gradle/wrapper/                # Gradle 8.9 wrapper
```

---

## Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1+) or newer
- Android SDK 35 (Compile / Target SDK 35, Minimum SDK 26)
- JDK 17+

### Build from Command Line

```bash
# Clone the repository
git clone https://github.com/xan45/RupeeFlow.git
cd RupeeFlow

# Run unit tests
./gradlew test

# Build signed release APK
./gradlew assembleRelease
```

The signed release APK will be generated at:
`app/build/outputs/apk/release/app-release.apk`

---

## Release Versioning

- **Current Version:** `0.1.0-alpha.1`
- **Version Code:** `1`
- **Release Channel:** GitHub Public Releases

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
