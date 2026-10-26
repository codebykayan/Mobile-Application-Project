# TinySteps – System Architecture

## 1. Architecture Overview

TinySteps is an Android application built with Kotlin and Jetpack Compose. It follows the recommended Android layered architecture with three layers: **UI**, **Domain** and **Data**.

Each layer has one job, and dependencies point inward: the UI depends on the domain layer, and the data layer implements the domain interfaces. The domain layer has no Android imports, so its rules are easy to unit-test.

Parent and child use different devices, so **Firestore is the shared source of truth**: a task assigned on the parent's device appears on the child's device, and every completed step updates the parent's view.

![TinySteps architecture diagram](architecture.png)

*Figure 3 — TinySteps architecture: UI, Domain and Data layers with unidirectional data flow*

## 2. Architecture Layers

| Layer | Responsibility | Key classes | Rule |
|---|---|---|---|
| **UI** | Display state and send user events; decide navigation | Composable screens (`RoleSelectionScreen` … `TaskCompletedScreen`), Routes, ViewModels (e.g. `StepByStepTaskViewModel`), `TinyStepsNavHost`, `TinyStepsTheme` | Screens are stateless and never call Firebase or a repository directly; only the ViewModel changes `uiState` |
| **Domain** | Business rules of TinySteps, independent of Android | `Task`, `TaskStep`, `Child` models; `AssignTaskUseCase`, `CompleteStepUseCase`, `GetChildTasksUseCase`; `TaskRepository`, `ChildRepository` interfaces | Pure Kotlin: no Android, Compose or Firebase imports |
| **Data** | Store, sync and fetch data; upload step images; keep small settings | `FirestoreTaskRepository`, `FirestoreChildRepository`, `StepImageStorage`, `UserPreferencesRepository` (DataStore), `TaskDto` and `TaskMapper` | Firestore is the single shared source of truth for tasks and progress; no UI code |

### UI Layer
- One stateless `XxxScreen(uiState, onEvent…, modifier)` per screen, with a `@Preview` for each state.
- One `XxxRoute` per screen that connects the screen to its ViewModel.
- ViewModels hold one immutable `UiState` (`mutableStateOf` with `private set`).
- `TinyStepsNavHost` owns the `NavController` and uses type-safe `@Serializable` routes; screens only call lambdas.

### Domain Layer
- Models: `Task`, `TaskStep`, `Child`.
- Use cases: `AssignTaskUseCase` (parent assigns a task to a child), `CompleteStepUseCase` (child completes the current step), `GetChildTasksUseCase` (child's To do / Doing / Done lists).
- Repository interfaces: `TaskRepository`, `ChildRepository`.

### Data Layer
- `FirestoreTaskRepository` and `FirestoreChildRepository` implement the domain interfaces.
- `StepImageStorage` uploads step pictures to Firebase Storage and returns their URL.
- `UserPreferencesRepository` stores the selected child and favourite colour in DataStore.
- `TaskDto` and `TaskMapper` convert between Firestore documents and domain models.

## 3. Unidirectional Data Flow (UDF)

State flows **down** from the ViewModel to the screen; events flow **up** from the screen to the ViewModel.

**Example screen:** Step-by-Step Task (route `task_step/{taskId}`) — `StepByStepTaskScreen`, `StepByStepTaskRoute`, `StepByStepTaskViewModel`.

| UiState | Meaning |
|---|---|
| Loading | Task and steps are being loaded from Firestore (progress indicator) |
| Success | Current step: task title, "Step n of total", instruction, image, Done button; after the last step `showFeedback = true` opens the "Great job" sheet |
| Error | Task cannot be loaded or the step cannot be saved (no network); message with a Retry button |

| Event (UI → ViewModel) | Result |
|---|---|
| `onDone` | `viewModel.completeStep()` |
| `onFinishTask` | `TinyStepsNavHost` opens Task Completed |
| `onClose` (X) | `TinyStepsNavHost` returns to My Tasks |

`StepByStepTaskRoute` reads `viewModel.uiState` and passes it to the stateless `StepByStepTaskScreen`, which only draws it. When the child taps Done, the screen calls `onDone`, and the Route forwards it to `viewModel.completeStep()`. The ViewModel builds a new state with `copy()` and Compose recomposes the screen. Navigation is decided by `TinyStepsNavHost`, never by the screen.

```kotlin
class StepByStepTaskViewModel : ViewModel() {
    var uiState by mutableStateOf(StepByStepTaskUiState())
        private set

    // Event from the UI (flows up)
    fun completeStep() {
        uiState = if (uiState.stepNumber < uiState.totalSteps)
            uiState.copy(stepNumber = uiState.stepNumber + 1)
        else uiState.copy(showFeedback = true)
    }
}
```

## 4. Feature Walk-through: completing a step

| Step | Layer | What happens |
|---|---|---|
| 1 | UI | The child taps Done on `StepByStepTaskScreen`; `StepByStepTaskRoute` forwards the event to `viewModel.completeStep()` |
| 2 | UI → Domain | `StepByStepTaskViewModel.completeStep()` calls `CompleteStepUseCase(taskId, stepNumber)` |
| 3 | Domain | `CompleteStepUseCase` checks the step is the current step of the task, marks the `TaskStep` as done, recalculates progress and status, and calls `TaskRepository.updateProgress(task)` |
| 4 | Data | `FirestoreTaskRepository` converts the task with `TaskMapper.toDto()` and saves it in Firestore |
| 5 | Data → UI | The repository returns a `Flow<Result>`; the ViewModel updates `uiState` with `copy()` and the screen recomposes |
| 6 | UI (parent device) | `ParentHomeViewModel` observes `TaskRepository` through the Firestore listener; the task card on Parent/Teacher Home & Progress updates (e.g. 6/6, Completed) |

## 5. Technology

| Library | Used for |
|---|---|
| Kotlin + Jetpack Compose + Material 3 | All screens and `TinyStepsTheme` |
| Navigation Compose + Kotlinx Serialization | Type-safe routes in `TinyStepsNavHost` |
| Lifecycle ViewModel Compose | One ViewModel per screen |
| Activity Compose (Photo Picker) | Choosing step images in `CreateTaskRoute` |
| Firebase Firestore | Shared tasks, steps and progress |
| Firebase Storage | Step images |
| Kotlin Coroutines / Flow | Async calls and live updates |
| Coil | Loading step images |
| DataStore Preferences | Selected child and favourite colour |

The full justification for each library is in section B3 of the Milestone 2 submission.
