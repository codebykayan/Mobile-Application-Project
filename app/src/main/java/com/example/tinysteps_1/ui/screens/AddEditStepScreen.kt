package com.example.tinysteps_1.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinysteps_1.R
import com.example.tinysteps_1.ui.theme.TinyStepsTheme

data class AddEditStepUiState(
    val stepId: String? = null,
    val instruction: String = "",
    val hint: String = "",
    val imageUri: String? = null,
    val stepNumber: Int = 1,
    val totalSteps: Int = 1,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AddEditStepViewModel : ViewModel() {
    var uiState by mutableStateOf(AddEditStepUiState())
        private set

    fun initialize(taskId: String?, stepId: String?) {
        if (stepId != null && stepId.isNotBlank()) {
            uiState = AddEditStepUiState(
                stepId = stepId,
                instruction = "Put on your pajamas",
                hint = "The blue ones are on your bed",
                imageUri = "mock_gallery_photo",
                stepNumber = 2,
                totalSteps = 4,
                isEditMode = true
            )
        } else {
            uiState = AddEditStepUiState(
                stepId = null,
                instruction = "",
                hint = "",
                imageUri = null,
                stepNumber = 5,
                totalSteps = 5,
                isEditMode = false
            )
        }
    }

    fun onInstructionChange(text: String) {
        uiState = uiState.copy(instruction = text, errorMessage = null)
    }

    fun onHintChange(text: String) {
        uiState = uiState.copy(hint = text)
    }

    fun onImageSelected(uriString: String) {
        uiState = uiState.copy(imageUri = uriString)
    }

    fun onRemovePhoto() {
        uiState = uiState.copy(imageUri = null)
    }

    fun validateAndSave(): Boolean {
        if (uiState.instruction.isBlank()) {
            uiState = uiState.copy(errorMessage = "Please enter what the child should do")
            return false
        }
        return true
    }

    fun onDeleteStep(stepId: String) {
        // Shared mock state logic
    }
}

@Composable
fun AddEditStepRoute(
    taskId: String,
    stepId: String? = null,
    onSaveSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditStepViewModel = viewModel()
) {
    LaunchedEffect(taskId, stepId) {
        viewModel.initialize(taskId, stepId)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onImageSelected(uri.toString())
        }
    }

    AddEditStepScreen(
        uiState = viewModel.uiState,
        onInstructionChange = viewModel::onInstructionChange,
        onHintChange = viewModel::onHintChange,
        onChangePhoto = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onRemovePhoto = viewModel::onRemovePhoto,
        onSaveStep = {
            if (viewModel.validateAndSave()) {
                onSaveSuccess()
            }
        },
        onDeleteStep = { id ->
            viewModel.onDeleteStep(id)
            onSaveSuccess()
        },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun AddEditStepScreen(
    uiState: AddEditStepUiState,
    onInstructionChange: (String) -> Unit,
    onHintChange: (String) -> Unit,
    onChangePhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    onSaveStep: () -> Unit,
    onDeleteStep: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Delete Step Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(text = stringResource(id = R.string.dialog_delete_step_title)) },
            text = { Text(text = stringResource(id = R.string.dialog_delete_step_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteStep(uiState.stepId ?: "")
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
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Bar: Back Arrow, Title, Step Progress Indicator
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
                        stringResource(id = R.string.edit_step_title)
                    } else {
                        stringResource(id = R.string.add_step_title)
                    },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(
                        id = R.string.step_progress_indicator,
                        uiState.stepNumber,
                        uiState.totalSteps
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error Message (if any)
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // "What should the child do?" Label & Input
            Text(
                text = stringResource(id = R.string.label_child_do),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = uiState.instruction,
                onValueChange = onInstructionChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.hint_instruction),
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

            // Step Photo Label
            Text(
                text = stringResource(id = R.string.label_step_photo),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Step Photo Card Component
            StepPhotoCard(imageUri = uiState.imageUri)

            Spacer(modifier = Modifier.height(16.dp))

            // Change Photo & Remove Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PhotoActionButton(
                    text = stringResource(id = R.string.btn_change_photo),
                    icon = Icons.Outlined.PhotoCamera,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = onChangePhoto,
                    modifier = Modifier.weight(1f)
                )

                PhotoActionButton(
                    text = stringResource(id = R.string.btn_remove_photo),
                    icon = Icons.Outlined.Delete,
                    color = Color(0xFFDC2626),
                    onClick = onRemovePhoto,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // "Short hint (optional)" Label & Input
            Text(
                text = stringResource(id = R.string.label_short_hint),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = uiState.hint,
                onValueChange = onHintChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.hint_short_hint),
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

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(36.dp))

            // Green "Save step" Button
            Button(
                onClick = onSaveStep,
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
                        text = stringResource(id = R.string.btn_save_step),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Red "Delete step" Action (Visible only in Edit mode)
            if (uiState.isEditMode) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showDeleteConfirmDialog = true }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.btn_delete_step_action),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFFDC2626)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StepPhotoCard(
    imageUri: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFEAF4F6)),
        contentAlignment = Alignment.Center
    ) {
        // Center Illustration / Icon
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (imageUri != null) "👕" else "📷",
                fontSize = 72.sp
            )
        }

        // Bottom Left Pill Badge ("✓ From gallery")
        if (imageUri != null) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF475569))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.badge_from_gallery),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PhotoActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditStepAddModePreview() {
    TinyStepsTheme {
        AddEditStepScreen(
            uiState = AddEditStepUiState(
                stepId = null,
                instruction = "",
                hint = "",
                imageUri = null,
                stepNumber = 5,
                totalSteps = 5,
                isEditMode = false
            ),
            onInstructionChange = {},
            onHintChange = {},
            onChangePhoto = {},
            onRemovePhoto = {},
            onSaveStep = {},
            onDeleteStep = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditStepEditModePreview() {
    TinyStepsTheme {
        AddEditStepScreen(
            uiState = AddEditStepUiState(
                stepId = "s2",
                instruction = "Put on your pajamas",
                hint = "The blue ones are on your bed",
                imageUri = "mock_photo_uri",
                stepNumber = 2,
                totalSteps = 4,
                isEditMode = true
            ),
            onInstructionChange = {},
            onHintChange = {},
            onChangePhoto = {},
            onRemovePhoto = {},
            onSaveStep = {},
            onDeleteStep = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditStepEmptyPhotoPreview() {
    TinyStepsTheme {
        AddEditStepScreen(
            uiState = AddEditStepUiState(
                stepId = "s1",
                instruction = "Take a bath",
                hint = "",
                imageUri = null,
                stepNumber = 1,
                totalSteps = 4,
                isEditMode = true
            ),
            onInstructionChange = {},
            onHintChange = {},
            onChangePhoto = {},
            onRemovePhoto = {},
            onSaveStep = {},
            onDeleteStep = {},
            onBack = {}
        )
    }
}
