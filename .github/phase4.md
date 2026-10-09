# PHASE 4 - PRAYER TIMES SYSTEM

## OBJECTIVE

Implement a complete Prayer Times system for the application.

This phase introduces:

- Prayer Times
- Location Management
- Prayer Notifications
- Adhan System
- Prayer Widgets
- Prayer Settings

The Prayer feature must be:

- Accurate
- Reliable
- Offline-friendly
- Configurable
- Fast

This system should work seamlessly alongside Quran Reading and Hifdh.

---

# PHASE 4 SCOPE

Implement ONLY:

- Prayer Times
- Prayer Notifications
- Adhan
- Location
- Prayer Widgets
- Prayer Settings

DO NOT IMPLEMENT:

- Qibla
- Nearby Mosques

These belong to later phases.

---

# CORE PRINCIPLES

Prayer times must be:

- Accurate
- Configurable
- Respect local conventions

Users must always understand how prayer times are calculated.

Prayer functionality must never interfere with Quran functionality.

If Prayer features fail:

Quran and Hifdh must continue to work.

---

# FEATURE 1 - PRAYER SCREEN

Create dedicated navigation destination:

```text
الصلاة
```

Main screen displays:

```text
الفجر

الشروق

الظهر

العصر

المغرب

العشاء
```

Display today's times clearly.

---

# FEATURE 2 - NEXT PRAYER

Display:

```text
الصلاة القادمة
```

Example:

```text
المغرب

01:12:43 remaining
```

Automatically update countdown.

---

# FEATURE 3 - CURRENT PRAYER

Detect current prayer period.

Example:

```text
Current Prayer:

العصر
```

Highlight active prayer.

---

# FEATURE 4 - LOCATION MANAGEMENT

Support:

Automatic Location

Manual Location

Create:

```text
LocationRepository
```

Sources:

- GPS
- Manual city selection

User can change location anytime.

---

# FEATURE 5 - LOCATION PERMISSIONS

Before requesting location:

Explain purpose:

```text
يتم استخدام الموقع لحساب
مواقيت الصلاة والقبلة والمساجد القريبة
```

If denied:

App must continue working.

User can choose city manually.

Never force permission.

---

# FEATURE 6 - MANUAL CITY SELECTION

Allow:

Country

Region

City

Example:

```text
France

Île-de-France

Pontoise
```

Store selection locally.

---

# FEATURE 7 - PRAYER TIME ENGINE

Create:

```text
PrayerTimeEngine
```

Responsibilities:

- Generate prayer times
- Handle date changes
- Handle timezone changes
- Handle location changes

Engine must be isolated from UI.

---

# FEATURE 8 - CALCULATION METHODS

Allow changing calculation method.

Examples:

```text
Muslim World League

Egyptian

Umm Al-Qura

ISNA

Gulf Region

France Mosque Method
```

Store selected method.

Recalculate instantly.

---

# FEATURE 9 - PRAYER ADJUSTMENTS

Allow manual offsets.

Example:

```text
Fajr +2 min

Maghrib -1 min
```

Store locally.

Apply transparently.

---

# FEATURE 10 - DAILY PRAYER VIEW

Display:

Prayer Name

Prayer Time

Status

Example:

```text
الفجر ✅

06:15
```

Visual indicator:

Past

Current

Upcoming

---

# FEATURE 11 - WEEKLY VIEW

Display prayer schedule for:

7 days

User can navigate future days.

---

# FEATURE 12 - MONTHLY VIEW

Display monthly calendar.

Each day shows:

Fajr

Maghrib

and main prayer information.

---

# FEATURE 13 - HIJRI DATE

Display:

Hijri Date

Gregorian Date

Example:

```text
15 رمضان 1448

26 February 2027
```

---

# FEATURE 14 - ISLAMIC CALENDAR

Create Islamic Calendar page.

Display:

- Today Hijri Date
- Important Islamic dates
- Current Month

---

# FEATURE 15 - ADHAN SYSTEM

Create Adhan Engine.

Supported events:

Fajr

Dhuhr

Asr

Maghrib

Isha

At prayer time:

Play selected Adhan.

---

# FEATURE 16 - MULTIPLE ADHAN SOUNDS

Allow user selection.

Examples:

```text
Mecca Adhan

Madinah Adhan

Short Adhan

Notification Only
```

Store preference.

---

# FEATURE 17 - ADHAN PREVIEW

Allow testing Adhan.

Button:

```text
استماع
```

User can preview before selecting.

