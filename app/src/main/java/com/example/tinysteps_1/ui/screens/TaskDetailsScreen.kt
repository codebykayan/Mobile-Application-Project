package com.example.tinysteps_1.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinysteps_1.R
import com.example.tinysteps_1.ui.theme.TinyStepsTheme

data class TaskStepStatusUi(
    val id: String,
    val instruction: String,
    val isCompleted: Boolean = false,
    val isCurrent: Boolean = false,
    val stepNumber: Int = 1
)

data class TaskDetailsUiState(
    val taskId: String = "",
    val title: String = "",
    val childName: String = "",
    val childAvatar: String = "",
    val status: TaskStatus = TaskStatus.ACTIVE,
    val completedSteps: Int = 0,
    val totalSteps: Int = 0,
    val assignedAt: String = "",
    val updatedAt: String = "",
    val steps: List<TaskStepStatusUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val progress: Float
        get() = if (totalSteps > 0) completedSteps.toFloat() / totalSteps else 0f

    val percentageText: String
        get() = "${(progress * 100).toInt()}%"
}

class TaskDetailsViewModel : ViewModel() {
    private val sampleTaskDetailsMap = mapOf(
        "t1" to TaskDetailsUiState(
            taskId = "t1",
            title = "Get Ready for School",
            childName = "Sara",
            childAvatar = "🐰",
            status = TaskStatus.ACTIVE,
            completedSteps = 5,
            totalSteps = 6,
            assignedAt = "Sun, 4 Oct · 6:30 AM",
            updatedAt = "2 min ago",
            steps = listOf(
                TaskStepStatusUi("s1", "Make your bed", isCompleted = true, stepNumber = 1),
                TaskStepStatusUi("s2", "Brush your teeth", isCompleted = true, stepNumber = 2),
                TaskStepStatusUi("s3", "Wash your face", isCompleted = true, stepNumber = 3),
                TaskStepStatusUi("s4", "Put on your uniform", isCompleted = true, stepNumber = 4),
                TaskStepStatusUi("s5", "Eat your breakfast", isCompleted = true, stepNumber = 5),
                TaskStepStatusUi("s6", "Pack your school bag", isCompleted = false, isCurrent = true, stepNumber = 6)
            )
        ),
        "t2" to TaskDetailsUiState(
            taskId = "t2",
            title = "Clean My Room",
            childName = "Omar",
            childAvatar = "🐻",
            status = TaskStatus.ACTIVE,
            completedSteps = 2,
            totalSteps = 4,
            assignedAt = "Sun, 4 Oct · 7:00 AM",
            updatedAt = "15 min ago",
            steps = listOf(
                TaskStepStatusUi("s1", "Pick up toys", isCompleted = true, stepNumber = 1),
                TaskStepStatusUi("s2", "Make the bed", isCompleted = true, stepNumber = 2),
                TaskStepStatusUi("s3", "Organize desk", isCompleted = false, isCurrent = true, stepNumber = 3),
                TaskStepStatusUi("s4", "Vacuum floor", isCompleted = false, isCurrent = false, stepNumber = 4)
            )
        )
    )

    var uiState by mutableStateOf(TaskDetailsUiState())
        private set

    fun initialize(taskId: String) {
        val loadedTask = sampleTaskDetailsMap[taskId] ?: sampleTaskDetailsMap["t1"]!!
        uiState = loadedTask.copy(taskId = taskId)
    }

    fun deleteTask(onSuccess: () -> Unit) {
        onSuccess()
    }
}

