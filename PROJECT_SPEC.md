# RupeeFlow — Native Android Project Specification

## 1. Project Overview
- **App Name:** RupeeFlow
- **Version:** 0.1.0-alpha.1 (Version Code: 1)
- **Target Platform:** Native Android (Min SDK 26, Target/Compile SDK 35)
- **Architecture:** 100% Offline MVVM with Clean Architecture (Domain, Data, UI)
- **UI Framework:** Jetpack Compose + Material 3 (Material You / Dynamic Dark Theme)
- **Local Persistence:** Room SQLite Database (`rupeeflow_local.db`)
- **Statement Import:** Android Storage Access Framework (SAF)

## 2. Privacy & Security
- Zero network permissions (`android.permission.INTERNET` is omitted).
- Zero telemetry, ads, or analytics tracking.
- All bank and UPI statements are processed purely on-device.
- Fresh installs start with a completely empty database.

## 3. Supported Statement Parsers
- Kotak Mahindra Bank
- HDFC Bank
- State Bank of India (SBI)
- ICICI Bank
- PhonePe
- Google Pay
- Generic UPI / Bank Statement Fallback
