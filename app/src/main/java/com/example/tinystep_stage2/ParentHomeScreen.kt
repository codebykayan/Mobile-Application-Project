package com.example.tinystep_stage2

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinystep_stage2.ui.theme.PrimaryGreen
import com.example.tinystep_stage2.ui.theme.TertiaryInProgress
import com.example.tinystep_stage2.ui.theme.TinyStepsTheme

enum class TaskFilter {
    ACTIVE,
    COMPLETED
}

enum class TaskStatus {
    TO_DO,
    IN_PROGRESS,
    COMPLETED
}

data class TaskSummary(
    val id: String = "",
    val title: String = "",
    val childName: String = "",
    val status: TaskStatus = TaskStatus.TO_DO,
    val completedSteps: Int = 0,
    val totalSteps: Int = 0,
)

data class ParentHomeUiState(
    val parentName: String = "",
    val activeCount: Int = 0,
    val completedCount: Int = 0,
    val selectedFilter: TaskFilter = TaskFilter.ACTIVE,
    val tasks: List<TaskSummary> = emptyList(),
)

class ParentHomeViewModel : ViewModel() {
    var uiState by mutableStateOf(
        ParentHomeUiState(
            parentName = "Huda",
            activeCount = 3,
            completedCount = 1,
            selectedFilter = TaskFilter.ACTIVE,
            tasks = listOf(
                TaskSummary(id = "1", title = "Get Ready for School", childName = "Sara", status = TaskStatus.IN_PROGRESS, completedSteps = 5, totalSteps = 6),
                TaskSummary(id = "2", title = "Clean My Room", childName = "Omar", status = TaskStatus.IN_PROGRESS, completedSteps = 2, totalSteps = 4),
                TaskSummary(id = "3", title = "Homework Time", childName = "Sara", status = TaskStatus.TO_DO, completedSteps = 0, totalSteps = 5),
            ),
        )
    )
        private set

    fun setFilter(filter: TaskFilter) {
        uiState = uiState.copy(selectedFilter = filter)
    }
}

@Composable
fun ParentHomeRoute(
    modifier: Modifier = Modifier,
    viewModel: ParentHomeViewModel = viewModel(),
    onOpenTask: (String) -> Unit = {},
    onCreateTask: () -> Unit = {},
    onSwitchRole: () -> Unit = {},
) {
    val uiState = viewModel.uiState

    ParentHomeScreen(
        uiState = uiState,
        onFilterChange = { filter ->
            viewModel.setFilter(filter)
        },
        onTaskClick = onOpenTask,
        onNewTask = onCreateTask,
        onProfileClick = onSwitchRole,
        modifier = modifier,
    )
}

