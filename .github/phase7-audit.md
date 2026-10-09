# Phase 7 Release Audit

**Audit date:** 2026-10-09 18:31 (local)  
**Verdict:** Build-clean candidate; production release sign-off remains open.

## Verified

- `:app:assembleDebug`, `:app:testDebugUnitTest`, and `:app:lintDebug` pass on the audited source.
- The final debug APK installs and launches on BlueStacks API 28. The latest `adb shell am start -W` reported **1,796 ms** (`TotalTime`). The Quran database was already initialized, so this does not measure first-install import. Debug-emulator samples have varied substantially and do not establish startup performance on physical devices.
- The app remained in the foreground after launch; no Android runtime error was recorded for its process.
- APK size: **56,592,021 bytes** (about 56.6 MB decimal). The only file under `res/raw` is the alternate `adhan_other.mp3`.
- Startup no longer pre-imports the Quran dataset from `Application.onCreate`; the Home dashboard renders before Quran-dependent tabs load. Monthly prayer schedules and the Media3 player/cache are also created on demand.
- Tasmii3 keeps the current ayah on mismatch, displays the incorrect transcript during an automatic retry, retries after mismatch/uncertainty/no speech, and advances only after a complete accepted match.
- The mosque map now uses OSMDroid's native OSM MAPNIK tile renderer/cache, with tactile zoom/pan, user/mosque markers, attribution, retry, and open-map actions. Real OSM tiles rendered in the emulator smoke test.
- Nearby Mosques city selection followed immediately by List displayed the first completed city's results without a second attempt. Search completion now ignores stale location/radius responses.
- Monthly prayer view supports month navigation, six daily prayer/sunrise times, and selected-month Hijri observances with an estimate caveat. Hijri date conversion has unit coverage.
- Large reader, index, prayer, memorization, mosque, bookmark, and download lists use Compose lazy layouts. Room has indexes for ayah page/Juz/surah ordering, bookmark ayah IDs, memorization session recency, and review ayah/date access.
- Qibla sensor collection now starts/stops with the screen lifecycle, including app backgrounding.
- Manifest disables Android auto-backup. Source review found no analytics SDK or logging calls for precise location. Quran text remains outside the user backup payload.
- Backup import bounds input size, validates records before applying them, and applies Room-owned data in a transaction. Default-reciter restore now removes the prior preference when the imported value is absent; focused unit tests cover both removal and replacement.

## Release Gates Still Open

- No full backup export/import round-trip or rollback test against Room/DataStore; exported JSON is not encrypted and can contain private progress, settings, prayer location, and mosque favorites. Treat the chosen SAF file as sensitive.
- No TalkBack, large-font, high-contrast, tablet, or foldable hands-on audit was run.
- No CPU/RAM profiler session, recomposition/frame-time capture, long-scroll performance test, or audio interruption/leak test was run. The startup sample alone does not establish the under-two-second target across devices.
- BlueStacks does not provide a useful compass sensor. Qibla heading/accuracy requires a physical-device check.
- Exact-time Adhan delivery and background playback were not exercised on hardware. Exact alarms require the Android “Alarms & reminders” access on supported versions; fallback scheduling may be delayed.
- No analytics or crash-reporting SDK is present. This avoids telemetry collection today, but an explicit consent/privacy decision and an opt-in design are still needed before adding either.
- Physical-device testing of OSM navigation and tile behavior under weak/intermittent networks remains open.

## Recommendation

Keep this build as a debug validation artifact, not a Play Store release. Close the device-dependent and accessibility gates, add backup repository round-trip/rollback coverage, and decide the privacy/consent policy for analytics and crash reporting before production sign-off.
