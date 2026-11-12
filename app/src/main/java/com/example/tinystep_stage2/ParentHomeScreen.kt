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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val assignedChildIds: List<String> = emptyList(),
    val status: TaskStatus = TaskStatus.TO_DO,
    val completedSteps: Int = 0,
    val totalSteps: Int = 0,
)

data class ParentHomeUiState(
    val parentName: String = "",
    val activeCount: Int = 0,
    val completedCount: Int = 0,
    val selectedFilter: TaskFilter = TaskFilter.ACTIVE,
    val selectedChildFilterId: String? = null,
    val children: List<ChildProfile> = emptyList(),
    val tasks: List<TaskSummary> = emptyList(),
)

class ParentHomeViewModel : ViewModel() {
    var uiState by mutableStateOf(
        ParentHomeUiState(
            parentName = "Huda",
            selectedFilter = TaskFilter.ACTIVE,
            selectedChildFilterId = null,
            children = AppRepository.children,
            tasks = listOf(
                TaskSummary(
                    id = "1",
                    title = "Get Ready for School",
                    childName = "Sara",
                    assignedChildIds = listOf("1"),
                    status = TaskStatus.IN_PROGRESS,
                    completedSteps = 5,
                    totalSteps = 6,
                ),
                TaskSummary(
                    id = "2",
                    title = "Clean My Room",
                    childName = "Omar",
                    assignedChildIds = listOf("2"),
                    status = TaskStatus.IN_PROGRESS,
                    completedSteps = 2,
                    totalSteps = 4,
                ),
                TaskSummary(
                    id = "3",
                    title = "Homework Time",
                    childName = "Sara",
                    assignedChildIds = listOf("1"),
                    status = TaskStatus.TO_DO,
                    completedSteps = 0,
                    totalSteps = 5,
                ),
            ),
        )
    )
        private set

    fun updateChildren() {
        val currentChildren = AppRepository.children
        val activeCount = uiState.tasks.count { it.status != TaskStatus.COMPLETED }
        val completedCount = uiState.tasks.count { it.status == TaskStatus.COMPLETED }
        uiState = uiState.copy(
            children = currentChildren,
            activeCount = activeCount,
            completedCount = completedCount,
        )
    }

    fun setFilter(filter: TaskFilter) {
        uiState = uiState.copy(selectedFilter = filter)
    }

    fun setChildFilter(childId: String?) {
        uiState = uiState.copy(selectedChildFilterId = childId)
    }
}

@Composable
fun ParentHomeRoute(
    modifier: Modifier = Modifier,
    viewModel: ParentHomeViewModel = viewModel(),
    onOpenTask: (String) -> Unit = {},
    onCreateTask: () -> Unit = {},
    onManageChildren: () -> Unit = {},
    onSwitchRole: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        viewModel.updateChildren()
    }

    val uiState = viewModel.uiState

    ParentHomeScreen(
        uiState = uiState,
        onFilterChange = viewModel::setFilter,
        onChildFilterChange = viewModel::setChildFilter,
        onTaskClick = onOpenTask,
        onNewTask = onCreateTask,
        onManageChildren = onManageChildren,
        onProfileClick = onSwitchRole,
        modifier = modifier,
    )
}