@Composable
fun ParentHomeScreen(
    uiState: ParentHomeUiState,
    onFilterChange: (TaskFilter) -> Unit,
    onTaskClick: (String) -> Unit,
    onNewTask: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredTasks = when (uiState.selectedFilter) {
        TaskFilter.ACTIVE -> uiState.tasks.filter { it.status != TaskStatus.COMPLETED }
        TaskFilter.COMPLETED -> uiState.tasks.filter { it.status == TaskStatus.COMPLETED }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewTask,
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                shape = MaterialTheme.shapes.large,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Task",
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New Task",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Good morning, ${uiState.parentName}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Let's take small steps.",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Surface(
                    onClick = onProfileClick,
                    shape = CircleShape,
                    color = PrimaryGreen.copy(alpha = 0.15f),
                    modifier = Modifier.size(48.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.parentName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stat Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                StatCard(
                    value = uiState.activeCount.toString(),
                    label = "Active tasks",
                    icon = Icons.AutoMirrored.Filled.List,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = uiState.completedCount.toString(),
                    label = "Completed",
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilterChip(
                    selected = uiState.selectedFilter == TaskFilter.ACTIVE,
                    onClick = { onFilterChange(TaskFilter.ACTIVE) },
                    label = { Text("Active") },
                    leadingIcon = if (uiState.selectedFilter == TaskFilter.ACTIVE) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen.copy(alpha = 0.15f),
                        selectedLabelColor = PrimaryGreen,
                        selectedLeadingIconColor = PrimaryGreen,
                    ),
                )
                FilterChip(
                    selected = uiState.selectedFilter == TaskFilter.COMPLETED,
                    onClick = { onFilterChange(TaskFilter.COMPLETED) },
                    label = { Text("Completed") },
                    leadingIcon = if (uiState.selectedFilter == TaskFilter.COMPLETED) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen.copy(alpha = 0.15f),
                        selectedLabelColor = PrimaryGreen,
                        selectedLeadingIconColor = PrimaryGreen,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tasks List or Empty State
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No tasks yet — tap New Task to create one",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(filteredTasks) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onTaskClick(task.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun TaskCard(
    task: TaskSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                StatusChip(status = task.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (task.childName == "Sara") Color(0xFFFFE3A3) else Color(0xFFD3E3FD),
                    modifier = Modifier.size(28.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = task.childName.take(1).uppercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = task.childName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Steps completed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${task.completedSteps}/${task.totalSteps}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TertiaryInProgress,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val progress = if (task.totalSteps > 0) task.completedSteps.toFloat() / task.totalSteps.toFloat() else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = TertiaryInProgress,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
fun StatusChip(
    status: TaskStatus,
    modifier: Modifier = Modifier,
) {
    val (text, containerColor, contentColor, icon) = when (status) {
        TaskStatus.TO_DO -> Quad("To do", Color.LightGray.copy(alpha = 0.3f), MaterialTheme.colorScheme.onSurfaceVariant, Icons.Default.Schedule)
        TaskStatus.IN_PROGRESS -> Quad("In progress", TertiaryInProgress.copy(alpha = 0.15f), TertiaryInProgress, Icons.Default.Schedule)
        TaskStatus.COMPLETED -> Quad("Completed", PrimaryGreen.copy(alpha = 0.15f), PrimaryGreen, Icons.Default.Check)
    }

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = containerColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = contentColor,
            )
        }
    }
}

data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Preview(showBackground = true)
@Composable
private fun StatCardPreview() {
    TinyStepsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            StatCard(
                value = "3",
                label = "Active tasks",
                icon = Icons.AutoMirrored.Filled.List,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskCardPreview() {
    TinyStepsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TaskCard(
                task = TaskSummary(
                    id = "1",
                    title = "Get Ready for School",
                    childName = "Sara",
                    status = TaskStatus.IN_PROGRESS,
                    completedSteps = 5,
                    totalSteps = 6,
                ),
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ParentHomeScreenPreview() {
    val sampleTasks = listOf(
        TaskSummary(id = "1", title = "Get Ready for School", childName = "Sara", status = TaskStatus.IN_PROGRESS, completedSteps = 5, totalSteps = 6),
        TaskSummary(id = "2", title = "Clean My Room", childName = "Omar", status = TaskStatus.IN_PROGRESS, completedSteps = 2, totalSteps = 4),
        TaskSummary(id = "3", title = "Homework Time", childName = "Sara", status = TaskStatus.TO_DO, completedSteps = 0, totalSteps = 5),
    )
    TinyStepsTheme {
        ParentHomeScreen(
            uiState = ParentHomeUiState(
                parentName = "Huda",
                activeCount = 3,
                completedCount = 1,
                selectedFilter = TaskFilter.ACTIVE,
                tasks = sampleTasks,
            ),
            onFilterChange = {},
            onTaskClick = {},
            onNewTask = {},
            onProfileClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ParentHomeScreenEmptyPreview() {
    TinyStepsTheme {
        ParentHomeScreen(
            uiState = ParentHomeUiState(
                parentName = "Huda",
                activeCount = 0,
                completedCount = 0,
                selectedFilter = TaskFilter.ACTIVE,
                tasks = emptyList(),
            ),
            onFilterChange = {},
            onTaskClick = {},
            onNewTask = {},
            onProfileClick = {},
        )
    }
}
