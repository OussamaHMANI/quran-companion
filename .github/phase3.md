# PHASE 3 - HIFDH & MEMORIZATION SYSTEM

## OBJECTIVE

Implement the most important differentiating feature of the application:

QURAN MEMORIZATION (HIFDH).

This phase transforms the application from a Quran Reader into a complete Quran Memorization Platform.

The memorization experience must be:

- Simple
- Efficient
- Motivating
- Scientifically structured
- Offline-first
- Highly customizable

The goal is to help users memorize and review Quran effectively.

---

# PHASE 3 SCOPE

Implement ONLY:

- Memorization modes
- Review engine
- Memorization statistics
- Progress tracking
- Spaced repetition

Do NOT implement:

- Prayer Times
- Qibla
- Nearby Mosques

Those belong to later phases.

---

# CORE PRINCIPLES

Memorization must not feel like a game.

Avoid unnecessary gamification.

This application is for:

- Hifdh
- Revision
- Consistency
- Retention

The Quran remains the center of the experience.

---

# QURAN SAFETY RULE

CRITICAL

Never modify Quran text.

Never generate Quran verses.

Never auto-correct Quran text.

Never save altered Quran text.

All hiding effects must be visual only.

Canonical Quran text must remain untouched.

---

# FEATURE 1 - HIFDH SECTION

Create dedicated navigation destination:

```text
الحفظ
```

This screen becomes the entry point for all memorization activities.

Display:

- Continue session
- New session
- Review session
- Statistics

---

# FEATURE 2 - CREATE MEMORIZATION SESSION

User selects:

```text
Surah

Starting Ayah

Ending Ayah

Reciter

Repeat Count
```

Example:

```text
Al-Baqara

Ayah 1

Ayah 5

Ajmi

5 repetitions
```

Create:

```kotlin
MemorizationSession
```

Model should store:

- sessionId
- surahId
- startAyah
- endAyah
- reciterId
- createdAt

---

# FEATURE 3 - MEMORIZATION MODE A

READ + LISTEN

Workflow:

1. Display Ayah.
2. Play audio.
3. Highlight Ayah.
4. Repeat according to settings.

User can:

- replay
- next
- previous

Goal:

Initial exposure.

---

# FEATURE 4 - MEMORIZATION MODE B

LISTEN THEN RECITE

Workflow:

1. Display Ayah.
2. Play recitation.
3. Wait delay.
4. Hide Ayah.
5. Ask user to recite.
6. User taps reveal.

Button:

```text
إظهار الآية
```

Goal:

Force memory retrieval.

---

# FEATURE 5 - MEMORIZATION MODE C

PROGRESSIVE HIDING

Do not modify Quran text.

Only modify rendering layer.

Level 1:

100% visible

Level 2:

75% visible

Level 3:

50% visible

Level 4:

25% visible

Level 5:

0% visible

Example:

Level 1

الحمد لله رب العالمين

Level 2

الحمد ___ رب العالمين

Level 3

_____ ___ رب ________

Level 4

_____ ___ ___ ________

Level 5

••••••••••••••••••••••

Goal:

Strengthen memorization gradually.

---

# FEATURE 6 - MEMORIZATION MODE D

GROUP MEMORIZATION

User selects range:

```text
1 -> 5
```

Workflow:

Ayah 1 repeated

Ayah 2 repeated

Ayah 3 repeated

Ayah 4 repeated

Ayah 5 repeated

Then:

Ayah 1-5 together

Repeat entire group.

Goal:

Chain memorization.

---

# FEATURE 7 - PERSONALIZED REPETITION

Allow:

Ayah Repeat

Values:

1

3

5

10

Unlimited

Allow:

Pause Delay

Values:

0 sec

1 sec

2 sec

3 sec

5 sec

Persist preferences.

---

# FEATURE 8 - MEMORIZATION STATUS

Every Ayah can have state.

Create enum:

```kotlin
NEW

LEARNING

REVIEW

MEMORIZED
```

Arabic labels:

```text
جديد

قيد الحفظ

مراجعة

محفوظ
```

Store locally.

---

# FEATURE 9 - DIFFICULTY RATING

After each review:

Ask:

```text
كيف كانت المراجعة؟
```

Options:

```text
صعب

متوسط

سهل
```

Store result.

This rating influences future review scheduling.

---

# FEATURE 10 - SPACED REPETITION ENGINE

Create standalone review engine.

No UI logic.

Independent module.

Default schedule:

Day 0

Day 1

Day 3

Day 7

Day 14

Day 30

Day 60

Day 90

Algorithm adapts based on difficulty rating.

---

# FEATURE 11 - REVIEW GENERATOR

Every day generate list:

```text
Verses due today
```

Example:

```text
15 Ayahs pending review
```

User can:

```text
Start Review
```

in one tap.

