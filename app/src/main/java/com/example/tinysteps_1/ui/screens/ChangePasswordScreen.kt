package com.example.tinysteps_1.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinysteps_1.R
import com.example.tinysteps_1.ui.theme.TinyStepsTheme

enum class PasswordField {
    CURRENT,
    NEW,
    CONFIRM
}

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isCurrentVisible: Boolean = false,
    val isNewVisible: Boolean = false,
    val isConfirmVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ChangePasswordViewModel : ViewModel() {
    var uiState by mutableStateOf(ChangePasswordUiState())
        private set

    fun onCurrentPasswordChange(password: String) {
        uiState = uiState.copy(currentPassword = password, errorMessage = null)
    }

    fun onNewPasswordChange(password: String) {
        uiState = uiState.copy(newPassword = password, errorMessage = null)
    }

    fun onConfirmPasswordChange(password: String) {
        uiState = uiState.copy(confirmPassword = password, errorMessage = null)
    }

    fun onToggleVisibility(field: PasswordField) {
        uiState = when (field) {
            PasswordField.CURRENT -> uiState.copy(isCurrentVisible = !uiState.isCurrentVisible)
            PasswordField.NEW -> uiState.copy(isNewVisible = !uiState.isNewVisible)
            PasswordField.CONFIRM -> uiState.copy(isConfirmVisible = !uiState.isConfirmVisible)
        }
    }

    fun validateAndUpdate(): Boolean {
        if (uiState.currentPassword.isBlank()) {
            uiState = uiState.copy(errorMessage = "Please enter your current password")
            return false
        }
        if (uiState.newPassword.length < 8) {
            uiState = uiState.copy(errorMessage = "New password must be at least 8 characters")
            return false
        }
        if (!uiState.newPassword.any { it.isDigit() }) {
            uiState = uiState.copy(errorMessage = "New password must contain at least one number")
            return false
        }
        if (uiState.newPassword == uiState.currentPassword) {
            uiState = uiState.copy(errorMessage = "New password must be different from current password")
            return false
        }
        if (uiState.confirmPassword != uiState.newPassword) {
            uiState = uiState.copy(errorMessage = "Password confirmation does not match")
            return false
        }
        uiState = uiState.copy(successMessage = "Password updated successfully")
        return true
    }
}

@Composable
fun ChangePasswordRoute(
    onUpdateSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChangePasswordViewModel = viewModel()
) {
    ChangePasswordScreen(
        uiState = viewModel.uiState,
        onCurrentPasswordChange = viewModel::onCurrentPasswordChange,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onToggleVisibility = viewModel::onToggleVisibility,
        onUpdatePassword = {
            if (viewModel.validateAndUpdate()) {
                onUpdateSuccess()
            }
        },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun ChangePasswordScreen(
    uiState: ChangePasswordUiState,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleVisibility: (PasswordField) -> Unit,
    onUpdatePassword: () -> Unit,
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
                .verticalScroll(rememberScrollState())
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
                    text = stringResource(id = R.string.label_change_password),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = stringResource(id = R.string.change_password_subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 48.dp, bottom = 24.dp)
            )

            // Error Banner
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Current Password Input
            Text(
                text = stringResource(id = R.string.label_current_password),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            PasswordInputField(
                value = uiState.currentPassword,
                onValueChange = onCurrentPasswordChange,
                placeholder = "••••••••",
                isVisible = uiState.isCurrentVisible,
                onToggleVisibility = { onToggleVisibility(PasswordField.CURRENT) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // New Password Input
            Text(
                text = stringResource(id = R.string.label_new_password),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            PasswordInputField(
                value = uiState.newPassword,
                onValueChange = onNewPasswordChange,
                placeholder = "Tiny\$teps2026",
                isVisible = uiState.isNewVisible,
                onToggleVisibility = { onToggleVisibility(PasswordField.NEW) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Requirements Indicators
            val isAtLeast8Chars = uiState.newPassword.length >= 8
            val hasOneNumber = uiState.newPassword.any { it.isDigit() }
            val isDifferentFromOld = uiState.newPassword.isNotBlank() && uiState.newPassword != uiState.currentPassword

            Column(modifier = Modifier.padding(start = 4.dp)) {
                PasswordRequirementRow(
                    text = stringResource(id = R.string.req_8_chars),
                    isMet = isAtLeast8Chars
                )
                Spacer(modifier = Modifier.height(6.dp))
                PasswordRequirementRow(
                    text = stringResource(id = R.string.req_one_number),
                    isMet = hasOneNumber
                )
                Spacer(modifier = Modifier.height(6.dp))
                PasswordRequirementRow(
                    text = stringResource(id = R.string.req_different_old),
                    isMet = isDifferentFromOld
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm Password Input
            Text(
                text = stringResource(id = R.string.label_confirm_password),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            PasswordInputField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                placeholder = "••••••••••••",
                isVisible = uiState.isConfirmVisible,
                onToggleVisibility = { onToggleVisibility(PasswordField.CONFIRM) }
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(36.dp))

            // Update Password Button
            Button(
                onClick = onUpdatePassword,
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
                        text = stringResource(id = R.string.btn_update_password),
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
fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF94A3B8)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (isVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = stringResource(id = R.string.toggle_password_visibility),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(16.dp),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun PasswordRequirementRow(
    text: String,
    isMet: Boolean,
    modifier: Modifier = Modifier
) {
    val contentColor = if (isMet) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8)
    val checkTint = if (isMet) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = checkTint,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 14.sp,
                fontWeight = if (isMet) FontWeight.Bold else FontWeight.Normal
            ),
            color = contentColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChangePasswordDefaultPreview() {
    TinyStepsTheme {
        ChangePasswordScreen(
            uiState = ChangePasswordUiState(
                currentPassword = "••••••••",
                newPassword = "Tiny\$teps2026",
                confirmPassword = "••••••••••••",
                isCurrentVisible = false,
                isNewVisible = true,
                isConfirmVisible = false
            ),
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onToggleVisibility = {},
            onUpdatePassword = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChangePasswordVisiblePreview() {
    TinyStepsTheme {
        ChangePasswordScreen(
            uiState = ChangePasswordUiState(
                currentPassword = "MyOldPassword1",
                newPassword = "Tiny\$teps2026",
                confirmPassword = "Tiny\$teps2026",
                isCurrentVisible = true,
                isNewVisible = true,
                isConfirmVisible = true
            ),
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onToggleVisibility = {},
            onUpdatePassword = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChangePasswordErrorPreview() {
    TinyStepsTheme {
        ChangePasswordScreen(
            uiState = ChangePasswordUiState(
                currentPassword = "",
                newPassword = "short",
                confirmPassword = "short",
                errorMessage = "New password must be at least 8 characters"
            ),
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onToggleVisibility = {},
            onUpdatePassword = {},
            onBack = {}
        )
    }
}
