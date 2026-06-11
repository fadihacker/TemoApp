# E-Loop (TemoApp)

**E-Loop** is an Android mobile application that helps users recycle electronic devices responsibly. Users can scan or upload device photos, get AI-powered valuations, earn eco points, find nearby recycling centers, schedule pickups, and track their environmental impact — all in one app.

Built with **Kotlin** and **Jetpack Compose**, with backend services powered by **Firebase** and **Google Gemini AI**.

---

## Features

### Device Scanning & Valuation
- Upload a photo from the gallery to identify and value electronic devices
- Supports smartphones, laptops, headphones, smartwatches, tablets, and more
- Condition-based pricing (Excellent, Good, Fair, Broken)
- Price database covering popular brands: Apple, Samsung, Dell, HP, Sony, and others
- AI valuation powered by **Google Gemini** for high-accuracy analysis
- On-device **ML Kit** image labeling as a fallback (works offline)

### Recycling Journey
- Browse device categories and start a recycling flow
- Select device condition and receive a final EGP price estimate
- Schedule a home pickup and confirm your order
- Track your device journey from scan to recycling

### Eco Impact & Gamification
- Earn **Eco Points** for every device recycled
- View CO₂ savings and personal environmental impact
- Leaderboard to compete with other eco warriors
- Achievements and rewards system
- Recycling history log

### Find & Learn
- Interactive map of nearby recycling centers (Cairo area)
- Center details: hours, ratings, accepted waste types
- Eco articles and educational content
- Device journey timeline

### EcoBot Assistant
- Built-in chatbot for quick answers about:
  - Device prices and valuations
  - How to recycle
  - Nearest collection centers
  - Points and rewards system

### User Account
- Firebase Authentication (login & signup)
- User profile with eco stats
- Push notifications via Firebase Cloud Messaging
- Account settings, theme toggle (light/dark), and language switch (English / Arabic)

### Admin
- Admin dashboard for managing orders and platform activity

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Navigation | Navigation Compose |
| Backend | Firebase Auth, Firestore, Analytics, FCM |
| AI | Google Gemini API, ML Kit (Image Labeling, Object Detection) |
| Camera | CameraX |
| Maps | Google Maps Compose, OSMDroid |
| Networking | Retrofit, OkHttp |
| Images | Coil |
| Build | Gradle (Kotlin DSL), AGP 8.13 |

---

## Project Structure

```
app/
├── src/main/
│   ├── java/com/eloop/mobileapp/
│   │   ├── MainActivity.kt          # App entry point & navigation
│   │   ├── DeviceAnalyzer.kt        # Device scan logic
│   │   ├── DevicePriceDatabase.kt   # Price data & image URLs
│   │   ├── DevicePriceTable.kt      # Category estimates
│   │   ├── data/
│   │   │   ├── FirebaseRepository.kt
│   │   │   ├── GeminiClient.kt      # Gemini AI integration
│   │   │   ├── EcoData.kt
│   │   │   ├── RecyclingCentersData.kt
│   │   │   └── SessionManager.kt
│   │   ├── service/
│   │   │   └── ELoopMessagingService.kt
│   │   └── ui/
│   │       ├── components/          # Reusable UI components
│   │       ├── screens/             # 30+ app screens
│   │       └── theme/               # Colors, typography, theming
│   └── res/
│       ├── values/                  # English strings
│       └── values-ar/               # Arabic strings (RTL support)
├── google-services.json             # Firebase configuration
└── build.gradle.kts
```

---

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- JDK 17+
- Android SDK (API 36)
- A Firebase project with Auth, Firestore, and Messaging enabled
- A Google Gemini API key

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/fadihacker/TemoApp.git
   cd TemoApp
   ```

2. **Create `local.properties`** in the project root (this file is git-ignored):
   ```properties
   sdk.dir=C\:\\Users\\YOUR_USER\\AppData\\Local\\Android\\Sdk
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

3. **Firebase setup**
   - Place your `google-services.json` in the `app/` directory
   - Enable **Authentication**, **Cloud Firestore**, and **Cloud Messaging** in the Firebase Console

4. **Open in Android Studio** and sync Gradle

5. **Run** on an emulator or physical device (minSdk 24, targetSdk 36)

### Build

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (minified)
./gradlew assembleRelease
```

---

## App Screens

| Screen | Description |
|---|---|
| Splash & Onboarding | Welcome flow for new users |
| Login / Signup | Firebase authentication |
| Home | Dashboard with eco stats and quick actions |
| Categories | Browse recyclable device types |
| Scan | Upload photo for device identification |
| Condition Selection | Rate your device's condition |
| AI Valuation | Gemini-powered price estimate |
| Impact | Personal CO₂ and eco impact stats |
| Find | Map of nearby recycling centers |
| Rewards & Leaderboard | Points, rankings, and achievements |
| EcoBot Chat | AI assistant for recycling questions |
| Schedule Pickup | Book a device collection |
| Profile & Settings | Account, theme, language preferences |
| Admin Dashboard | Platform management |

---

## Localization

The app supports **English** and **Arabic** (RTL). Language can be switched from Account Settings. String resources are in:

- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-ar/strings.xml`

---

## Configuration Notes

| File | Purpose | Committed? |
|---|---|---|
| `local.properties` | Android SDK path + Gemini API key | No (git-ignored) |
| `app/google-services.json` | Firebase project config | Yes |
| `gradle.properties` | Gradle JVM settings | Yes |

> **Important:** Never commit `local.properties` — it contains your private API keys.

---

## License

This project was developed as a graduation project. All rights reserved.
