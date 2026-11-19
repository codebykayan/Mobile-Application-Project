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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinysteps_1.R
import com.example.tinysteps_1.ui.theme.TinyStepsTheme

data class TaskStepUi(
    val id: String,
    val instruction: String,
    val imageUri: String? = null,
    val stepNumber: Int = 1,
    val iconEmoji: String? = null,
    val isCompleted: Boolean = false,
    val hint: String? = null
)

data class CreateTaskUiState(
    val taskId: String? = null,
    val taskName: String = "",
    val steps: List<TaskStepUi> = emptyList(),
    val children: List<ChildUi> = emptyList(),
    val selectedChildIds: Set<String> = emptySet(),
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CreateTaskViewModel : ViewModel() {
    private val defaultChildren = listOf(
        ChildUi("1", "Sara", "🐰"),
        ChildUi("2", "Omar", "🐻")
    )

    private val sampleEditSteps = listOf(
        TaskStepUi("s1", "Take a bath", stepNumber = 1, iconEmoji = "🛁"),
        TaskStepUi("s2", "Put on your pajamas", stepNumber = 2, iconEmoji = "👕"),
        TaskStepUi("s3", "Brush your teeth", stepNumber = 3, iconEmoji = "🪥"),
        TaskStepUi("s4", "Read a story in bed", stepNumber = 4, iconEmoji = "📖")
    )

    var uiState by mutableStateOf(CreateTaskUiState(children = defaultChildren))
        private set

    fun initialize(taskId: String?) {
        if (taskId != null && taskId.isNotBlank() && taskId != "new") {
            uiState = CreateTaskUiState(
                taskId = taskId,
                taskName = "Bedtime Routine",
                steps = sampleEditSteps,
                children = defaultChildren,
                selectedChildIds = setOf("1", "2"),
                isEditMode = true
            )
        } else {
            uiState = CreateTaskUiState(
                taskId = null,
                taskName = "",
                steps = emptyList(),
                children = defaultChildren,
                selectedChildIds = emptySet(),
                isEditMode = false
            )
        }
    }

    fun onTaskNameChange(newName: String) {
        uiState = uiState.copy(taskName = newName, errorMessage = null)
    }

    fun onChildSelected(childId: String) {
        val currentSelected = uiState.selectedChildIds.toMutableSet()
        if (childId == "all") {
            if (currentSelected.size == uiState.children.size) {
                currentSelected.clear()
            } else {
                currentSelected.addAll(uiState.children.map { it.id })
            }
        } else {
            if (currentSelected.contains(childId)) {
                currentSelected.remove(childId)
            } else {
                currentSelected.add(childId)
            }
        }
        uiState = uiState.copy(selectedChildIds = currentSelected)
    }

    fun onDeleteStep(stepId: String) {
        val updatedSteps = uiState.steps.filter { it.id != stepId }
            .mapIndexed { index, step -> step.copy(stepNumber = index + 1) }
        uiState = uiState.copy(steps = updatedSteps)
    }

    fun onMoveStep(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in uiState.steps.indices || toIndex !in uiState.steps.indices) return
        val list = uiState.steps.toMutableList()
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        val updatedSteps = list.mapIndexed { index, step -> step.copy(stepNumber = index + 1) }
        uiState = uiState.copy(steps = updatedSteps)
    }
}

