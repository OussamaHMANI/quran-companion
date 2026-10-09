# PHASE 2 - AUDIO ENGINE & RECITERS

## OBJECTIVE

Implement a complete Quran Audio System for the Android application.

This phase starts only after Phase 1 (Quran Reader) is finished.

The goal is to provide a premium audio experience focused on:

- Multiple reciters
- Surah playback
- Ayah playback
- Ayah synchronization
- Repetition system
- Offline audio
- Background playback
- Download manager
- High performance

The implementation must be scalable, maintainable and offline-friendly.

---

# PHASE 2 SCOPE

Implement ONLY:

- Audio engine
- Reciters
- Playback
- Downloads
- Synchronization
- Audio settings

DO NOT IMPLEMENT:

- Memorization mode
- Prayer times
- Qibla
- Nearby mosques

These belong to later phases.

---

# ARCHITECTURE REQUIREMENTS

Use:

- Kotlin
- Clean Architecture
- MVVM
- Hilt
- Coroutines
- Flow
- Media3
- Room
- DataStore

Audio logic must not be implemented inside Composables.

Business logic must stay in Domain Layer.

Repositories must isolate data sources.

---

# FEATURE 1 - RECITERS

Create Reciter model.

```kotlin
data class Reciter(
    val id: String,
    val arabicName: String,
    val englishName: String,
    val riwaya: String,
    val imageUrl: String?,
    val audioBaseUrl: String
)
```

Create:

```text
ReciterRepository
```

Functions:

```kotlin
getAllReciters()

getReciterById()

getDefaultReciter()

setDefaultReciter()
```

---

# INITIAL RECITERS

Support architecture for:

- Ahmed Al Ajmi
- Maher Al Muaiqly
- Mishary Alafasy
- Yasser Al Dosari
- Saad Al Ghamdi
- Abdul Basit
- Al Minshawi
- Ali Jaber

Do not hardcode reciters in UI.

They must come from repository layer.

---

# FEATURE 2 - AUDIO PLAYER

Use Android Media3.

Create:

```text
AudioPlayerManager
```

Capabilities:

- Play
- Pause
- Stop
- Resume
- Seek
- Previous
- Next

Playback must survive:

- screen rotation
- navigation changes

---

# FEATURE 3 - PLAY SURAH

Allow playback of complete Surah.

Display:

- Surah name
- Reciter name
- Playback position
- Remaining duration

User controls:

- Play
- Pause
- Resume
- Stop

---

# FEATURE 4 - PLAY SINGLE AYAH

When user long presses Ayah:

Display menu:

- Play Ayah
- Repeat Ayah
- Bookmark
- Start Memorization

Selecting Play Ayah should play only the selected Ayah.

---

# FEATURE 5 - PLAY RANGE

Allow user selection:

```text
Starting Ayah
Ending Ayah
```

Example:

```text
1 → 10
```

Play only the selected portion.

---

# FEATURE 6 - REPEAT ENGINE

Create configurable repeat system.

Supported values:

```text
1
3
5
10
Unlimited
```

Create delay engine.

Supported delays:

```text
0 sec
1 sec
2 sec
3 sec
5 sec
```

Example:

Ayah
↓
Pause 2 seconds
↓
Repeat
↓
Repeat
↓
Repeat

---

# FEATURE 7 - RANGE REPETITION

Example:

```text
Ayah 1 → Ayah 5
```

Repeat:

```text
1x
3x
5x
10x
```

The repeat engine must be reusable for future memorization features.

---

# FEATURE 8 - AUDIO TO AYAH SYNCHRONIZATION

Create synchronization engine.

Flow:

Audio Position
↓
Current Ayah
↓
Highlight Ayah
↓
Auto Scroll

Requirements:

- smooth
- accurate
- lightweight

No UI lag allowed.

---

# FEATURE 9 - CURRENT AYAH HIGHLIGHT

When reciter reaches Ayah:

```text
Current Ayah
```

must be visually highlighted.

Highlight should update automatically.

No manual refresh required.

---

# FEATURE 10 - AUTO SCROLL

