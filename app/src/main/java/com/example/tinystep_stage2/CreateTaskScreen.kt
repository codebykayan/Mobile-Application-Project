package com.example.tinystep_stage2

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinystep_stage2.ui.theme.ChildScreenBackground
import com.example.tinystep_stage2.ui.theme.ErrorRed
import com.example.tinystep_stage2.ui.theme.PrimaryGreen
import com.example.tinystep_stage2.ui.theme.TinyStepsTheme

data class StepDraft(
    val id: String = "",
    val instruction: String = "",
    val imageUri: String? = null,
)

data class ChildOption(
    val id: String = "",
    val name: String = "",
)

data class CreateTaskUiState(
    val taskName: String = "",
    val steps: List<StepDraft> = emptyList(),
    val children: List<ChildOption> = emptyList(),
    val selectedChildId: String? = null,
    val stepPendingDelete: StepDraft? = null,
)

class CreateTaskViewModel : ViewModel() {
    var uiState by mutableStateOf(
        CreateTaskUiState(
            taskName = "Bedtime Routine",
            steps = listOf(
                StepDraft(id = "1", instruction = "Take a bath"),
                StepDraft(id = "2", instruction = "Put on your pajamas"),
                StepDraft(id = "3", instruction = "Brush your teeth"),
                StepDraft(id = "4", instruction = "Read a story in bed"),
            ),
            children = listOf(
                ChildOption(id = "1", name = "Sara"),
                ChildOption(id = "2", name = "Omar"),
            ),
            selectedChildId = "1",
            stepPendingDelete = null,
        )
    )
        private set

    var activeStepIdForPhoto: String? = null

    fun onTaskNameChange(name: String) {
        uiState = uiState.copy(taskName = name)
    }

    fun addStep() {
        val newId = (uiState.steps.size + 1).toString()
        val newSteps = uiState.steps + StepDraft(id = newId, instruction = "New step")
        uiState = uiState.copy(steps = newSteps)
    }

    fun editStep(stepId: String) {
        val newSteps = uiState.steps.map { if (it.id == stepId) it.copy(instruction = it.instruction + " (Edited)") else it }
        uiState = uiState.copy(steps = newSteps)
    }

    fun requestDelete(stepId: String) {
        val step = uiState.steps.find { it.id == stepId }
        uiState = uiState.copy(stepPendingDelete = step)
    }

    fun confirmDelete() {
        val stepToDelete = uiState.stepPendingDelete
        if (stepToDelete != null) {
            val newSteps = uiState.steps.filter { it.id != stepToDelete.id }
            uiState = uiState.copy(steps = newSteps, stepPendingDelete = null)
        }
    }

    fun dismissDelete() {
        uiState = uiState.copy(stepPendingDelete = null)
    }

    fun selectChild(childId: String) {
        uiState = uiState.copy(selectedChildId = childId)
    }

    fun setStepImage(stepId: String, uri: String?) {
        val newSteps = uiState.steps.map { if (it.id == stepId) it.copy(imageUri = uri) else it }
        uiState = uiState.copy(steps = newSteps)
    }
}