@Composable
fun ParentHomeScreen(
    uiState: ParentHomeUiState,
    onFilterChange: (TaskFilter) -> Unit,
    onChildFilterChange: (String?) -> Unit,
    onTaskClick: (String) -> Unit,
    onNewTask: () -> Unit,
    onManageChildren: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedChild = uiState.children.find { it.id == uiState.selectedChildFilterId }
    val filteredTasks = uiState.tasks.filter { task ->
        val matchesStatus = when (uiState.selectedFilter) {
            TaskFilter.ACTIVE -> task.status != TaskStatus.COMPLETED
            TaskFilter.COMPLETED -> task.status == TaskStatus.COMPLETED
        }
        val matchesChild = if (uiState.selectedChildFilterId == null) {
            true
        } else {
            (task.assignedChildIds.contains(uiState.selectedChildFilterId)) ||
                    ((selectedChild != null) && task.childName.equals(selectedChild.name, ignoreCase = true))
        }
        matchesStatus && matchesChild
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
                    contentDescription = stringResource(R.string.new_task_button),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.new_task_button),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.good_morning, uiState.parentName),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.lets_take_small_steps),
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
                    label = stringResource(R.string.active_tasks),
                    icon = Icons.AutoMirrored.Filled.List,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = uiState.completedCount.toString(),
                    label = stringResource(R.string.completed_summary),
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Children Heading
            Text(
                text = stringResource(R.string.children_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic Child Filter Chips with Add Child Button at end
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedChildFilterId == null,
                        onClick = { onChildFilterChange(null) },
                        label = { Text(stringResource(R.string.filter_all)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen.copy(alpha = 0.15f),
                            selectedLabelColor = PrimaryGreen,
                        ),
                    )
                }
                items(uiState.children) { child ->
                    FilterChip(
                        selected = uiState.selectedChildFilterId == child.id,
                        onClick = { onChildFilterChange(child.id) },
                        label = { Text(child.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen.copy(alpha = 0.15f),
                            selectedLabelColor = PrimaryGreen,
                        ),
                    )
                }
                item {
                    Surface(
                        onClick = onManageChildren,
                        shape = CircleShape,
                        color = PrimaryGreen.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, PrimaryGreen),
                        modifier = Modifier.size(36.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_child_button),
                                tint = PrimaryGreen,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Task Status Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilterChip(
                    selected = uiState.selectedFilter == TaskFilter.ACTIVE,
                    onClick = { onFilterChange(TaskFilter.ACTIVE) },
                    label = { Text(stringResource(R.string.filter_active)) },
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
                    label = { Text(stringResource(R.string.filter_completed)) },
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
                        text = stringResource(R.string.no_tasks_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(filteredTasks) { task ->
                        TaskCard(
                            task = task,
                            children = uiState.children,
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
            Spacer(modifier = Modifier.height(4.dp))
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
    children: List<ChildProfile>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusLabel = when (task.status) {
        TaskStatus.IN_PROGRESS -> stringResource(R.string.status_in_progress)
        TaskStatus.COMPLETED -> stringResource(R.string.status_completed)
        TaskStatus.TO_DO -> stringResource(R.string.status_to_do)
    }

    val (statusBgColor, statusTextColor, statusIcon) = when (task.status) {
        TaskStatus.IN_PROGRESS -> Triple(TertiaryInProgress.copy(alpha = 0.15f), TertiaryInProgress, Icons.Default.Schedule)
        TaskStatus.COMPLETED -> Triple(PrimaryGreen.copy(alpha = 0.15f), PrimaryGreen, Icons.Default.CheckCircle)
        TaskStatus.TO_DO -> Triple(Color.LightGray.copy(alpha = 0.3f), MaterialTheme.colorScheme.onSurfaceVariant, Icons.Default.Schedule)
    }

    val assignedLabel = formatAssignedLabel(task, children)

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
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = statusBgColor,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusTextColor,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusTextColor,
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = assignedLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (task.totalSteps > 0) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.progress_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(R.string.steps_progress, task.completedSteps, task.totalSteps),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val progress = task.completedSteps.toFloat() / task.totalSteps.toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = PrimaryGreen,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun formatAssignedLabel(task: TaskSummary, children: List<ChildProfile>): String {
    if (task.assignedChildIds.isEmpty()) {
        return if (task.childName.isNotBlank()) stringResource(R.string.assigned_to, task.childName) else stringResource(R.string.assigned_to, "Sara")
    }
    val assignedChildren = children.filter { it.id in task.assignedChildIds }
    val names = if (assignedChildren.isNotEmpty()) {
        assignedChildren.map { it.name }
    } else {
        listOf(task.childName.ifBlank { "Sara" })
    }

    return when {
        (names.size >= children.size) && children.isNotEmpty() -> stringResource(R.string.assigned_to_all)
        names.size == 1 -> stringResource(R.string.assigned_to, names[0])
        names.size == 2 -> stringResource(R.string.assigned_to_two, names[0], names[1])
        else -> stringResource(R.string.assigned_to_many, names.joinToString(", "))
    }
}

@Preview(showBackground = true)
@Composable
private fun ParentHomeScreenPreview() {
    val sampleTasks = listOf(
        TaskSummary(id = "1", title = "Get Ready for School", childName = "Sara", assignedChildIds = listOf("1"), status = TaskStatus.IN_PROGRESS, completedSteps = 5, totalSteps = 6),
        TaskSummary(id = "2", title = "Clean My Room", childName = "Omar", assignedChildIds = listOf("2"), status = TaskStatus.IN_PROGRESS, completedSteps = 2, totalSteps = 4),
        TaskSummary(id = "3", title = "Homework Time", childName = "Sara", assignedChildIds = listOf("1"), status = TaskStatus.TO_DO, completedSteps = 0, totalSteps = 5),
    )
    val sampleChildren = listOf(
        ChildProfile(id = "1", name = "Sara", age = 8, avatarColor = 0xFFFFE3A3),
        ChildProfile(id = "2", name = "Omar", age = 10, avatarColor = 0xFFCDE4FA),
    )
    TinyStepsTheme {
        ParentHomeScreen(
            uiState = ParentHomeUiState(
                parentName = "Huda",
                activeCount = 3,
                completedCount = 1,
                selectedFilter = TaskFilter.ACTIVE,
                children = sampleChildren,
                tasks = sampleTasks,
            ),
            onFilterChange = {},
            onChildFilterChange = {},
            onTaskClick = {},
            onNewTask = {},
            onManageChildren = {},
            onProfileClick = {},
        )
    }
}
