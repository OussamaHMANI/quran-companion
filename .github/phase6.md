# PHASE 6 - NEARBY MOSQUES SYSTEM

## OBJECTIVE

Implement a complete Nearby Mosques feature.

This phase introduces:

- Nearby Mosque Discovery
- Mosque Maps
- Mosque Details
- Mosque Navigation
- Mosque Favorites
- Advanced Search
- Mosque Filters

The objective is to help users quickly locate mosques around them.

This feature must be:

- Fast
- Accurate
- Easy to use
- Battery efficient
- Accessible
- RTL optimized

---

# IMPORTANT RULE

NEARBY MOSQUES ≠ QIBLA

Qibla:

Points toward the Kaaba.

Mosque feature:

Finds nearby mosques.

These are independent systems.

Never combine them.

---

# PHASE 6 SCOPE

Implement ONLY:

- Nearby Mosques
- Maps
- Mosque Search
- Mosque Details
- Favorites
- Navigation

Do NOT implement:

- New Quran features
- New Hifdh features
- New Qibla features

---

# FEATURE 1 - MOSQUE SCREEN

Create navigation destination:

```text
المساجد
```

Display:

- Map View
- List View
- Search
- Favorites

---

# FEATURE 2 - MOSQUE MODEL

Create:

```kotlin
data class Mosque(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val phone: String?,
    val website: String?,
    val distanceMeters: Double,
    val rating: Float?,
    val isFavorite: Boolean
)
```

---

# FEATURE 3 - MOSQUE REPOSITORY

Create:

```text
MosqueRepository
```

Capabilities:

```text
Search Nearby Mosques

Get Mosque Details

Save Favorite

Remove Favorite

Get Favorites
```

---

# FEATURE 4 - USER LOCATION

Reuse existing location architecture.

Support:

- GPS
- Network Location
- Manual Location

Location updates must be efficient.

---

# FEATURE 5 - FIND NEAREST MOSQUES

Display:

```text
مسجد النور

250 m

[ الاتجاهات ]
```

Sort by distance.

Nearest mosque appears first.

---

# FEATURE 6 - DISTANCE CALCULATION

Show:

```text
Meters

Kilometers
```

Examples:

```text
250 m

1.3 km

5.4 km
```

Auto format.

---

# FEATURE 7 - LIST VIEW

Display:

Mosque Name

Distance

Address

Favorite Button

Directions Button

Simple clean design.

---

# FEATURE 8 - MAP VIEW

Create interactive map.

Display:

- User position
- Mosque markers
- Distance

Selecting a marker:

opens mosque details.

---

# FEATURE 9 - MOSQUE DETAIL SCREEN

Display:

```text
Mosque Name

Address

Distance

Directions

Call

Website

Favorite
```

---

# FEATURE 10 - MOSQUE INFORMATION

When available show:

- Name
- Address
- Phone
- Website
- Distance
- Coordinates

Handle missing information gracefully.

---

# FEATURE 11 - SEARCH

Search mosques by:

```text
Name

City

Address
```

Support Arabic search.

Support Unicode properly.

---

# FEATURE 12 - FILTERS

Allow filtering.

Examples:

```text
Distance

Rating

Favorites
```

Architecture must allow future filters.

---

# FEATURE 13 - FAVORITES

User can:

Save favorite mosque.

Remove favorite mosque.

Access favorite list.

Store locally.

---

# FEATURE 14 - FAVORITES SCREEN

Display:

Saved mosques.

Quick navigation.

Quick directions.

Offline availability.

---

# FEATURE 15 - DIRECTIONS

Button:

```text
الاتجاهات
```

Launch navigation app.

Support:

- Google Maps
- Waze
- Default Navigation

---

# FEATURE 16 - EXTERNAL NAVIGATION

Pass:

Latitude

Longitude

Safely validate coordinates.

Never crash if navigation app unavailable.

---