---

# FEATURE 12 - REVIEW SESSION

Dedicated review workflow.

Display:

Ayah

Hide Ayah

User recalls

Reveal

Rate

Continue

Repeat

Goal:

Efficient revision.

---

# FEATURE 13 - DAILY GOALS

Optional feature.

User chooses:

```text
1 Ayah

3 Ayahs

5 Ayahs

10 Ayahs
```

Display progress.

Never penalize missed days.

---

# FEATURE 14 - MEMORIZATION DASHBOARD

Create dashboard.

Display:

- Total Memorized
- Learning
- Review Due
- Last Session
- Current Streak
- Estimated Progress

---

# FEATURE 15 - SURAH PROGRESS

Example:

```text
Al-Baqara

██████████░░░░░░░

45%
```

Display:

Memorized

Learning

Review

Remaining

---

# FEATURE 16 - JUZ PROGRESS

Display progress by:

- Juz
- Hizb
- Surah

Allow quick navigation.

---

# FEATURE 17 - REVISION CALENDAR

Create calendar view.

Display:

Reviewed Days

Missed Days

Upcoming Reviews

Daily activity

---

# FEATURE 18 - FAVORITE MEMORIZATION LISTS

Allow user to save collections.

Example:

```text
Juz Amma

Morning Review

Weak Ayahs

Daily Revision
```

---

# FEATURE 19 - WEAK AYAH DETECTION

If user repeatedly marks:

```text
صعب
```

Mark Ayah as weak.

Add to:

```text
Needs More Revision
```

list.

---

# FEATURE 20 - MEMORIZATION HISTORY

Store:

- Session date
- Duration
- Ayahs studied
- Ayahs reviewed

Allow browsing history.

---

# FEATURE 21 - OFFLINE REQUIREMENTS

Everything must work offline:

- Sessions
- Reviews
- Progress
- Statistics
- Dashboard
- Scheduling

Network must never be required.

---

# FEATURE 22 - NOTIFICATIONS

Optional reminders.

Examples:

```text
موعد المراجعة اليوم

لديك 12 آية للمراجعة

حان وقت الحفظ
```

User can disable.

---

# FEATURE 23 - STATISTICS

Create advanced statistics.

Display:

Total Ayahs Memorized

Total Reviews

Hours Studied

Average Ayahs Per Day

Review Success Rate

Most Active Surahs

Most Difficult Surahs

---

# FEATURE 24 - SEARCH IN MEMORIZED CONTENT

Allow user to search:

- Memorized Ayahs
- Learning Ayahs
- Review Queue

---

# FEATURE 25 - EXPORT / BACKUP

Prepare architecture for future backup.

Create abstraction.

Do not implement cloud backup yet.

---

# FEATURE 26 - ACCESSIBILITY

Must support:

- Large fonts
- TalkBack
- RTL
- High contrast

All memorization modes must remain accessible.

---

# FEATURE 27 - PERFORMANCE

Critical requirements:

No heavy work on UI thread.

No UI freezes.

No lag during hiding transitions.

No excessive recompositions.

Review engine must be lightweight.

Store data efficiently.

---

# FEATURE 28 - DATABASE MODELS

Suggested models:

```kotlin
MemorizationSession

MemorizationProgress

ReviewSchedule

ReviewHistory

DifficultyRecord

DailyGoal

SessionStatistics
```

---

# FEATURE 29 - REPOSITORIES

Create:

```text
MemorizationRepository

ReviewRepository

StatisticsRepository
```

Do not place logic inside ViewModels.

---

# FEATURE 30 - USE CASES

Examples:

```text
CreateSessionUseCase

StartReviewUseCase

RateAyahUseCase

GenerateReviewUseCase

UpdateProgressUseCase

GetStatisticsUseCase
```

---

# FEATURE 31 - TESTING

Create tests for:

Session Creation

Progress Tracking

Review Scheduling

Difficulty Rating

Weak Ayah Detection

Statistics

Dashboard Calculations

History

Daily Goals

Reminder Generation

Offline Behavior

---

# COMPLETION CRITERIA

Phase 3 is complete only when:

✅ Memorization sessions work

✅ Mode A works

✅ Mode B works

✅ Mode C works

✅ Mode D works

✅ Review engine works

✅ Difficulty tracking works

✅ Daily reviews work

✅ Dashboard works

✅ Statistics work

✅ Offline support works

✅ Progress persistence works

✅ Tests pass

✅ RTL verified

✅ Accessibility reviewed

✅ Performance reviewed

---

# FINAL DELIVERABLE

At completion:

Generate report including:

- files created
- files modified
- architecture choices
- tests written
- review algorithm explanation
- database schema
- performance analysis

DO NOT START PHASE 4 AUTOMATICALLY.

Wait for explicit instruction before implementing Prayer Times.