@Composable
fun CreateTaskRoute(
    modifier: Modifier = Modifier,
    viewModel: CreateTaskViewModel = viewModel(),
    onTaskAssigned: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val uiState = viewModel.uiState
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            viewModel.activeStepIdForPhoto?.let { stepId ->
                viewModel.setStepImage(stepId, uri?.toString())
            }
            viewModel.activeStepIdForPhoto = null
        },
    )

    CreateTaskScreen(
        uiState = uiState,
        onTaskNameChange = viewModel::onTaskNameChange,
        onAddStep = viewModel::addStep,
        onEditStep = viewModel::editStep,
        onDeleteStepClick = viewModel::requestDelete,
        onConfirmDelete = viewModel::confirmDelete,
        onDismissDelete = viewModel::dismissDelete,
        onChoosePhotoClick = { stepId ->
            viewModel.activeStepIdForPhoto = stepId
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onChildSelected = viewModel::selectChild,
        onAssign = onTaskAssigned,
        onBack = onBack,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    uiState: CreateTaskUiState,
    onTaskNameChange: (String) -> Unit,
    onAddStep: () -> Unit,
    onEditStep: (String) -> Unit,
    onDeleteStepClick: (String) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onChoosePhotoClick: (String) -> Unit,
    onChildSelected: (String) -> Unit,
    onAssign: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedChild = uiState.children.find { it.id == uiState.selectedChildId }
    val isAssignEnabled = uiState.taskName.isNotBlank() && uiState.steps.isNotEmpty() && uiState.selectedChildId != null
    val assignButtonText = selectedChild?.let { "Assign task to ${it.name}" } ?: "Assign task"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Task",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Text(
                    text = "Task name",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = uiState.taskName,
                    onValueChange = onTaskNameChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Steps (${uiState.steps.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(uiState.steps) { index, step ->
                        StepEditorRow(
                            step = step,
                            number = index + 1,
                            onImageClick = { onChoosePhotoClick(step.id) },
                            onEdit = { onEditStep(step.id) },
                            onDelete = { onDeleteStepClick(step.id) },
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = onAddStep,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(1.dp, PrimaryGreen),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PrimaryGreen,
                            ),
                        ) {
                            Text(
                                text = "+ Add step",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Assign to",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    uiState.children.forEach { child ->
                        val isSelected = child.id == uiState.selectedChildId
                        FilterChip(
                            selected = isSelected,
                            onClick = { onChildSelected(child.id) },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (child.name == "Sara") Color(0xFFFFE3A3) else Color(0xFFD3E3FD),
                                        modifier = Modifier.size(24.dp),
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = child.name.take(1).uppercase(),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                        }
                                    }
                                    Text(text = child.name)
                                }
                            },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface,
                                selectedContainerColor = ChildScreenBackground,
                                selectedLabelColor = PrimaryGreen,
                                selectedLeadingIconColor = PrimaryGreen,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                selectedBorderColor = PrimaryGreen,
                            ),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAssign,
                enabled = isAssignEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color.White,
                    disabledContainerColor = PrimaryGreen.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f),
                ),
            ) {
                Text(
                    text = assignButtonText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
        }
    }

    if (uiState.stepPendingDelete != null) {
        val stepNum = uiState.steps.indexOfFirst { it.id == uiState.stepPendingDelete.id } + 1
        DeleteStepDialogContent(
            stepNumber = if (stepNum > 0) stepNum else 1,
            stepInstruction = uiState.stepPendingDelete.instruction,
            taskName = uiState.taskName.ifBlank { "Task" },
            onConfirm = onConfirmDelete,
            onDismiss = onDismissDelete,
        )
    }
}

@Composable
fun StepEditorRow(
    step: StepDraft,
    number: Int,
    onImageClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.DragIndicator,
                contentDescription = "Reorder",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                onClick = onImageClick,
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF4E9D8),
                modifier = Modifier.size(48.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$number",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = step.instruction,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit step",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete step",
                    tint = ErrorRed,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
fun DeleteStepDialogContent(
    stepNumber: Int,
    stepInstruction: String,
    taskName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Delete step $stepNumber?")
        },
        text = {
            Text(text = "\"$stepInstruction\" will be removed from $taskName. This can't be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed),
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun StepEditorRowPreview() {
    TinyStepsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            StepEditorRow(
                step = StepDraft(id = "1", instruction = "Take a bath"),
                number = 1,
                onImageClick = {},
                onEdit = {},
                onDelete = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteStepDialogContentPreview() {
    TinyStepsTheme {
        DeleteStepDialogContent(
            stepNumber = 2,
            stepInstruction = "Put on your pajamas",
            taskName = "Bedtime Routine",
            onConfirm = {},
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTaskScreenFilledPreview() {
    val state = CreateTaskUiState(
        taskName = "Bedtime Routine",
        steps = listOf(
            StepDraft(id = "1", instruction = "Take a bath"),
            StepDraft(id = "2", instruction = "Put on your pajamas"),
            StepDraft(id = "3", instruction = "Brush your teeth"),
            StepDraft(id = "4", instruction = "Read a story in bed"),
        ),
        children = listOf(
            ChildOption(id = "1", name = "Sara"),
            ChildOption(id = "2", name = "Omar"),
        ),
        selectedChildId = "1",
        stepPendingDelete = null,
    )
    TinyStepsTheme {
        CreateTaskScreen(
            uiState = state,
            onTaskNameChange = {},
            onAddStep = {},
            onEditStep = {},
            onDeleteStepClick = {},
            onConfirmDelete = {},
            onDismissDelete = {},
            onChoosePhotoClick = {},
            onChildSelected = {},
            onAssign = {},
            onBack = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTaskScreenEmptyPreview() {
    val state = CreateTaskUiState(
        taskName = "",
        steps = emptyList(),
        children = listOf(
            ChildOption(id = "1", name = "Sara"),
            ChildOption(id = "2", name = "Omar"),
        ),
        selectedChildId = null,
        stepPendingDelete = null,
    )
    TinyStepsTheme {
        CreateTaskScreen(
            uiState = state,
            onTaskNameChange = {},
            onAddStep = {},
            onEditStep = {},
            onDeleteStepClick = {},
            onConfirmDelete = {},
            onDismissDelete = {},
            onChoosePhotoClick = {},
            onChildSelected = {},
            onAssign = {},
            onBack = {},
        )
    }
}