# FEATURE 17 - OFFLINE BEHAVIOR

If internet unavailable:

Show:

- Favorites
- Last search results
- Saved mosques

Gracefully degrade features.

---

# FEATURE 18 - CACHE

Create local cache.

Store:

- Recent search results
- Mosque details
- Favorites

Minimize API calls.

---

# FEATURE 19 - RECENT SEARCHES

Store recent searches.

Display:

```text
Recent Mosques

Recent Cities
```

Allow clear history.

---

# FEATURE 20 - HOME INTEGRATION

Allow optional Home widget:

```text
Nearest Mosque

450 m
```

One tap navigation.

---

# FEATURE 21 - QUICK ACTIONS

Mosque card actions:

```text
Directions

Call

Favorite
```

Accessible with one tap.

---

# FEATURE 22 - CITY BROWSING

Allow exploring mosques in:

- Current city
- Other cities

Even without GPS.

---

# FEATURE 23 - ACCESSIBILITY

Support:

- Large fonts
- Screen readers
- RTL
- High contrast

All map alternatives must have text versions.

---

# FEATURE 24 - RTL SUPPORT

Verify:

- Map controls
- Search field
- Details view
- Filters
- Favorites

Everything must be RTL friendly.

---

# FEATURE 25 - BATTERY OPTIMIZATION

Avoid constant GPS requests.

Use smart refresh intervals.

Do not request location unnecessarily.

---

# FEATURE 26 - PERFORMANCE

Requirements:

- Fast search
- Smooth scrolling
- Fast map rendering
- Cached results

No blocking UI thread.

---

# FEATURE 27 - ERROR HANDLING

Handle:

GPS unavailable

Permission denied

Internet unavailable

Map unavailable

Navigation unavailable

Provider timeout

Invalid data

Never crash.

Always offer recovery options.

---

# FEATURE 28 - SECURITY

Validate coordinates.

Validate URLs.

Validate phone numbers.

Never trust external API responses blindly.

---

# FEATURE 29 - DATABASE MODELS

Suggested:

```kotlin
FavoriteMosque

RecentSearch

CachedMosque

MosqueSearchHistory

MosqueDetails
```

---

# FEATURE 30 - REPOSITORIES

Create:

```text
MosqueRepository

FavoriteMosqueRepository

MapRepository
```

---

# FEATURE 31 - USE CASES

Examples:

```text
SearchNearbyMosquesUseCase

GetMosqueDetailsUseCase

AddFavoriteMosqueUseCase

RemoveFavoriteMosqueUseCase

GetFavoriteMosquesUseCase

LaunchDirectionsUseCase
```

---

# FEATURE 32 - SETTINGS

Create Mosque Settings.

Options:

```text
Distance Unit

Auto Refresh

Map Type

Show Favorites First
```

Persist with DataStore.

---

# FEATURE 33 - STATISTICS

Optional.

Display:

Favorite Mosques

Most Visited Mosques

Recent Searches

---

# FEATURE 34 - TESTING

Create tests for:

Distance Calculation

Search Logic

Filtering

Favorites

Cache

Navigation Launch

Offline Mode

Mosque Repository

Error Handling

RTL Layouts

---

# FEATURE 35 - COMPLETION CRITERIA

Phase 6 is complete only when:

✅ Nearby mosques can be found

✅ Distance calculation works

✅ List view works

✅ Map view works

✅ Search works

✅ Favorites work

✅ Directions work

✅ Offline cache works

✅ Error states handled

✅ Performance reviewed

✅ RTL verified

✅ Accessibility verified

✅ Tests pass

---

# FINAL DELIVERABLE

Generate report including:

- Files created
- Files modified
- Mosque provider strategy
- Caching strategy
- Search strategy
- Favorite architecture
- Navigation integration
- Performance analysis
- Test coverage

DO NOT START PHASE 7 AUTOMATICALLY.

Wait for explicit instruction before implementing final optimization and release phase.