@Composable
fun TaskDetailsRoute(
    taskId: String,
    onEditTask: (String) -> Unit,
    onDeleteSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailsViewModel = viewModel()
) {
    LaunchedEffect(taskId) {
        viewModel.initialize(taskId)
    }

    TaskDetailsScreen(
        uiState = viewModel.uiState,
        onEditTask = { onEditTask(viewModel.uiState.taskId) },
        onDeleteTask = {
            viewModel.deleteTask(onDeleteSuccess)
        },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun TaskDetailsScreen(
    uiState: TaskDetailsUiState,
    onEditTask: () -> Unit,
    onDeleteTask: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Delete Task Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(text = stringResource(id = R.string.dialog_delete_task_title)) },
            text = { Text(text = stringResource(id = R.string.dialog_delete_task_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteTask()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.action_delete),
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(text = stringResource(id = R.string.btn_cancel))
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Header Bar: Back Arrow, Title, Edit Pencil Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_content_description),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.task_details_title),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Circular Edit Pencil Button
                    IconButton(
                        onClick = onEditTask,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = stringResource(id = R.string.edit_task_title),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Summary Card with Circular Progress
                TaskSummaryCard(uiState = uiState)

                Spacer(modifier = Modifier.height(24.dp))

                // Steps Section Header: Steps (Left), Updated time (Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.steps_label),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (uiState.updatedAt.isNotBlank()) {
                        Text(
                            text = stringResource(id = R.string.updated_at_prefix, uiState.updatedAt),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Step List Items (Read-only status cards)
            items(uiState.steps) { step ->
                TaskStepStatusCard(step = step)
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))

                // Bottom Action Buttons Row: Delete & Edit task
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFFDC2626)
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.action_delete),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFFDC2626)
                            )
                        }
                    }

                    Button(
                        onClick = onEditTask,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.btn_edit_task),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskSummaryCard(
    uiState: TaskDetailsUiState,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Progress Indicator
            CircularTaskProgress(
                progress = uiState.progress,
                percentageText = uiState.percentageText
            )

            Spacer(modifier = Modifier.width(20.dp))

            // Task Details Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Child Avatar & Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = uiState.childAvatar, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiState.childName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Status Badge
                val isCompleted = uiState.status == TaskStatus.COMPLETED
                val badgeBg = if (isCompleted) Color(0xFFE8F5EE) else Color(0xFFEDE9FE)
                val badgeColor = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                val badgeText = if (isCompleted) {
                    stringResource(id = R.string.status_completed)
                } else {
                    stringResource(
                        id = R.string.status_in_progress_ratio,
                        uiState.completedSteps,
                        uiState.totalSteps
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = badgeColor
                    )
                }

                if (uiState.assignedAt.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(id = R.string.assigned_at_prefix, uiState.assignedAt),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CircularTaskProgress(
    progress: Float,
    percentageText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 8.dp,
            trackColor = Color(0xFFE2E8F0),
            strokeCap = StrokeCap.Round
        )
        Text(
            text = percentageText,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun TaskStepStatusCard(
    step: TaskStepStatusUi,
    modifier: Modifier = Modifier
) {
    val isCurrent = step.isCurrent
    val isDone = step.isCompleted

    val borderStroke = when {
        isCurrent -> BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
        else -> BorderStroke(1.dp, Color(0xFFE2E8F0))
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = borderStroke,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Indicator
            if (isDone) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFCBD5E1), CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Instruction Text
            Text(
                text = step.instruction,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Right Status Badge ("Done" or "Current")
            if (isDone) {
                Text(
                    text = stringResource(id = R.string.badge_done),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5EE))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            } else if (isCurrent) {
                Text(
                    text = stringResource(id = R.string.badge_current),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEDE9FE))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskDetailsInProgressPreview() {
    TinyStepsTheme {
        TaskDetailsScreen(
            uiState = TaskDetailsUiState(
                taskId = "t1",
                title = "Get Ready for School",
                childName = "Sara",
                childAvatar = "🐰",
                status = TaskStatus.ACTIVE,
                completedSteps = 5,
                totalSteps = 6,
                assignedAt = "Sun, 4 Oct · 6:30 AM",
                updatedAt = "2 min ago",
                steps = listOf(
                    TaskStepStatusUi("s1", "Make your bed", isCompleted = true, stepNumber = 1),
                    TaskStepStatusUi("s2", "Brush your teeth", isCompleted = true, stepNumber = 2),
                    TaskStepStatusUi("s3", "Wash your face", isCompleted = true, stepNumber = 3),
                    TaskStepStatusUi("s4", "Put on your uniform", isCompleted = true, stepNumber = 4),
                    TaskStepStatusUi("s5", "Eat your breakfast", isCompleted = true, stepNumber = 5),
                    TaskStepStatusUi("s6", "Pack your school bag", isCompleted = false, isCurrent = true, stepNumber = 6)
                )
            ),
            onEditTask = {},
            onDeleteTask = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskDetailsCompletedPreview() {
    TinyStepsTheme {
        TaskDetailsScreen(
            uiState = TaskDetailsUiState(
                taskId = "t4",
                title = "Feed the Cat",
                childName = "Omar",
                childAvatar = "🐻",
                status = TaskStatus.COMPLETED,
                completedSteps = 3,
                totalSteps = 3,
                assignedAt = "Sun, 4 Oct · 8:00 AM",
                updatedAt = "Just now",
                steps = listOf(
                    TaskStepStatusUi("s1", "Fill food bowl", isCompleted = true, stepNumber = 1),
                    TaskStepStatusUi("s2", "Fill water bowl", isCompleted = true, stepNumber = 2),
                    TaskStepStatusUi("s3", "Pet the cat", isCompleted = true, stepNumber = 3)
                )
            ),
            onEditTask = {},
            onDeleteTask = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskDetailsNotStartedPreview() {
    TinyStepsTheme {
        TaskDetailsScreen(
            uiState = TaskDetailsUiState(
                taskId = "t3",
                title = "Homework Time",
                childName = "Sara",
                childAvatar = "🐰",
                status = TaskStatus.NOT_STARTED,
                completedSteps = 0,
                totalSteps = 5,
                assignedAt = "Sun, 4 Oct · 4:00 PM",
                updatedAt = "Not started yet",
                steps = listOf(
                    TaskStepStatusUi("s1", "Open math book", isCompleted = false, isCurrent = false, stepNumber = 1),
                    TaskStepStatusUi("s2", "Solve page 12", isCompleted = false, isCurrent = false, stepNumber = 2)
                )
            ),
            onEditTask = {},
            onDeleteTask = {},
            onBack = {}
        )
    }
}
