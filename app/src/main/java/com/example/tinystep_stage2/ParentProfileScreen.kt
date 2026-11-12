package com.example.tinystep_stage2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.tinystep_stage2.ui.theme.ErrorRed
import com.example.tinystep_stage2.ui.theme.PrimaryGreen
import com.example.tinystep_stage2.ui.theme.TinyStepsTheme

data class ParentProfileUiState(
    val parentName: String = "Huda",
    val email: String = "huda@example.com",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val isCurrentPasswordVisible: Boolean = false,
    val isNewPasswordVisible: Boolean = false,
    val isConfirmNewPasswordVisible: Boolean = false,
    val currentPasswordErrorRes: Int? = null,
    val newPasswordErrorRes: Int? = null,
    val confirmNewPasswordErrorRes: Int? = null,
)

class ParentProfileViewModel : ViewModel() {
    var uiState by mutableStateOf(ParentProfileUiState())
        private set

    fun onCurrentPasswordChange(password: String) {
        uiState = uiState.copy(
            currentPassword = password,
            currentPasswordErrorRes = null,
        )
    }

    fun onNewPasswordChange(password: String) {
        uiState = uiState.copy(
            newPassword = password,
            newPasswordErrorRes = null,
        )
    }

    fun onConfirmNewPasswordChange(password: String) {
        uiState = uiState.copy(
            confirmNewPassword = password,
            confirmNewPasswordErrorRes = null,
        )
    }

    fun toggleCurrentPasswordVisibility() {
        uiState = uiState.copy(isCurrentPasswordVisible = !uiState.isCurrentPasswordVisible)
    }

    fun toggleNewPasswordVisibility() {
        uiState = uiState.copy(isNewPasswordVisible = !uiState.isNewPasswordVisible)
    }

    fun toggleConfirmNewPasswordVisibility() {
        uiState = uiState.copy(isConfirmNewPasswordVisible = !uiState.isConfirmNewPasswordVisible)
    }

    fun validateAndUpdatePassword() {
        var isValid = true
        var currentErr: Int? = null
        var newErr: Int? = null
        var confirmErr: Int? = null

        if (uiState.currentPassword.isBlank()) {
            currentErr = R.string.error_password_too_short
            isValid = false
        }

        if (uiState.newPassword.isBlank() || (uiState.newPassword.length < 6)) {
            newErr = R.string.error_password_too_short
            isValid = false
        }

        if (uiState.confirmNewPassword != uiState.newPassword) {
            confirmErr = R.string.error_passwords_do_not_match
            isValid = false
        }

        uiState = uiState.copy(
            currentPasswordErrorRes = currentErr,
            newPasswordErrorRes = newErr,
            confirmNewPasswordErrorRes = confirmErr,
        )

        if (isValid) {
            // Password change validation passed. Clears fields without false success messages or local password storage.
            uiState = uiState.copy(
                currentPassword = "",
                newPassword = "",
                confirmNewPassword = "",
            )
        }
    }
}