---

# FEATURE 18 - PRAYER NOTIFICATIONS

Create configurable notifications.

Examples:

```text
الفجر بعد 10 دقائق

المغرب الآن

حان وقت الصلاة
```

---

# FEATURE 19 - REMINDER SETTINGS

Allow:

```text
5 minutes before

10 minutes before

15 minutes before

30 minutes before
```

Or disabled.

---

# FEATURE 20 - SILENT MODE BEHAVIOR

User chooses:

```text
Play Adhan anyway

Respect silent mode

Notification only
```

Persist preferences.

---

# FEATURE 21 - PRAYER WIDGET

Create Android Home Widget.

Display:

Current Date

Next Prayer

Countdown

Prayer Time

Widget updates automatically.

---

# FEATURE 22 - SMALL WIDGET

Compact version.

Display:

```text
Maghrib

18:42
```

Only essential information.

---

# FEATURE 23 - LARGE WIDGET

Display:

All daily prayers

Next prayer

Countdown

Hijri date

---

# FEATURE 24 - QUICK SETTINGS TILE

Optional.

Android quick tile showing:

```text
Next prayer
```

---

# FEATURE 25 - OFFLINE SUPPORT

Prayer times must continue working offline.

No internet required.

Using:

Stored location

Stored calculation settings

Stored timezone

---

# FEATURE 26 - TIMEZONE CHANGES

Detect changes.

Recalculate quickly.

Handle travel situations.

---

# FEATURE 27 - DAYLIGHT SAVING TIME

Handle:

Summer Time

Winter Time

Automatically.

---

# FEATURE 28 - TRAVEL MODE

When large location change detected:

Offer prayer recalculation.

Example:

```text
It looks like you travelled.

Update prayer times?
```

---

# FEATURE 29 - HOME SCREEN INTEGRATION

Display:

```text
Next Prayer

Countdown
```

directly on Home.

---

# FEATURE 30 - FAVORITE LOCATIONS

Allow saving:

Home

Work

Travel

Favorite Cities

Fast switching.

---

# FEATURE 31 - ACCESSIBILITY

Support:

Large Text

TalkBack

High Contrast

RTL

Screen Readers

---

# FEATURE 32 - PERFORMANCE

Requirements:

No expensive calculations on UI Thread.

No visible lag.

No battery drain.

Update countdown efficiently.

Minimize background work.

---

# FEATURE 33 - DATABASE MODELS

Suggested models:

```kotlin
PrayerSettings

PrayerMethod

PrayerSchedule

PrayerNotification

SavedLocation

AdhanPreference
```

---

# FEATURE 34 - REPOSITORIES

Create:

```text
PrayerRepository

LocationRepository

NotificationRepository
```

---

# FEATURE 35 - USE CASES

Examples:

```text
GetTodayPrayersUseCase

GetNextPrayerUseCase

CalculatePrayerTimesUseCase

SavePrayerMethodUseCase

GetHijriDateUseCase

SchedulePrayerNotificationUseCase
```

---

# FEATURE 36 - SETTINGS

Create Prayer Settings page.

Sections:

Location

Calculation Method

Adhan

Notifications

Offsets

Widget Options

---

# FEATURE 37 - ERROR HANDLING

Handle:

Location unavailable

GPS disabled

Permission denied

Prayer calculation failure

Timezone errors

Corrupted settings

Never crash.

Always display useful recovery actions.

---

# FEATURE 38 - TESTING

Create tests for:

Prayer Engine

Prayer Calculations

Location Updates

Timezone Changes

Adhan Scheduling

Notification Scheduling

Countdown Logic

Prayer Status Logic

Manual City Selection

Offline Mode

Widget Updates

---

# COMPLETION CRITERIA

Phase 4 is complete only when:

✅ Daily prayer times work

✅ Next prayer works

✅ Countdown works

✅ Manual city selection works

✅ Location works

✅ Calculation methods work

✅ Offsets work

✅ Adhan works

✅ Notifications work

✅ Widgets work

✅ Offline mode works

✅ Timezone handling works

✅ Tests pass

✅ RTL verified

✅ Accessibility verified

✅ Performance reviewed

---

# FINAL DELIVERABLE

At completion generate report including:

- Files created
- Files modified
- Architecture decisions
- Prayer calculation strategy
- Notification strategy
- Widget implementation
- Test coverage
- Performance audit

DO NOT START PHASE 5 AUTOMATICALLY.

Wait for explicit instruction before implementing Qibla functionality.