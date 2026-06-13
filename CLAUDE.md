# MoodFlow — Claude context

This file gives you everything you need to work on this project without asking the user for context.

---

## What the app does

Android note-taking app. Users register/login with Firebase Auth (email/password or Google Sign-In), then create, edit, delete, and pin notes. Notes have a color (Long ARGB), are stored locally in Room, and synced to Firebase Realtime Database. Offline writes are queued and flushed by WorkManager when connectivity returns.

---

## Build system

- **AGP 9.x + Kotlin 2.3.x** — several breaking changes are already applied, do not revert them:
  - `buildFeatures { compose = true }` is removed — the Compose compiler plugin handles this
  - `CommonExtension` is no longer generic — use `ApplicationExtension` / `LibraryExtension` directly
  - `configureKotlinAndroid` takes `KotlinAndroidProjectExtension`, not `CommonExtension`
  - Compiler flags use the DSL: `optIn.addAll(...)`, `freeCompilerArgs.add(...)` — never `kotlinOptions { freeCompilerArgs += [...] }`
  - Context receivers: use `-Xcontext-parameters` and `context(name: Type)` syntax, NOT `-Xcontext-receivers` / `context(Type)`
  - `fallbackToDestructiveMigrationFrom` requires `dropAllTables = true` named parameter

- Convention plugins live in `buildlogic/convention/src/main/kotlin/`
  - `AndroidApplicationConventionPlugin` — app module
  - `AndroidLibraryConventionPlugin` — library modules
  - `AndroidFeatureConventionPlugin` — feature modules (calls `configureAndroidCompose()`)
  - `AndroidKoinConventionPlugin` — adds Koin

---

## Architecture

### Layer map

```
core:model          → Note, User (Parcelable domain models, shared everywhere)
core:domain         → Repository interfaces, use case interfaces/impls, validators
core:data           → Auth use case impls, PreferencesHandlerImpl, StringsProviderImpl, DI modules
authentication      → FirebaseAuthDataSourceImpl, AuthRepositoryImpl, GetGoogleIdTokenUseCaseImpl
core:database       → NotesDataSourceImpl (Firebase), NotesRepositoryImpl, SyncNotesWorker
core:localdatabase  → Room: NoteDatabase, NoteDao, NoteEntity, PendingDeletionEntity, migrations
core:network        → NetworkMonitor (connectivity)
core:viewModel      → BaseViewModel, BaseViewModelWithActions
core:designsystem   → Compose components, MoodFlowColors, NoteColors, theme
core:navigation     → MoodFLowScreen (NavKey destinations), navigation utils
core:screen         → HandleActions, SetStatusBarIconsColor, getTopSystemPadding
feature:login       → MoodFlowLogin, LoginViewModel, LoginViewContract
feature:register    → MoodFlowRegister, RegisterViewModel, RegisterViewContract
feature:home        → MoodFlowHome, HomeViewModel, HomeViewContract
feature:editnote    → MoodFlowEditNote, EditNoteViewModel, EditNoteViewContract
feature:settings    → MoodFlowSettings, SettingsViewModel, SettingsViewContract
app                 → MainActivity, MoodFlowNavigation, MainViewModel, Koin app setup, NotesWidget (Glance)
```

### MVI pattern

Every feature has a `ViewContract` file with three classes:

```kotlin
class FooViewContract {
    data class UiState(...)      // immutable snapshot rendered by Compose
    sealed class UiIntent(...)   // user actions → sent via viewModel.sendIntent(...)
    sealed class UiAction(...)   // one-shot side effects (navigation, toasts)
}
```

ViewModels extend:
- `BaseViewModel<S, I>` — state + intent channel
- `BaseViewModelWithActions<S, I, A>` — adds `dispatchAction(action)` for one-shot events

`BaseViewModel` queues intents through an unlimited `Channel` and calls `manageIntent(intent)` for each. `setState { copy(...) }` updates the `StateFlow`.

`BaseViewModelWithActions` buffers actions if there are no subscribers yet (handles the race between ViewModel init and UI subscription).

### Composable structure — the Content pattern

Every screen composable is split into two functions. **Always follow this pattern when adding or modifying screens.**

