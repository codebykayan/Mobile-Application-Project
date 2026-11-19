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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinysteps_1.R
import com.example.tinysteps_1.ui.theme.TinyStepsTheme

data class AddEditChildUiState(
    val childId: String? = null,
    val name: String = "",
    val age: Int = 8,
    val avatar: String = "🐻",
    val favoriteColor: Long = 0xFF2563EB,
    val isDeviceLinked: Boolean = true,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AddEditChildViewModel : ViewModel() {
    var uiState by mutableStateOf(AddEditChildUiState())
        private set

    fun initialize(childId: String?) {
        if (childId != null && childId.isNotBlank() && childId != "new") {
            uiState = AddEditChildUiState(
                childId = childId,
                name = "Omar",
                age = 10,
                avatar = "🐻",
                favoriteColor = 0xFF2563EB,
                isDeviceLinked = true,
                isEditMode = true
            )
        } else {
            uiState = AddEditChildUiState(
                childId = null,
                name = "",
                age = 8,
                avatar = "🐰",
                favoriteColor = 0xFF25844F,
                isDeviceLinked = false,
                isEditMode = false
            )
        }
    }

    fun onNameChange(newName: String) {
        uiState = uiState.copy(name = newName, errorMessage = null)
    }

    fun onAgeChange(newAge: Int) {
        if (newAge in 7..12) {
            uiState = uiState.copy(age = newAge, errorMessage = null)
        }
    }

    fun onColorSelected(colorHex: Long) {
        uiState = uiState.copy(favoriteColor = colorHex)
    }

    fun validateAndSave(): Boolean {
        if (uiState.name.isBlank()) {
            uiState = uiState.copy(errorMessage = "Please enter child's name")
            return false
        }
        if (uiState.age !in 7..12) {
            uiState = uiState.copy(errorMessage = "Age must be between 7 and 12 years")
            return false
        }
        return true
    }

    fun deleteChild(onSuccess: () -> Unit) {
        onSuccess()
    }
}

@Composable
fun AddEditChildRoute(
    childId: String? = null,
    onSaveSuccess: () -> Unit,
    onBack: () -> Unit,
    onOpenLinkDevice: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditChildViewModel = viewModel()
) {
    LaunchedEffect(childId) {
        viewModel.initialize(childId)
    }

    AddEditChildScreen(
        uiState = viewModel.uiState,
        onNameChange = viewModel::onNameChange,
        onAgeChange = viewModel::onAgeChange,
        onColorSelected = viewModel::onColorSelected,
        onNewCode = {
            onOpenLinkDevice(viewModel.uiState.childId ?: "new")
        },
        onSave = {
            if (viewModel.validateAndSave()) {
                onSaveSuccess()
            }
        },
        onDelete = {
            viewModel.deleteChild(onSaveSuccess)
        },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun AddEditChildScreen(
    uiState: AddEditChildUiState,
    onNameChange: (String) -> Unit,
    onAgeChange: (Int) -> Unit,
    onColorSelected: (Long) -> Unit,
    onNewCode: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Delete Child Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = stringResource(id = R.string.dialog_delete_child_title)) },
            text = { Text(text = stringResource(id = R.string.dialog_delete_child_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
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
                TextButton(onClick = { showDeleteDialog = false }) {
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Bar: Back Arrow & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_content_description),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (uiState.isEditMode) {
                        stringResource(id = R.string.edit_child_title)
                    } else {
                        stringResource(id = R.string.add_child_title)
                    },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animal Avatar & Buddy Subtitle
            ChildAvatar(
                avatar = uiState.avatar,
                color = Color(uiState.favoriteColor),
                childName = uiState.name
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Error Message (if any)
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }

            // Child Name Label & Input Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.label_child_name),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.hint_child_name),
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

                Spacer(modifier = Modifier.height(20.dp))

                // Age Label & Selector
                Text(
                    text = stringResource(id = R.string.label_age),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                AgeSelector(
                    age = uiState.age,
                    onAgeChange = onAgeChange
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Favorite Color Label & Picker
                Text(
                    text = stringResource(id = R.string.label_favourite_colour),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                FavoriteColorPicker(
                    selectedColorHex = uiState.favoriteColor,
                    onColorSelected = onColorSelected
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Linked Device Card (if edit mode or device linked)
                if (uiState.isEditMode || uiState.isDeviceLinked) {
                    DeviceLinkCard(
                        childName = uiState.name.ifBlank { "Child" },
                        isLinked = uiState.isDeviceLinked,
                        onNewCode = onNewCode
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))

            // Save Button
            Button(
                onClick = onSave,
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
                        text = if (uiState.isEditMode) {
                            stringResource(id = R.string.btn_save_changes)
                        } else {
                            stringResource(id = R.string.btn_add_child_submit)
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Red "Delete child" Action (Visible in Edit Mode)
            if (uiState.isEditMode) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showDeleteDialog = true }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.btn_delete_child_action),
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
fun ChildAvatar(
    avatar: String,
    color: Color,
    childName: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(108.dp)
                .clip(CircleShape)
                .background(Color(0xFFDBEAFE)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = avatar, fontSize = 54.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        val subtitleText = if (childName.isNotBlank()) {
            stringResource(id = R.string.buddy_subtitle, childName)
        } else {
            stringResource(id = R.string.buddy_subtitle_generic)
        }

        Text(
            text = subtitleText,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AgeSelector(
    age: Int,
    onAgeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Minus Button
        IconButton(
            onClick = { if (age > 7) onAgeChange(age - 1) },
            enabled = age > 7,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0xFFCBD5E1), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = null,
                tint = if (age > 7) MaterialTheme.colorScheme.onSurface else Color(0xFFCBD5E1),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = age.toString(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Plus Button
        IconButton(
            onClick = { if (age < 12) onAgeChange(age + 1) },
            enabled = age < 12,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0xFFCBD5E1), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = if (age < 12) MaterialTheme.colorScheme.onSurface else Color(0xFFCBD5E1),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(id = R.string.unit_years_old),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FavoriteColorPicker(
    selectedColorHex: Long,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorOptions = listOf(
        0xFF22C55EL to Color(0xFF22C55E), // Green
        0xFF2563EBL to Color(0xFF2563EB), // Blue
        0xFFFF7A59L to Color(0xFFFF7A59), // Orange
        0xFF38BDF8L to Color(0xFF38BDF8), // Sky Blue
        0xFFF59E0BL to Color(0xFFF59E0B), // Gold
        0xFFA855F7L to Color(0xFFA855F7)  // Purple
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        colorOptions.forEach { (hexValue, color) ->
            val isSelected = selectedColorHex == hexValue

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .then(
                        if (isSelected) {
                            Modifier.border(2.5.dp, color, CircleShape)
                        } else {
                            Modifier
                        }
                    )
                    .padding(if (isSelected) 3.dp else 0.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelected(hexValue) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceLinkCard(
    childName: String,
    isLinked: Boolean,
    onNewCode: () -> Unit,
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1FAF4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Link,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Child's device title & Linked status
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.device_title, childName),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isLinked) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isLinked) {
                            stringResource(id = R.string.status_linked)
                        } else {
                            stringResource(id = R.string.status_not_linked)
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isLinked) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // New Code Action Link
            Text(
                text = stringResource(id = R.string.btn_new_code),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onNewCode)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditChildAddModePreview() {
    TinyStepsTheme {
        AddEditChildScreen(
            uiState = AddEditChildUiState(
                childId = null,
                name = "",
                age = 8,
                avatar = "🐰",
                favoriteColor = 0xFF25844FL,
                isDeviceLinked = false,
                isEditMode = false
            ),
            onNameChange = {},
            onAgeChange = {},
            onColorSelected = {},
            onNewCode = {},
            onSave = {},
            onDelete = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditChildEditModePreview() {
    TinyStepsTheme {
        AddEditChildScreen(
            uiState = AddEditChildUiState(
                childId = "2",
                name = "Omar",
                age = 10,
                avatar = "🐻",
                favoriteColor = 0xFF2563EBL,
                isDeviceLinked = true,
                isEditMode = true
            ),
            onNameChange = {},
            onAgeChange = {},
            onColorSelected = {},
            onNewCode = {},
            onSave = {},
            onDelete = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditChildLinkedDevicePreview() {
    TinyStepsTheme {
        AddEditChildScreen(
            uiState = AddEditChildUiState(
                childId = "1",
                name = "Sara",
                age = 8,
                avatar = "🐰",
                favoriteColor = 0xFFFF7A59L,
                isDeviceLinked = true,
                isEditMode = true
            ),
            onNameChange = {},
            onAgeChange = {},
            onColorSelected = {},
            onNewCode = {},
            onSave = {},
            onDelete = {},
            onBack = {}
        )
    }
}
