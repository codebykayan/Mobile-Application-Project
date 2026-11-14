#Section 4 - Relationships and Delete behavior
** Owner: ** [Arwad] | ** Status: ** Done
Table and field match the ER diagram and Section 3 (data dictionary)
## Relationship | Type | Implemented by | On delete |
| --- | --- | --- | --- | 
| Parent has many Children | 1:N | Child.parentId → Parent.parentId | CASCADE |
| Parent creates many Tasks | 1:N | Task.parentId → Parent.parentId | CASCADE |
| Task contains many TaskSteps | 1:N | TaskStep.taskId → Task.taskId | CASCADE |
| Task has many TaskAssignments | 1:N | TaskAssignment.taskId → Task.taskId | CASCADE |
| Child has many TaskAssignments | 1:N | TaskAssignment.childId → Child.childId | CASCADE |
| TaskAssignment has many StepProgress | 1:N | StepProgress.assignmentId → TaskAssignment.assignmentId | CASCADE |
| TaskStep has many StepProgress | 1:N | StepProgress.stepId → TaskStep.stepId | CASCADE |
| Child has many PairingCodes | 1:N | PairingCode.childId → Child.childId | CASCADE |
| Child ↔️ Task | M:N | Junction table TaskAssignment | CASCADE |
| TaskAssignment ↔️ TaskStep | M:N | Junction table StepProgress | CASCADE |

## Delete behavior

- Deleting a Task deletes its StepProgress, TaskAssignments and TaskSteps.
- Deleting a Child deletes its StepProgress, TaskAssignments and PairingCodes. Tasks stay.
- Deleting a TaskStep deletes its StepProgress.
- Deleting a Parent deletes all their Children and Tasks.

Firestore has no automatic cascade, so the app deletes dependent documents in a WriteBatch (dependents first).
