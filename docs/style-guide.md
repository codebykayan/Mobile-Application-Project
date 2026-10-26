# TinySteps — Style Guide

One style guide for every screen and diagram. The same values are used in the Compose theme (`ui/theme/Color.kt`, `Type.kt`, `Theme.kt`).

## Colours
| Token | Hex | Used for |
|---|---|---|
| Primary (Green) | `#25844F` | Buttons, selected states, progress bars, active tab, check icons |
| On Primary | `#FFFFFF` | Text and icons on green buttons |
| Accent (Sunny Yellow) | `#FFC94A` | Stars, celebration badge, "Start" button, highlights |
| On Accent | `#3A2A00` | Text on yellow |
| In progress (Purple) | `#5B3FD9` | "In progress" chips and the "Doing now" card |
| Completed | `#25844F` | "Completed / Done" chips and checks |
| Error / Delete | `#C2412D` | Delete icons, delete dialog button, error messages |
| Parent screens background | `#F7F9FB` | Parent/Teacher screens |
| Child screens background | `#F1FAF4` | Child screens |
| Surface | `#FFFFFF` | Cards, dialogs, sheets |
| Text | `#1B2430` | Headings and body text |
| Secondary text | `#5B6474` | Subtitles, hints, labels |

## Typography — Nunito (Google Fonts)
| Style | Size / weight | Used for |
|---|---|---|
| Headline | 24–28 sp, Bold | Screen titles ("Hi Sara!", "My Tasks") |
| Title | 18–20 sp, Bold | Card titles, step instruction |
| Body | 14–16 sp, Regular | Descriptions, task details |
| Label | 12–14 sp, Medium | Chips, buttons, small labels |
| Child text | 18 sp minimum | All text on child screens |

## Spacing and shape
- 8 dp grid; screen padding 16 dp (parent) and 24 dp (child).
- Corner radius: 16 dp for parent cards, 24 dp for child cards and sheets.
- Buttons are pill-shaped; touch targets at least 48 dp, main child buttons 56–72 dp high.

## Components (Material 3)
| Component | Where |
|---|---|
| Card | Task cards, step cards, counters, role and child cards |
| Button / OutlinedButton | Continue, Assign task, Done, Back to My Tasks, Go Home |
| ExtendedFloatingActionButton | "+ New Task" on Parent/Teacher Home & Progress |
| FilterChip | Active / Completed filter, Assign to child |
| LinearProgressIndicator | Task progress on Parent Home, Child Home and My Tasks |
| StepProgressBar (custom) | Segmented step bar on Step-by-Step Task |
| NavigationBar | Child Home ⇄ My Tasks |
|