Player automatically follows current Ayah.

Requirements:

Smooth scrolling.

No jitter.

No jumpy movement.

If user scrolls manually:

- suspend auto scroll
- show button

```text
Return to Current Ayah
```

---

# FEATURE 11 - MINI PLAYER

Create persistent mini player.

Visible during playback.

Display:

- Play / Pause
- Surah name
- Reciter name

Accessible from every screen.

---

# FEATURE 12 - FULL PLAYER

Create dedicated audio screen.

Display:

- Reciter photo
- Reciter name
- Surah name
- Progress
- Duration

Buttons:

- Previous
- Play
- Pause
- Next

Support RTL layout.

---

# FEATURE 13 - BACKGROUND PLAYBACK

Audio must continue when:

- App minimized
- User opens another app
- Device screen turns off

Playback must remain stable.

---

# FEATURE 14 - LOCK SCREEN CONTROLS

Implement lock screen controls.

Supported actions:

- Play
- Pause
- Previous
- Next

Display:

- Surah
- Reciter

---

# FEATURE 15 - MEDIA NOTIFICATION

Create Media3 notification.

Display:

- Reciter
- Surah

Controls:

- Play
- Pause
- Previous
- Next

Notification must remain synchronized with playback state.

---

# FEATURE 16 - AUDIO DOWNLOAD SYSTEM

Support downloads.

Create model:

```kotlin
data class DownloadedAudio(
    val reciterId: String,
    val surahId: Int,
    val localPath: String,
    val fileSize: Long,
    val status: DownloadStatus
)
```

Actions:

- Download
- Pause
- Resume
- Retry
- Delete

---

# FEATURE 17 - OFFLINE AUDIO

Downloaded audio must work:

- Without internet
- In airplane mode
- Without server access

Downloaded content must be prioritized over streaming.

---

# FEATURE 18 - DOWNLOAD CENTER

Create:

```text
Downloads Screen
```

Display:

- Downloaded Surahs
- Total Storage Usage
- Download Progress
- Failed Downloads

---

# FEATURE 19 - AUDIO CACHE

Create cache manager.

Capabilities:

- Cache audio
- Clear cache
- Show cache size

Requirements:

Never download identical audio twice.

Reuse existing file.

---

# FEATURE 20 - SETTINGS

Create Audio Settings section.

Options:

Default Reciter

Repeat Count

Repeat Delay

Streaming Quality

Auto Download

Cache Size

Clear Cache

Store all settings with DataStore.

---

# FEATURE 21 - ERROR HANDLING

Handle:

- No internet
- Server unavailable
- Download failure
- Storage full
- Corrupted file
- Timeout

Application must never crash.

Display clear Arabic messages.

---

# FEATURE 22 - PERFORMANCE RULES

CRITICAL

Never perform audio operations on Main Thread.

Never block UI.

Avoid memory leaks.

Avoid unnecessary recompositions.

Keep scrolling smooth.

Target:

60 FPS reader experience.

---

# FEATURE 23 - TESTING

Create tests for:

ReciterRepository

AudioPlayerManager

AudioState

RepeatEngine

AyahSynchronizationEngine

DownloadManager

OfflinePlayback

CacheManager

SettingsStorage

NotificationState

---

# COMPLETION CRITERIA

Phase 2 is complete only when:

✅ Multiple reciters work

✅ Surah playback works

✅ Ayah playback works

✅ Range playback works

✅ Repeat engine works

✅ Highlighted Ayah works

✅ Auto-scroll works

✅ Background playback works

✅ Notification works

✅ Lock screen controls work

✅ Downloads work

✅ Offline playback works

✅ Cache works

✅ Settings persist

✅ Tests pass

✅ Performance remains smooth

✅ RTL verified

✅ Accessibility checked

---

# FINAL DELIVERABLE

When Phase 2 is finished:

Generate report including:

- Files created
- Files modified
- Architecture decisions
- Dependencies added
- Test coverage
- Performance audit
- Pending improvements

DO NOT START PHASE 3 AUTOMATICALLY.

Wait for explicit instruction before implementing memorization features.