# Quran Karim for Android

An Arabic-first Quran app for reading, listening, memorization, prayer times, Qibla, and nearby mosques. The application is built with Kotlin and Jetpack Compose and uses a local Quran dataset for reading.

## Features

- Browse Surahs and Juz, read Quran pages, search, bookmark ayahs, and save reading progress.
- Play recitations by ayah or range, repeat ayahs and ranges, and continue playback in the background.
- Practice memorization with listening and voice-recognition modes, including an optional offline Arabic ASR model.
- View prayer times and Hijri events, use the Qibla compass, and search for nearby mosques.
- Store reading and memorization progress locally.

Quran text is bundled with the app. It must not be generated or edited; changes to the dataset should be checked against the integrity tests.

## Requirements

- Android Studio with Android SDK Platform 34 and Build Tools installed.
- JDK 17.
- An Android device or emulator running Android 8.0 (API 26) or later.

Set `sdk.dir` in `local.properties` to your local Android SDK installation if Android Studio has not created that file. Do not commit machine-specific SDK paths.

## Build and Test

From the repository root:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
```

On macOS or Linux:

```sh
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

Install the debug APK on a connected device with:

```sh
./gradlew :app:installDebug
```

On Windows, use:

```powershell
.\gradlew.bat :app:installDebug
```

## Project Structure

- `app/src/main/java/com/quoren/app/domain`: domain models, repository contracts, and business logic.
- `app/src/main/java/com/quoren/app/data`: Quran, audio, memorization, prayer, and location data implementations.
- `app/src/main/java/com/quoren/app/feature`: Compose screens and their ViewModels.
- `app/src/main/java/com/quoren/app/di`: Hilt dependency-injection modules.
- `app/src/main/assets`: bundled Quran source data.
- `app/src/test`: unit tests, including Quran dataset integrity checks.

## Permissions and Network Use

Microphone access is requested for voice recitation. Location access is used for location-based prayer times and nearby mosques. Network access is used for streaming recitations and map tiles; downloaded audio remains available offline. Offline voice recognition requires downloading the Vosk model from the app, which is approximately 318 MB.

Permissions are requested when their related features are used. Reading Quran text does not require a network connection.