```kotlin
// Public — wires ViewModel
@Composable
fun MoodFlowFoo(
    viewModel: FooViewModel = koinViewModel(),
    onGoSomewhere: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    // BackHandler here if needed (before HandleActions)
    BackHandler { viewModel.sendIntent(UiIntent.OnGoBack) }

    HandleActions(viewModel.actions) { action ->
        when (action) { ... }
    }

    Content(
        state = state,
        onSomething = { viewModel.sendIntent(UiIntent.OnSomething) },
        ...
    )
}

// Private — pure UI, no ViewModel reference
@Composable
private fun Content(
    state: UiState,
    onSomething: () -> Unit,
    ...
) {
    // focusManager, isKeyboardOpen, LaunchedEffect, animateColorAsState, etc. go here
}
```

Screens that use `SharedTransitionScope` make both functions extension functions:
```kotlin
@Composable
fun SharedTransitionScope.MoodFlowFoo(...) { ... }

@Composable
private fun SharedTransitionScope.Content(...) { ... }
```

---

## Key domain model

```kotlin
// core:model
data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: Long = 0,
    val pinned: Boolean = false,
    val color: Long = 0L,        // 0L = white (default), otherwise full ARGB Long e.g. 0xFFFFF9C4L
    val userId: String = "",
    val pendingSync: Boolean = false,
    val deletedAt: Long? = null, // null = active note; non-null = in trash (epoch ms)
) : Parcelable
```

Same fields exist on `NoteEntity` (Room) and `NoteDto` (Firebase). Mappers in `core:database/mapper/toDomain.kt` convert between all three. **Always keep all three in sync when adding fields.**

### Note colors

`NoteColors` in `core:designsystem`:
- `palette: List<Long>` — 8 ARGB longs (0L = white/default)
- `toComposeColor(colorLong: Long): Color` — converts to Compose Color; 0L → `Color.White`

Color is stored as `Long` in Room and Firebase. Conversion uses `colorLong.toInt()` which is safe for full ARGB values above `0x7FFFFFFF`.

---

## Room database

- Class: `NoteDatabase` — current version **5**
- Migrations in `core:localdatabase/migrations/Migrations.kt`:
  - `MIGRATION_2_3` — creates `PendingDeletionEntity` table
  - `MIGRATION_3_4` — adds `color INTEGER NOT NULL DEFAULT 0` to `NoteEntity`
  - `MIGRATION_4_5` — adds `deletedAt INTEGER DEFAULT NULL` to `NoteEntity`
  - Version 1 uses `fallbackToDestructiveMigrationFrom(dropAllTables = true, 1)`
- **Always add a migration when changing any entity.** Never bump the version without a migration (except from 1).

---

## Firebase

- **Realtime Database** — notes stored at `users/{userId}/notes/{noteId}`
- **Auth** — email/password and Google Sign-In (via CredentialManager + `GetSignInWithGoogleOption`)
- Google Sign-In requires enabling the provider in Firebase Console → Authentication → Sign-in methods, and adding the app's SHA-1 fingerprint under Authentication → Settings
- `NotesDataSourceImpl` uses `getValue(NoteDto::class.java)` for deserialization — Firebase maps fields by name, so field names in `NoteDto` must match the database keys
- `NotesRepositoryImpl.getAllNotes` runs three concurrent coroutines:
  1. Room flow → emit to UI immediately
  2. Network monitor → trigger sync on reconnection
  3. Firebase listener → merge into Room (preserving local color if Firebase has 0L)

---

## Offline sync

Flow for any write:
1. Write to Room with `pendingSync = true`
2. Call `scheduleSyncWork()` → enqueues `SyncNotesWorker` via WorkManager (REPLACE policy, exponential backoff, requires network)
3. Worker calls `notesRepository.syncPendingChanges()` which pushes all pending notes then pending deletions to Firebase
4. On success, marks note as synced (`pendingSync = false`)

`PendingDeletionEntity` tracks notes that were deleted locally but not yet deleted in Firebase.

---

## Trash (soft delete)

### How it works

- **Deleting** a note from the home screen calls `NotesRepository.deleteNote` → sets `deletedAt = System.currentTimeMillis()` and `pendingSync = true` locally. The note is **not** removed from Firebase immediately; `SyncNotesWorker` pushes the updated `deletedAt` value on next sync.
- **Restoring** a note sets `deletedAt = null` and `pendingSync = true`. Sync propagates the restore to Firebase.
- **Permanently deleting** from the trash uses the existing `PendingDeletionEntity` flow — removes locally and queues a Firebase delete.
- **Auto-clean** — `CleanTrashWorker` runs once daily (periodic, `KEEP` policy). It queries notes with `deletedAt <= now - 30 days` and permanently deletes them via the same `PendingDeletionEntity` flow. Scheduled from `App.onCreate` via `NotesSyncManager.scheduleTrashClean()`.

