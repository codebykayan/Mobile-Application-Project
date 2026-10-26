# TinySteps – Style Guide

## 1. Color Palette

| Element | Color | Hex | Usage |
|---|---|---|---|
| Primary | Forest Green | #25844F | Primary buttons, main actions and headings |
| On Primary | White | #FFFFFF | Text and icons on primary buttons |
| Secondary | Warm Yellow | #FFC94A | Stars, rewards and highlights |
| On Secondary | Dark Brown | #3A2A00 | Text on secondary elements |
| Tertiary | Purple | #5B3FD9 | In-progress indicators |
| Error | Red | #C2412D | Errors and delete actions |
| Parent Background | Light Gray | #F7F9FB | Parent and teacher screens |
| Child Background | Light Green | #F1FAF4 | Child screens |
| Surface | White | #FFFFFF | Cards and surfaces |
| Primary Text | Dark Navy | #1B2430 | Main text |
| Secondary Text | Slate Gray | #5B6474 | Supporting information |

## 2. Typography

Font Family: Nunito (Google Fonts)

| Text Style | Size | Weight |
|---|---|---|
| Display Large | 57sp | Bold |
| Display Medium | 45sp | Bold |
| Display Small | 36sp | Bold |
| Headline Large | 32sp | Bold |
| Headline Medium | 28sp | Bold |
| Headline Small | 24sp | Bold |
| Title Large | 22sp | Bold |
| Title Medium | 16sp | Bold |
| Title Small | 14sp | Bold |
| Body Large | 16sp | Normal |
| Body Medium | 14sp | Normal |
| Body Small | 12sp | Normal |
| Label Large | 14sp | Medium |
| Label Medium | 12sp | Medium |
| Label Small | 11sp | Medium |

## 3. Shapes

The application uses Material 3 Shapes:
- Medium: 16dp rounded corners
- Large: 24dp rounded corners
## 3.1 Status Colors

| Status | Color | Hex | Usage |
|---|---|---|---|
| Completed | Green | #25844F | Completed steps and tasks, Done section |
| In progress | Purple | #5B3FD9 | In-progress chips, Doing now section, progress bars |
| Error / Delete | Red | #C2412D | Delete icons, delete confirmation, error messages |

## 3.2 Spacing and Layout

- 8dp grid system
- 16dp–24dp screen padding
- Pill-shaped (fully rounded) primary buttons
- Minimum touch target: 48dp; child screen buttons 56–72dp high
- Child screens use larger text (18sp and above)

## 3.3 Components (Material 3)

| Component | Used for |
|---|---|
| Card | Role cards, task cards, step cards, counter cards |
| Button / OutlinedButton | Continue, Done, Finish task, Go Home |
| FilterChip | Status filters, Assign to child |
| LinearProgressIndicator | Task and step progress |
| NavigationBar | Child Home / My Tasks bottom navigation |
| TopAppBar | Create Task |
| AlertDialog | Delete step confirmation |
| ModalBottomSheet | "Great job" feedback after the last step |
| FloatingActionButton | New Task |
  

## 4. Theme Configuration

TinySteps uses Jetpack Compose Material 3 with shared color schemes, typography and shapes.

- Light theme uses the defined TinySteps color palette.
- Dark theme currently uses the same color values as the light theme.
- Dynamic colors are disabled by default to preserve the app's identity.
- Dynamic colors can be enabled on supported Android versions.
- Parent and child background colors are defined separately.
- ChildScreenBackground is mapped to surfaceVariant.

## 5. Design Principles

- Maintain consistent colors and typography across screens.
- Use clear visual indicators for task progress and completion.
- Use rounded components to create a friendly interface for children aged 7–12.
- Keep task instructions simple and easy to read.
- Distinguish primary actions, progress states and destructive actions using the defined color palette.
