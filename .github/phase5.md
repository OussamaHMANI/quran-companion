# PHASE 5 - QIBLA SYSTEM

## OBJECTIVE

Implement a professional Qibla solution.

This phase introduces:

- Qibla Direction
- Compass Engine
- Sensor Integration
- Magnetic Calibration
- Accuracy Detection
- Offline Qibla
- Qibla Widget

The Qibla system must be:

- Accurate
- Fast
- Stable
- Offline
- Easy to understand

---

# IMPORTANT RULE

QIBLA ≠ MOSQUE

The Qibla points to:

```text
Kaaba
Makkah
Saudi Arabia
```

The Qibla must NEVER point to:

```text
Nearest mosque
```

Nearby mosques belong to Phase 6.

---

# PHASE 5 SCOPE

Implement ONLY:

- Qibla Engine
- Compass
- Calibration
- Direction Calculation
- Accuracy Detection
- Qibla Widget

Do NOT implement:

- Nearby Mosques

---

# FEATURE 1 - QIBLA SCREEN

Create dedicated screen:

```text
القبلة
```

Display:

- Kaaba icon
- Compass
- Direction
- Distance to Kaaba
- Accuracy status

---

# FEATURE 2 - GPS POSITION

Get user location.

Sources:

- GPS
- Network Location
- Last Known Location

Create:

```text
LocationProvider
```

---

# FEATURE 3 - QIBLA CALCULATION ENGINE

Create:

```text
QiblaEngine
```

Responsibilities:

- Calculate bearing
- Calculate Kaaba direction
- Update orientation

Input:

```text
Latitude
Longitude
```

Output:

```text
Bearing To Kaaba
```

---

# FEATURE 4 - KAABA COORDINATES

Use fixed coordinates for Kaaba.

Store in constants.

Never request from remote server.

Qibla must continue working offline.

---

# FEATURE 5 - COMPASS ENGINE

Create:

```text
CompassEngine
```

Use device sensors.

Support:

- Accelerometer
- Magnetometer

Compute:

```text
Current Device Heading
```

---

# FEATURE 6 - REAL TIME UPDATES

Update compass smoothly.

Requirements:

- Smooth rotation
- No jitter
- No visible lag

Avoid unnecessary sensor updates.

Optimize battery consumption.

---

# FEATURE 7 - KAABA INDICATOR

Show Kaaba direction clearly.

Example:

```text
🕋

↑
```

The user should instantly understand where to pray.

---

# FEATURE 8 - DISTANCE TO KAABA

Display:

```text
Distance To Kaaba
```

Example:

```text
4500 km
```

Calculate from current location.

---

# FEATURE 9 - DIRECTION ANGLE

Display:

```text
Qibla Angle

118°
```

Useful for advanced users.

---

# FEATURE 10 - CALIBRATION DETECTION

Detect poor compass calibration.

Show warning when required.

Example:

```text
Compass needs calibration
```

---

# FEATURE 11 - CALIBRATION GUIDE

Provide instructions.

Example:

```text
Move device in figure 8 pattern
```

Visual guide preferred.

---

# FEATURE 12 - ACCURACY LEVELS

Create levels:

```text
High

Medium

Low
```

Display current accuracy.

---

# FEATURE 13 - LOCATION PERMISSION

If location unavailable:

Show explanation.

Allow retry.

Never crash.

---

# FEATURE 14 - MANUAL LOCATION

Allow manual city selection.

Useful when GPS unavailable.

Qibla calculation must still work.

---

# FEATURE 15 - OFFLINE MODE

Qibla must work:

✅ Offline

✅ Airplane Mode

✅ No server connection

once location is available.

---

# FEATURE 16 - QIBLA HISTORY

Store:

```text
Last known location

Last direction

Calculation timestamp
```

Use as fallback.

---

# FEATURE 17 - QIBLA QUICK CARD

Display on home screen:

```text
Qibla
↑
```

Quick access.

---

# FEATURE 18 - QIBLA WIDGET

Small widget.

Display:

```text
Kaaba direction

Compass arrow
```

One tap opens Qibla screen.

---

# FEATURE 19 - QIBLA FULLSCREEN MODE

Dedicated fullscreen experience.

Display:

- Large compass
- Large Kaaba indicator
- Angle
- Distance

Minimal distractions.

---

# FEATURE 20 - ANIMATIONS

Use smooth motion.

Requirements:

- Lightweight
- GPU friendly
- Battery efficient

Avoid excessive visual effects.

---

# FEATURE 21 - BATTERY OPTIMIZATION

Compass should stop when:

- Screen hidden
- User leaves page

Restart when screen reopened.

---

# FEATURE 22 - ERROR HANDLING

Handle:

- Sensor unavailable
- GPS unavailable
- Permission denied
- Invalid coordinates

Always show recovery action.

Never crash.

---

# FEATURE 23 - ACCESSIBILITY

Support:

- RTL
- TalkBack
- Large fonts
- High contrast

All compass information must remain accessible.

---

# FEATURE 24 - PERFORMANCE

Requirements:

No UI Thread blocking.

No lag.

No sensor flooding.

Battery efficient.

Stable FPS.

---

# FEATURE 25 - DATABASE MODELS

Suggested:

```kotlin
QiblaSettings

LastKnownPosition

CompassState

QiblaCalculation
```

---

# FEATURE 26 - REPOSITORIES

Create:

```text
QiblaRepository

CompassRepository

LocationRepository
```

---

# FEATURE 27 - USE CASES

Create:

```text
GetQiblaDirectionUseCase

GetCurrentHeadingUseCase

CalculateBearingUseCase

GetDistanceToKaabaUseCase

CheckCalibrationUseCase
```

---

# FEATURE 28 - SETTINGS

Create settings section:

```text
Compass Sensitivity

Animations

Location Source

Auto Refresh
```

Persist with DataStore.

---

# FEATURE 29 - TESTING

Create tests for:

QiblaEngine

Bearing Calculation

Distance Calculation

Compass Engine

Accuracy Logic

Calibration Logic

Location Fallback

Offline Mode

Widget State

Error Handling

---

# FEATURE 30 - COMPLETION CRITERIA

Phase 5 complete only when:

✅ Qibla direction works

✅ Compass works

✅ GPS works

✅ Manual location works

✅ Calibration works

✅ Accuracy system works

✅ Offline mode works

✅ Distance calculation works

✅ Widget works

✅ Tests pass

✅ RTL verified

✅ Accessibility verified

✅ Performance reviewed

✅ Battery consumption reviewed

---

# FINAL DELIVERABLE

Generate report containing:

- Files created
- Files modified
- Architecture decisions
- Sensor strategy
- Bearing calculation strategy
- Calibration implementation
- Battery optimization strategy
- Test coverage
- Performance audit

DO NOT START PHASE 6 AUTOMATICALLY.

Wait for explicit instruction before implementing Nearby Mosques.