### DAO queries

| Query | Filter |
|---|---|
| `getNotesFlow(userId)` | `deletedAt IS NULL` — home screen |
| `getDeletedNotesFlow(userId)` | `deletedAt IS NOT NULL` — trash screen |
| `getNotes(userId)` | all notes — sync stale-check |
| `getOldTrashedNotes(userId, cutoff)` | `deletedAt IS NOT NULL AND deletedAt <= cutoff` |
| `softDeleteNote(id, deletedAt)` | sets `deletedAt` and `pendingSync = 1` |
| `restoreNote(id)` | sets `deletedAt = NULL` and `pendingSync = 1` |

### Sync interaction

- Trashed notes stay in Firebase with `deletedAt` set — other devices see the trash state on next sync.
- Pending notes (`pendingSync = true`) are never overwritten by `syncFromFirebase` — the stale-check skips them.
- After `permanentlyDeleteNote`, the note is gone from Firebase and local Room.

### Key rules

- **Never call `notesDataSource.deleteNote` for a trash operation** — that is a hard delete. Only use it from `permanentlyDeleteNote` or `PendingDeletionEntity` cleanup.
- `deleteNote` in `NotesRepository` is a **soft delete** (moves to trash). To hard-delete, call `permanentlyDeleteNote`.
- The trash screen (`feature:trash`) uses `GetTrashedNotesUseCase`, `RestoreNoteUseCase`, and `PermanentlyDeleteNoteUseCase`.

---

## Navigation

Uses **Navigation 3** (androidx.navigation3) with type-safe `NavKey` destinations:

```kotlin
sealed interface MoodFLowScreen : NavKey {
    Login, Register, Home, Settings  // objects
    EditNote(val id: String = "")    // data class — empty string = new note
}
```

Navigation is in `app/navigation/MoodFlowNavigation.kt`. All screens are wrapped in `SharedTransitionLayout` to enable shared element transitions between `Home` and `EditNote`.

Destinations: `Login`, `Register`, `Home`, `Settings`, `Trash`, `EditNote(id: String)`

Start screen depends on `PreferencesHandler.isLogged` (observed as `StateFlow` in `MainViewModel`).

---

## Home screen widget

Built with **Jetpack Glance**. All files live in `app/.../widget/`.

### Key constraints
- **No ViewModel support** — Glance widgets have no `ViewModelStoreOwner`. Never try to inject a ViewModel into a `GlanceAppWidget`. Use use cases and domain interfaces directly.
- **Single root per `item`** — in Glance's `LazyColumn`, each `item { }` lambda must contain a single root composable. Placing sibling composables directly in the lambda (without a `Column` wrapper) can cause them to overlap.

### Structure

| Class | Role |
|---|---|
| `NotesWidget` | `GlanceAppWidget` — injects `GetAllNotesUseCase` + `PreferencesHandler`, calls `provideContent` |
| `NotesWidgetReceiver` | `GlanceAppWidgetReceiver` — entry point registered in the manifest |
| `NotesWidgetConfigActivity` | Shown at widget placement; lets the user pick a filter, persists it via `updateAppWidgetState`, then triggers `NotesWidget.update()` |
| `NotesWidgetFilter` | Enum: `ALL`, `PINNED`, `NORMAL` — stored as a `stringPreferencesKey` in Glance state (DataStore) |

### Data flow

1. `provideGlance` reads `PreferencesHandler.isLogged` — if not logged in, uses `flowOf(emptyList())` so the widget shows a login prompt instead of notes
2. `GetAllNotesUseCase` handles `userId` resolution internally and returns `Flow<List<Note>>`
3. Notes are filtered client-side by the stored `NotesWidgetFilter` and sorted by date descending
4. `isLoggedIn` is captured once at `provideGlance` time and passed into the composable tree — it does **not** react to logout while the widget is open

### LazyColumn list layout

Notes and dividers are separate `item` entries to avoid overlap issues:

```kotlin
notes.forEachIndexed { index, note ->
    item { NoteItem(note = note, context = context) }
    if (index < notes.lastIndex) {
        item { Spacer(/* 1.dp divider */) }
    }
}
```

