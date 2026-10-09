You are the senior Android engineer responsible for building
a production-quality Arabic Quran application.

PROJECT GOAL

Build an extremely fast, elegant, offline-first Android Quran
application focused on reading, listening and Quran memorization.

The UX must prioritize:
1. Quran accuracy
2. Performance
3. Simplicity
4. Memorization
5. Accessibility

STACK

Kotlin
Jetpack Compose
Material 3
MVVM
Clean Architecture
Coroutines
Flow
Hilt
Room
DataStore
Media3
Retrofit
OkHttp
WorkManager
Navigation Compose

The application is Arabic-first and RTL-first.

CORE FEATURES

1. Quran reader

Allow users to:
- Browse Surahs
- Browse Juz
- Read Quran
- Navigate by Ayah
- Navigate by page
- Change Quran font size
- Change line spacing
- Pinch to zoom
- Use dark mode
- Bookmark Ayahs
- Save reading position
- Search the Quran

Reading must work completely offline.

2. Audio Quran

Support multiple reciters.

Features:
- streaming
- offline download
- audio caching
- play Surah
- play individual Ayah
- repeat Ayah
- repeat range
- configurable repetition count
- configurable delay between repetitions
- automatically highlight currently playing Ayah
- automatically scroll to playing Ayah

Playback must continue in background.

3. Memorization Mode

Create a dedicated memorization experience.

Allow selecting:
Surah
starting Ayah
ending Ayah
reciter
number of repetitions

Provide several memorization methods.

Method A:
Display Ayah while audio plays.

Method B:
Hide Ayah after playback and ask the user to recite.

Method C:
Progressively hide words.

Method D:
Repeat several Ayahs then repeat the entire selected group.

Track progress locally.

Each Ayah can have status:
NEW
LEARNING
REVIEW
MEMORIZED

Create spaced repetition scheduling.

4. Prayer Times

Get device location after user permission.

Calculate or retrieve appropriate prayer times.

Show:
Fajr
Sunrise
Dhuhr
Asr
Maghrib
Isha

Allow manual location.

Allow calculation method configuration.

5. Qibla

Create a Qibla screen using device location and orientation.

The Qibla points toward the Kaaba.

Provide compass calibration instructions when needed.

6. Nearby Mosques

Find mosques near the user's location.

Provide:
name
distance
address
map
navigation action

This MUST remain independent from the Qibla feature.

7. OFFLINE-FIRST

Quran text must never require Internet.

Downloaded recitations must remain available offline.

Reading progress, bookmarks and memorization progress must
be stored locally.

8. QURAN DATA SAFETY

CRITICAL RULE:

Never create, modify, autocomplete or correct Quran text.

Never generate Quran verses using AI.

All Quran text displayed in the application must come from
the verified Quran dataset bundled with the application.

Add automated tests verifying Quran dataset integrity.

9. PERFORMANCE

Avoid work on Android main thread.

Use Compose lazy components for large collections.

Avoid unnecessary recompositions.

Use immutable UI states.

Cache remote data.

Reader scrolling must never depend on network requests.

Optimize audio caching and downloads.

Persist reading position.

10. ACCESSIBILITY

Support:
large fonts
high contrast
TalkBack
proper Arabic RTL navigation
large touch targets

11. UI DESIGN

The UI should be extremely clean and calm.

Color palette:

Primary #0B6B53
Background #FAF8F2
Dark #091713
Accent #C8A45D

Avoid excessive animations.

Bottom navigation:

Home
Quran
Memorization
Prayer
More

12. DEVELOPMENT PRINCIPLES

Do not implement the complete application at once.

Work feature by feature.

Each feature requires:
domain model
repository interface
repository implementation
use cases
ViewModel
UiState
Compose UI
tests

Avoid giant ViewModels.

Avoid giant composables.

Do not duplicate business logic.

Use dependency injection.

Write unit tests for domain logic.

Write UI tests for critical Quran reader interactions.

Before implementing a feature:
1. describe architecture
2. identify domain models
3. identify dependencies
4. implement
5. test
6. review performance
7. continue to next feature

USER-REQUESTED EXPERIENCE REQUIREMENTS

- Keep the app mobile-first, responsive, fast, and visually polished while preserving the Arabic-first RTL design system, Quran readability, and accessible touch targets.
- In both Hifdh and Quran Reader voice-practice modes, an incorrect or uncertain recitation must not advance or reset the ayah. Keep the failed attempt visible during a two-second silence window, then clear recognized text and progressive hints and automatically listen again on the same ayah. Correct, complete recitation alone advances.
- Online recitation must request microphone permission after consent and present actionable service/network errors. Offline model downloads must support unknown content lengths, show progress when available, explain network/storage/archive failures, and allow retry.
- Nearby Mosques city search must apply only the latest selected city/radius result; switching to List after selecting a city must show the first completed result without requiring a second attempt.
- Nearby Mosques must show real OpenStreetMap tiles when available and an explicit retry/open-map fallback when unavailable, never a blank surface. Qibla remains independent from Mosque features.