@Composable
fun CreateTaskRoute(
    taskId: String? = null,
    onBack: () -> Unit,
    onTaskSaved: () -> Unit,
    onAddEditStep: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateTaskViewModel = viewModel()
) {
    LaunchedEffect(taskId) {
        viewModel.initialize(taskId)
    }

    CreateTaskScreen(
        uiState = viewModel.uiState,
        onTaskNameChange = viewModel::onTaskNameChange,
        onAddStep = { onAddEditStep(null) },
        onEditStep = { stepId -> onAddEditStep(stepId) },
        onDeleteStep = viewModel::onDeleteStep,
        onMoveStep = viewModel::onMoveStep,
        onChildSelected = viewModel::onChildSelected,
        onAssignTask = onTaskSaved,
        onDeleteTask = onTaskSaved,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun CreateTaskScreen(
    uiState: CreateTaskUiState,
    onTaskNameChange: (String) -> Unit,
    onAddStep: () -> Unit,
    onEditStep: (String) -> Unit,
    onDeleteStep: (String) -> Unit,
    onMoveStep: (Int, Int) -> Unit,
    onChildSelected: (String) -> Unit,
    onAssignTask: () -> Unit,
    onDeleteTask: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stepToDelete by remember { mutableStateOf<String?>(null) }
    var showDeleteTaskDialog by remember { mutableStateOf(false) }

    // Delete Step Dialog Confirmation
    if (stepToDelete != null) {
        AlertDialog(
            onDismissRequest = { stepToDelete = null },
            title = { Text(text = stringResource(id = R.string.dialog_delete_step_title)) },
            text = { Text(text = stringResource(id = R.string.dialog_delete_step_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        stepToDelete?.let { onDeleteStep(it) }
                        stepToDelete = null
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
                TextButton(onClick = { stepToDelete = null }) {
                    Text(text = stringResource(id = R.string.btn_cancel))
                }
            }
        )
    }

    // Delete Task Dialog Confirmation
    if (showDeleteTaskDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteTaskDialog = false },
            title = { Text(text = stringResource(id = R.string.dialog_delete_task_title)) },
            text = { Text(text = stringResource(id = R.string.dialog_delete_task_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteTaskDialog = false
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
                TextButton(onClick = { showDeleteTaskDialog = false }) {
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

                // Header Bar: Back Arrow, Title, Delete Action (if Edit Mode)
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
                        text = if (uiState.isEditMode) {
                            stringResource(id = R.string.edit_task_title)
                        } else {
                            stringResource(id = R.string.create_task_title)
                        },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (uiState.isEditMode) {
                        Text(
                            text = stringResource(id = R.string.action_delete),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFFDC2626),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showDeleteTaskDialog = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Task Name Input
                Text(
                    text = stringResource(id = R.string.label_task_name),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = uiState.taskName,
                    onValueChange = onTaskNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.hint_task_name),
                            color = Color(0xFF94A3B8)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Steps Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.steps_count_header, uiState.steps.size),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = stringResource(id = R.string.drag_to_reorder),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Step List Items
            item {
                StepList(
                    steps = uiState.steps,
                    onEditStep = onEditStep,
                    onDeleteStep = { id -> stepToDelete = id },
                    onMoveStep = onMoveStep
                )
            }

            item {
                // Dashed "+ Add step" Button
                val primaryColor = MaterialTheme.colorScheme.primary
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF1FAF4))
                        .drawWithContent {
                            drawContent()
                            drawRoundRect(
                                color = primaryColor,
                                style = Stroke(
                                    width = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                                ),
                                cornerRadius = CornerRadius(20.dp.toPx())
                            )
                        }
                        .clickable(onClick = onAddStep),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.btn_add_step),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = primaryColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Assign to Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.label_assign_to),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = stringResource(id = R.string.pick_one_or_more),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Child Selection Chips
                val allSelected = uiState.selectedChildIds.size == uiState.children.size && uiState.children.isNotEmpty()
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        ChildSelectionChip(
                            label = stringResource(id = R.string.chip_all_children),
                            avatar = "👥",
                            selected = allSelected,
                            onClick = { onChildSelected("all") }
                        )
                    }
                    itemsIndexed(uiState.children) { _, child ->
                        ChildSelectionChip(
                            label = child.name,
                            avatar = child.avatarEmoji,
                            selected = uiState.selectedChildIds.contains(child.id),
                            onClick = { onChildSelected(child.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Assign Button
                val selectedCount = uiState.selectedChildIds.size
                val assignButtonText = when {
                    selectedCount > 1 -> stringResource(id = R.string.btn_assign_to_count, selectedCount)
                    selectedCount == 1 -> stringResource(id = R.string.btn_assign_to_one)
                    else -> stringResource(id = R.string.btn_assign_no_children)
                }

                Button(
                    onClick = onAssignTask,
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = assignButtonText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StepList(
    steps: List<TaskStepUi>,
    onEditStep: (String) -> Unit,
    onDeleteStep: (String) -> Unit,
    onMoveStep: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        steps.forEachIndexed { index, step ->
            TaskStepCard(
                step = step,
                onEdit = { onEditStep(step.id) },
                onDelete = { onDeleteStep(step.id) }
            )
        }
    }
}

@Composable
fun TaskStepCard(
    step: TaskStepUi,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drag Handle Icon
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = stringResource(id = R.string.drag_handle_content_desc),
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Step Image / Icon Container
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1FAF4)),
                contentAlignment = Alignment.Center
            ) {
                if (step.iconEmoji != null) {
                    Text(text = step.iconEmoji, fontSize = 24.sp)
                } else {
                    Text(text = "📋", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Step Label & Instruction
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.step_number_label, step.stepNumber),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = step.instruction,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Edit Button
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = stringResource(id = R.string.edit_step_content_desc),
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Delete Button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(id = R.string.delete_step_content_desc),
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChildSelectionChip(
    label: String,
    avatar: String?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (avatar != null) {
                    Text(text = avatar, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                )
            }
        },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else null,
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedContainerColor = Color(0xFFE8F5EE),
            selectedLabelColor = MaterialTheme.colorScheme.primary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = Color(0xFFCBD5E1),
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            borderWidth = 1.dp,
            selectedBorderWidth = 1.5.dp
        ),
        modifier = modifier.heightIn(min = 44.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun CreateTaskCreateModePreview() {
    TinyStepsTheme {
        CreateTaskScreen(
            uiState = CreateTaskUiState(
                taskId = null,
                taskName = "",
                steps = emptyList(),
                children = listOf(ChildUi("1", "Sara", "🐰"), ChildUi("2", "Omar", "🐻")),
                selectedChildIds = emptySet(),
                isEditMode = false
            ),
            onTaskNameChange = {},
            onAddStep = {},
            onEditStep = {},
            onDeleteStep = {},
            onMoveStep = { _, _ -> },
            onChildSelected = {},
            onAssignTask = {},
            onDeleteTask = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTaskEditModePreview() {
    TinyStepsTheme {
        CreateTaskScreen(
            uiState = CreateTaskUiState(
                taskId = "t1",
                taskName = "Bedtime Routine",
                steps = listOf(
                    TaskStepUi("s1", "Take a bath", stepNumber = 1, iconEmoji = "🛁"),
                    TaskStepUi("s2", "Put on your pajamas", stepNumber = 2, iconEmoji = "👕"),
                    TaskStepUi("s3", "Brush your teeth", stepNumber = 3, iconEmoji = "🪥"),
                    TaskStepUi("s4", "Read a story in bed", stepNumber = 4, iconEmoji = "📖")
                ),
                children = listOf(ChildUi("1", "Sara", "🐰"), ChildUi("2", "Omar", "🐻")),
                selectedChildIds = setOf("1", "2"),
                isEditMode = true
            ),
            onTaskNameChange = {},
            onAddStep = {},
            onEditStep = {},
            onDeleteStep = {},
            onMoveStep = { _, _ -> },
            onChildSelected = {},
            onAssignTask = {},
            onDeleteTask = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTaskEmptyStepsPreview() {
    TinyStepsTheme {
        CreateTaskScreen(
            uiState = CreateTaskUiState(
                taskId = null,
                taskName = "Morning Routine",
                steps = emptyList(),
                children = listOf(ChildUi("1", "Sara", "🐰"), ChildUi("2", "Omar", "🐻")),
                selectedChildIds = setOf("1"),
                isEditMode = false
            ),
            onTaskNameChange = {},
            onAddStep = {},
            onEditStep = {},
            onDeleteStep = {},
            onMoveStep = { _, _ -> },
            onChildSelected = {},
            onAssignTask = {},
            onDeleteTask = {},
            onBack = {}
        )
    }
}