The divider after the last note is intentionally omitted.

### Widget update triggers

Call `NotesWidget().updateAll(context)` (or use `NotesWidgetReceiver`) whenever note data changes externally — e.g. after a successful edit in the app. This is already done via the receiver for OS-initiated updates.

---

## Dependency injection

Koin. Modules are registered in `App.kt`. Each feature/core module has its own `*Module.kt` file. When adding a new dependency:
1. Add the `*Module.kt` in the relevant module
2. Register it in `App.startKoin { modules(...) }`

`StringsProvider` is a `(Int) -> String` function alias injected into use cases and ViewModels to provide localized error strings without a Context reference in the domain layer.

### Context in use case implementations

When a use case implementation needs `Context` (e.g. for system APIs like `CredentialManager`), inject it in the constructor via Koin's `androidContext()` — **never** pass it through the interface, through intents, or into the ViewModel. The interface stays Android-free. See `GetGoogleIdTokenUseCaseImpl` as the canonical example.

---

## Conventions to follow

- **Never reference `viewModel` inside `Content`** — only state and lambdas
- **Never add fields to `Note`/`NoteEntity`/`NoteDto` without updating all three and writing a Room migration**
- **`color` is always `Long`** — never store as `Int` or `String`
- **Error messages** go through `StringsProvider`, never hardcoded strings
- `focusManager`, `isKeyboardOpen`, `LaunchedEffect`, animation state (`animateColorAsState`) belong in `Content`, not in the outer screen composable
- `BackHandler` and `HandleActions` belong in the outer screen composable, not in `Content`
- New Compose compiler flags go in `KotlinAndroid.kt` via `optIn.addAll()` or `freeCompilerArgs.add()`, never in individual module `build.gradle.kts` files (except for the app module's existing assertion flags)

---

## File locations — quick reference

| What | Where |
|---|---|
| Note domain model | `core/model/.../Note.kt` |
| Note Room entity | `core/localdatabase/.../NoteEntity.kt` |
| Note Firebase DTO | `core/database/.../NoteDto.kt` |
| Mappers | `core/database/.../mapper/toDomain.kt` |
| Room migrations | `core/localdatabase/.../migrations/Migrations.kt` |
| Room database | `core/localdatabase/.../NoteDatabase.kt` |
| Room DAO | `core/localdatabase/.../NoteDao.kt` |
| Firebase data source | `core/database/.../datasource/NotesDataSourceImpl.kt` |
| Repository impl | `core/database/.../repository/NotesRepositoryImpl.kt` |
| Sync worker | `core/database/.../workmanager/SyncNotesWorker.kt` |
| Clean trash worker | `core/database/.../workmanager/CleanTrashWorker.kt` |
| Trash screen | `feature/trash/.../MoodFlowTrash.kt` |
| Trash ViewModel | `feature/trash/.../TrashViewModel.kt` |
| Trash contract | `feature/trash/.../TrashViewContract.kt` |
| Trash Koin module | `feature/trash/.../di/trashViewModelModule.kt` |
| Note colors | `core/designsystem/.../theme/NoteColors.kt` |
| Navigation | `app/.../navigation/MoodFlowNavigation.kt` |
| Nav destinations | `core/navigation/.../MoodFLowScreen.kt` |
| Koin app setup | `app/.../App.kt` |
| Convention plugins | `buildlogic/convention/src/main/kotlin/` |
| Kotlin compiler config | `buildlogic/.../convention/KotlinAndroid.kt` |
| Preferences | `core/data/.../preferences/PreferencesHandlerImpl.kt` |
| Main ViewModel | `app/.../MainViewModel.kt` |
| Widget | `app/.../widget/NotesWidget.kt` |
| Widget receiver | `app/.../widget/NotesWidgetReceiver.kt` |
| Widget config activity | `app/.../widget/NotesWidgetConfigActivity.kt` |
| Widget filter + state key | `app/.../widget/NotesWidgetFilter.kt` |
| Google Sign-In use case interface | `core/domain/.../usecase/user/GetGoogleIdTokenUseCase.kt` |
| Google Sign-In use case impl | `authentication/.../data/usecase/GetGoogleIdTokenUseCaseImpl.kt` |
| Auth strings placeholder | `authentication/src/main/res/values/strings.xml` |
