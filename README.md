# Offline Notes

A simple notes app for Android that works fully offline and syncs to a backend when you're back online.

> **No hosted backend.** This project doesn't have a real server running anywhere — the backend is a minimal local stub (see [`backend/`](backend)) meant to be run on your own machine so you can see the sync flow work end to end. See [Running the backend](#running-the-backend) below.

## Features

- **Create, edit, and delete notes** — title, content, and timestamps.
- **Works offline** — every change is saved to a local database first, so the app never waits on the network.
- **Search** — filter the notes list by title or content as you type.
- **Login is optional** — the app is fully usable (create/edit/delete/search notes) without ever signing in. Signing in is only needed to enable background sync.
- **Background sync, gated on being signed in** — once you have a session, pending changes are pushed to the server automatically when you have a connection, using WorkManager (with network constraints and retry/backoff). Without a session, notes just stay `Pending` locally — no sync attempts, no wasted background jobs.
- **Session persisted across restarts** — the signed-in user is stored via a typed DataStore, so you stay logged in after killing/reopening the app.
- **Sync status per note** — a colored dot on each note shows whether it's `Pending` (yellow), `Synced` (green), or `Error` (red).
- **Backend error surfaced to you** — if a note fails to sync, the reason from the server is shown on the note.
- **Offline-safe delete** — deleting a note while offline just marks it for deletion locally; it's removed from the list right away and actually deleted on the server on the next sync.

  <img width="336" height="748" alt="Screenshot_20260908_121108" src="https://github.com/user-attachments/assets/0db8b385-8c1b-4662-a64e-d621050ace5c" />


## How offline sync works

1. Every save or delete writes to the local database immediately and marks the note `Pending`.
2. A background job (WorkManager) is scheduled — but only if you're signed in; otherwise nothing is enqueued at all, since there's no session to sync against.
3. The job only runs once the device has network connectivity, and sends **all pending notes in a single batch request** to the backend.
4. The server responds with a map of `note id → error message` for anything that failed. Any note not in that map is treated as successfully synced (or deleted, if it was a delete).
5. If the whole request fails (e.g. no network, server down), every pending note is marked `Error` and retried later with exponential backoff.
6. Signing in later (from the Notes List header) both persists the session and immediately schedules a sync, so anything that piled up while signed out gets pushed right away.

## Project structure

This is a multi-module Gradle project following a simple clean-architecture split:

```
app/                        # Android application entry point (DI wiring, App/MainActivity)
domain/                     # Business logic: models, use cases, repository interfaces
core/
  base/                     # Small shared abstractions (dispatchers, clock, error logger)
  common/                   # Shared implementations (networking, navigation, local storage)
  design-system/            # Reusable Compose UI (theme, buttons, status dot, etc.)
  navigation/               # Navigation contracts
  storage/                  # Generic, model-agnostic DataStore creation helper
feature/
  base/                     # Base Fragment wiring the theme
  notes/
    presentation/           # Screens (list + edit) built with Jetpack Compose
    data/                   # Room database + Retrofit API + repository implementation
  auth/
    presentation/           # Login screen (Compose) + ViewModel
    data/                   # Session (DataStore-backed) + login repository + Retrofit API
offline-sync/                # WorkManager job that triggers the sync use case, gated on session
backend/                    # Standalone Spring Boot (Kotlin) server for the sync + auth endpoints
```

The Android app is layered as: **presentation → domain ← data**, so the UI and the database/network code never talk directly — they both go through the use cases and repository interface defined in `domain`.

## Tech stack

- Kotlin, Jetpack Compose, Hilt (DI), Room (local database)
- Retrofit + OkHttp + kotlinx.serialization (networking)
- WorkManager (background sync)
- Jetpack DataStore (persisted login session)
- Timber (logging)
- Spring Boot + Kotlin (backend, for local testing)

## Running the Android app

Open the project in Android Studio and run the `app` module, or from the command line:

```bash
./gradlew :app:installDebug
```

By default the app is configured for the Android **emulator** talking to the local backend (see below) — `BaseUrl.kt` points at `http://10.0.2.2:8080`, which is the emulator's alias for your machine's `localhost`. Testing on a **physical device** instead? Change that URL to your machine's LAN IP (e.g. `http://192.168.x.x:8080`) — both devices need to be on the same network. Plain HTTP is only allowed in debug builds.

The app opens straight to the Notes List — you can start creating notes immediately without signing in. To turn on background sync, tap **Login** in the notes list header and sign in with the backend's seeded demo account (see [Running the backend](#running-the-backend)).

## Running the backend

There's no hosted server for this project — you need to run it yourself. It's a separate Gradle project (own wrapper, not part of the Android build):

```bash
cd backend
./gradlew bootRun
```

It starts on `http://localhost:8080` and exposes:

```
POST /notes/sync
POST /auth/login
```

Send a list of notes to `/notes/sync`; it responds with `{"errors": {}}` when everything succeeds, or `{"errors": {"<note id>": "<reason>"}}` for the ones that failed. Notes are stored in memory only — restarting the backend clears them.

`/auth/login` takes `{"email": "...", "password": "..."}` and returns `{"id": "...", "name": "..."}` on success, or `401` on bad credentials. There's no real user database — a single seeded demo account exists for testing: `test@example.com` / `password`.
