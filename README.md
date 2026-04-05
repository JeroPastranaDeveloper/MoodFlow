# MoodFlow

MoodFlow is an Android note-taking app with offline-first support, real-time Firebase sync, and per-note color customization.

---

## Features

- Email and password authentication (register / login / logout)
- Create, edit, delete, and pin notes
- Per-note background color selection with animated transition
- Staggered grid layout with shared element transitions between home and edit screens
- Full-text search with filters (content, pinned only, sort order)
- Multi-select mode: pin or delete multiple notes at once
- Offline-first: notes are saved locally and synced to Firebase when online
- Pending deletions are queued and executed on next connection
- Home screen widget: displays a scrollable list of notes with a configurable filter (all / pinned / normal)

---

## Tech stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose, Material 3 |
| Navigation | Navigation 3 (type-safe) |
| Architecture | MVI — `BaseViewModel` with `UiState`, `UiIntent`, `UiAction` |
| Dependency injection | Koin |
| Local database | Room (with migrations) |
| Remote database | Firebase Realtime Database |
| Authentication | Firebase Auth |
| Background sync | WorkManager (`SyncNotesWorker`) |
| Home screen widget | Jetpack Glance |
| Async | Kotlin Coroutines + Flow |
| Build system | Gradle convention plugins (AGP 9.x) |

---

## Architecture

MoodFlow follows **Clean Architecture** with a strict separation between layers:

```
app/
├── feature/          → UI layer (screens, ViewModels)
├── core/domain/      → Use cases and repository interfaces
├── core/data/        → Use case implementations, preferences
├── core/database/    → Firebase data source and repository implementation
├── core/localdatabase/ → Room database, DAOs, entities, migrations
├── core/model/       → Shared domain models (Note, User)
├── core/designsystem/ → Reusable Compose components and theming
├── core/navigation/  → Navigation destinations and animations
├── core/network/     → Network connectivity monitor
├── core/viewModel/   → Base ViewModel with MVI pattern
├── core/screen/      → Base screen utilities
├── core/utils/       → Extension functions
└── authentication/   → Firebase Auth data source and repository
```

### MVI pattern

Every screen follows the same contract:

```kotlin
class FooViewContract {
    data class UiState(...)      // immutable state rendered by the UI
    sealed class UiIntent(...)   // user actions sent to the ViewModel
    sealed class UiAction(...)   // one-shot events (navigation, toasts)
}
```

Each screen composable is split into two functions:

- **`MoodFlowFoo`** — connects the ViewModel: collects state, handles actions, sends intents, and delegates rendering to `Content`
- **`Content`** — pure composable that receives `UiState` and lambda callbacks, with no direct ViewModel dependency

### Offline-first sync

1. Every write (create / update / delete) is persisted to Room immediately with `pendingSync = true`
2. `WorkManager` schedules `SyncNotesWorker` with exponential backoff
3. On connectivity, the worker calls `syncPendingChanges()` which pushes all pending notes to Firebase
4. Firebase changes are observed via a real-time listener; incoming notes are merged into Room while preserving local data (color, pending changes)

### Room migrations

| Version | Change |
|---|---|
| 1 → 2 | Initial schema |
| 2 → 3 | Added `PendingDeletionEntity` table |
| 3 → 4 | Added `color: Long` column to `NoteEntity` |

---

## Module graph

```
app
 ├── feature:login
 ├── feature:register
 ├── feature:home
 ├── feature:editnote
 ├── feature:settings
 ├── core:navigation
 ├── core:designsystem
 ├── core:data
 ├── core:domain
 ├── core:database
 ├── core:localdatabase
 ├── core:network
 ├── core:viewmodel
 └── authentication
```

---

## Home screen widget

The app provides a Glance-based home screen widget that shows a scrollable list of notes.

- **Filter selection** — shown at widget placement time via `NotesWidgetConfigActivity`; the chosen filter (`ALL`, `PINNED`, or `NORMAL`) is persisted in `GlanceStateDefinition` (DataStore Preferences) and displayed in the widget header
- **Data** — fetched via `GetAllNotesUseCase`, which returns a `Flow<List<Note>>`; login state is checked via `PreferencesHandler.isLogged`
- **Unauthenticated state** — shows a dedicated message instead of notes
- **List layout** — each note and each divider are separate `LazyColumn` items; the divider after the last note is omitted
- **Tap behaviour** — tapping the header or any note opens `MainActivity`
- **Updates** — `NotesWidgetReceiver` triggers a re-render whenever the widget state changes; the widget is also updated programmatically after note edits

---

## Project setup

1. Clone the repository
2. Add your `google-services.json` to the `app/` directory
3. Enable **Email/Password** authentication in your Firebase project
4. Create a Firebase Realtime Database and set the rules to require authentication
5. Open the project in Android Studio Ladybug or later and run on a device with API 30+

### Requirements

- Android Studio Ladybug (2024.2) or later
- JDK 17
- Android API 30 (Android 11) minimum
- Firebase project with Realtime Database and Authentication enabled
