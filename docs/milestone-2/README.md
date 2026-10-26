# TinySteps

TinySteps is an Android app that helps children aged 7–12 complete multi-step daily routines (getting ready for school, cleaning a room, homework time) one step at a time. Parents and teachers create a task, add short steps with pictures, assign it to a child and follow the child's progress.

**Course:** CSC 402 Mobile Application Programming — Group 3

## Features
| Feature | What the user can do |
|---|---|
| Role Selection & Login | Choose the Parent/Teacher or the Child interface |
| Create & Edit Tasks | Enter a task name, add, edit, delete and reorder steps with pictures |
| Assign Task | Assign a prepared task to a specific child |
| Step-by-Step Guidance | The child sees one step at a time with a short instruction and a picture |
| Progress Tracking | Progress updates after each step and is visible to the child and the adult |

## Design (Milestone 2)
- **Clickable prototype (Figma):** https://www.figma.com/proto/oCiIMH71QOlPB8MUFV3vEo/Untitled?t=XmgMgFEmEKSr2ODA-1&scaling=min-zoom&content-scaling=fixed&page-id=0%3A1&node-id=5-829&starting-point-node-id=5%3A829
- **Wireframes:** [docs/wireframes/](docs/wireframes/)
- **Navigation map:** [docs/navigation-map.png](docs/navigation-map.png)
- **Architecture:** [docs/architecture.png](docs/architecture.png) and [docs/architecture.md](docs/architecture.md)
- **Style guide:** [docs/style-guide.md](docs/style-guide.md)
- **AI use log:** [docs/ai-use-log.md](docs/ai-use-log.md)

## Screens
| # | Screen | Route |
|---|---|---|
| 1 | Login / Role Selection | `role_selection` |
| 2 | Parent/Teacher Home & Progress | `parent_home` |
| 3 | Choose Child | `child_select` |
| 4 | Create Task | `create_task` |
| 5 | Child Home | `child_home` |
| 6 | My Tasks | `my_tasks` |
| 7 | Step-by-Step Task | `task_step/{taskId}` |
| 8 | Task Completed | `task_completed/{taskId}` |

## Tech stack
Kotlin • Jetpack Compose • Material 3 • Navigation Compose • ViewModel • Firebase (Firestore, Storage) • DataStore

## Project structure
