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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoCamera
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

data class ParentProfileUiState(
    val parentId: String = "p1",
    val fullName: String = "Huda Alharbi",
    val email: String = "huda@email.com",
    val photoUri: String? = null,
    val isEmailVerified: Boolean = true,
    val selectedRole: AccountRole = AccountRole.PARENT,
    val linkedChildren: List<ChildUi> = listOf(
        ChildUi("1", "Sara", "🐰"),
        ChildUi("2", "Omar", "🐻")
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ParentProfileViewModel : ViewModel() {
    private val sampleLinkedChildren = listOf(
        ChildUi("1", "Sara", "🐰"),
        ChildUi("2", "Omar", "🐻")
    )

    var uiState by mutableStateOf(
        ParentProfileUiState(
            parentId = "p1",
            fullName = "Huda Alharbi",
            email = "huda@email.com",
            photoUri = null,
            isEmailVerified = true,
            selectedRole = AccountRole.PARENT,
            linkedChildren = sampleLinkedChildren
        )
    )
        private set

    fun onNameChange(newName: String) {
        uiState = uiState.copy(fullName = newName, errorMessage = null)
    }

    fun onRoleSelected(role: AccountRole) {
        uiState = uiState.copy(selectedRole = role)
    }

    fun onPhotoSelected(uriString: String) {
        uiState = uiState.copy(photoUri = uriString)
    }

    fun validateAndSave(): Boolean {
        if (uiState.fullName.isBlank()) {
            uiState = uiState.copy(errorMessage = "Please enter your full name")
            return false
        }
        return true
    }
}

@Composable
fun ParentProfileRoute(
    onSaveSuccess: () -> Unit,
    onBack: () -> Unit,
    onChildrenClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentProfileViewModel = viewModel()
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onPhotoSelected(uri.toString())
        }
    }

    ParentProfileScreen(
        uiState = viewModel.uiState,
        onNameChange = viewModel::onNameChange,
        onPhotoChange = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onRoleSelected = viewModel::onRoleSelected,
        onSave = {
            if (viewModel.validateAndSave()) {
                onSaveSuccess()
            }
        },
        onChildrenClick = onChildrenClick,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun ParentProfileScreen(
    uiState: ParentProfileUiState,
    onNameChange: (String) -> Unit,
    onPhotoChange: () -> Unit,
    onRoleSelected: (AccountRole) -> Unit,
    onSave: () -> Unit,
    onChildrenClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    text = stringResource(id = R.string.profile_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Avatar & Change Photo Button
            ProfileAvatar(
                parentName = uiState.fullName,
                photoUri = uiState.photoUri,
                onPhotoChange = onPhotoChange
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

            // Full Name Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.label_fullname),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = uiState.fullName,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.hint_fullname),
                            color = Color(0xFF94A3B8)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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

                // Email Input (Read-only with Verified Badge)
                Text(
                    text = stringResource(id = R.string.label_email),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (uiState.isEmailVerified) {
                            Text(
                                text = stringResource(id = R.string.badge_verified),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE8F5EE))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE2E8F0),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Role Selection Label & Chips ("Role")
                Text(
                    text = stringResource(id = R.string.label_role),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AccountRoleChip(
                        role = AccountRole.PARENT,
                        selected = uiState.selectedRole == AccountRole.PARENT,
                        onClick = { onRoleSelected(AccountRole.PARENT) }
                    )
                    AccountRoleChip(
                        role = AccountRole.TEACHER,
                        selected = uiState.selectedRole == AccountRole.TEACHER,
                        onClick = { onRoleSelected(AccountRole.TEACHER) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Linked Children Card
                ProfileChildrenCard(
                    linkedChildren = uiState.linkedChildren,
                    onClick = onChildrenClick
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))

            // Save Changes Button
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
                        text = stringResource(id = R.string.btn_save_changes),
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

@Composable
fun ProfileAvatar(
    parentName: String,
    photoUri: String?,
    onPhotoChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initial = parentName.firstOrNull()?.toString() ?: "H"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            // Main Avatar Circle
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5EE)),
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    Text(
                        text = "📷",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp)
                    )
                } else {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Camera Badge Overlay
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onPhotoChange),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = stringResource(id = R.string.btn_change_photo_text),
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(id = R.string.btn_change_photo_text),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onPhotoChange)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ProfileChildrenCard(
    linkedChildren: List<ChildUi>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val childrenText = if (linkedChildren.size == 1) {
        stringResource(id = R.string.linked_one_child)
    } else {
        stringResource(id = R.string.linked_children_count, linkedChildren.size)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container Box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1FAF4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.People,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Children count label
            Text(
                text = childrenText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            // Children Avatars Stack
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                linkedChildren.forEach { child ->
                    val avatarBgColor = if (child.avatarEmoji.contains("🐰") || child.name == "Sara") {
                        Color(0xFFFEF3C7) // Light yellow
                    } else {
                        Color(0xFFDBEAFE) // Light blue
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(avatarBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = child.avatarEmoji, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ParentProfileDefaultPreview() {
    TinyStepsTheme {
        ParentProfileScreen(
            uiState = ParentProfileUiState(
                fullName = "Huda Alharbi",
                email = "huda@email.com",
                isEmailVerified = true,
                selectedRole = AccountRole.PARENT,
                linkedChildren = listOf(
                    ChildUi("1", "Sara", "🐰"),
                    ChildUi("2", "Omar", "🐻")
                )
            ),
            onNameChange = {},
            onPhotoChange = {},
            onRoleSelected = {},
            onSave = {},
            onChildrenClick = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ParentProfileTeacherRolePreview() {
    TinyStepsTheme {
        ParentProfileScreen(
            uiState = ParentProfileUiState(
                fullName = "Huda Alharbi",
                email = "huda@email.com",
                isEmailVerified = true,
                selectedRole = AccountRole.TEACHER,
                linkedChildren = listOf(
                    ChildUi("1", "Sara", "🐰"),
                    ChildUi("2", "Omar", "🐻")
                )
            ),
            onNameChange = {},
            onPhotoChange = {},
            onRoleSelected = {},
            onSave = {},
            onChildrenClick = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ParentProfileValidationErrorPreview() {
    TinyStepsTheme {
        ParentProfileScreen(
            uiState = ParentProfileUiState(
                fullName = "",
                email = "huda@email.com",
                errorMessage = "Please enter your full name"
            ),
            onNameChange = {},
            onPhotoChange = {},
            onRoleSelected = {},
            onSave = {},
            onChildrenClick = {},
            onBack = {}
        )
    }
}