@Composable
fun ParentProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: ParentProfileViewModel = viewModel(),
    onSwitchAccount: () -> Unit = {},
    onLogOut: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    ParentProfileScreen(
        uiState = uiState,
        onCurrentPasswordChange = viewModel::onCurrentPasswordChange,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onConfirmNewPasswordChange = viewModel::onConfirmNewPasswordChange,
        onToggleCurrentPasswordVisibility = viewModel::toggleCurrentPasswordVisibility,
        onToggleNewPasswordVisibility = viewModel::toggleNewPasswordVisibility,
        onToggleConfirmNewPasswordVisibility = viewModel::toggleConfirmNewPasswordVisibility,
        onUpdatePassword = viewModel::validateAndUpdatePassword,
        onSwitchAccount = onSwitchAccount,
        onLogOut = {
            AuthManager.signOut(context)
            onLogOut()
        },
        onBack = onBack,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentProfileScreen(
    uiState: ParentProfileUiState,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmNewPasswordChange: (String) -> Unit,
    onToggleCurrentPasswordVisibility: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmNewPasswordVisibility: () -> Unit,
    onUpdatePassword: () -> Unit,
    onSwitchAccount: () -> Unit,
    onLogOut: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val currentLang = LanguageManager.getLanguage(context)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_parent_profile),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                // Profile Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryGreen.copy(alpha = 0.15f),
                        modifier = Modifier.size(80.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = uiState.parentName.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = uiState.parentName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            item {
                // Section: Account Information
                SectionCard(title = stringResource(R.string.section_account_info)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = stringResource(R.string.parent_name_label),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = uiState.parentName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = stringResource(R.string.email_label),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = uiState.email,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }

            item {
                // Section: Change Password
                SectionCard(title = stringResource(R.string.section_change_password)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Current Password
                        Text(
                            text = stringResource(R.string.label_current_password),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        TextField(
                            value = uiState.currentPassword,
                            onValueChange = onCurrentPasswordChange,
                            placeholder = { Text("••••••••") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            singleLine = true,
                            isError = uiState.currentPasswordErrorRes != null,
                            visualTransformation = if (uiState.isCurrentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = onToggleCurrentPasswordVisibility) {
                                    Icon(
                                        imageVector = if (uiState.isCurrentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = stringResource(R.string.toggle_password_visibility),
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        )
                        uiState.currentPasswordErrorRes?.let { errRes ->
                            Text(
                                text = stringResource(errRes),
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        // New Password
                        Text(
                            text = stringResource(R.string.label_new_password),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        TextField(
                            value = uiState.newPassword,
                            onValueChange = onNewPasswordChange,
                            placeholder = { Text("••••••••") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            singleLine = true,
                            isError = uiState.newPasswordErrorRes != null,
                            visualTransformation = if (uiState.isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = onToggleNewPasswordVisibility) {
                                    Icon(
                                        imageVector = if (uiState.isNewPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = stringResource(R.string.toggle_password_visibility),
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        )
                        uiState.newPasswordErrorRes?.let { errRes ->
                            Text(
                                text = stringResource(errRes),
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        // Confirm New Password
                        Text(
                            text = stringResource(R.string.label_confirm_new_password),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        TextField(
                            value = uiState.confirmNewPassword,
                            onValueChange = onConfirmNewPasswordChange,
                            placeholder = { Text("••••••••") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            singleLine = true,
                            isError = uiState.confirmNewPasswordErrorRes != null,
                            visualTransformation = if (uiState.isConfirmNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = onToggleConfirmNewPasswordVisibility) {
                                    Icon(
                                        imageVector = if (uiState.isConfirmNewPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = stringResource(R.string.toggle_password_visibility),
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        )
                        uiState.confirmNewPasswordErrorRes?.let { errRes ->
                            Text(
                                text = stringResource(errRes),
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = onUpdatePassword,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen,
                                contentColor = Color.White,
                            ),
                        ) {
                            Text(
                                text = stringResource(R.string.button_update_password),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            item {
                // Section: Language
                SectionCard(title = stringResource(R.string.section_language)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        FilterChip(
                            selected = currentLang == "en",
                            onClick = {
                                if (currentLang != "en") {
                                    LanguageManager.setLanguage(context, "en")
                                }
                            },
                            label = { Text("English") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen.copy(alpha = 0.15f),
                                selectedLabelColor = PrimaryGreen,
                            ),
                            modifier = Modifier.weight(1f),
                        )
                        FilterChip(
                            selected = currentLang == "ar",
                            onClick = {
                                if (currentLang != "ar") {
                                    LanguageManager.setLanguage(context, "ar")
                                }
                            },
                            label = { Text("العربية") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen.copy(alpha = 0.15f),
                                selectedLabelColor = PrimaryGreen,
                            ),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            item {
                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onSwitchAccount,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = MaterialTheme.shapes.large,
                        border = BorderStroke(1.dp, PrimaryGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen),
                    ) {
                        Text(
                            text = stringResource(R.string.button_switch_account),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        )
                    }

                    OutlinedButton(
                        onClick = onLogOut,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = MaterialTheme.shapes.large,
                        border = BorderStroke(1.dp, ErrorRed),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    ) {
                        Text(
                            text = stringResource(R.string.button_log_out),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
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
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ParentProfileScreenPreview() {
    TinyStepsTheme {
        ParentProfileScreen(
            uiState = ParentProfileUiState(),
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onConfirmNewPasswordChange = {},
            onToggleCurrentPasswordVisibility = {},
            onToggleNewPasswordVisibility = {},
            onToggleConfirmNewPasswordVisibility = {},
            onUpdatePassword = {},
            onSwitchAccount = {},
            onLogOut = {},
            onBack = {},
        )
    }
}
