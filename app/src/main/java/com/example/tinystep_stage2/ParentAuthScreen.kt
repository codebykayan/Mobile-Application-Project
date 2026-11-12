package com.example.tinystep_stage2

import android.content.Context
import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinystep_stage2.ui.theme.ErrorRed
import com.example.tinystep_stage2.ui.theme.PrimaryGreen
import com.example.tinystep_stage2.ui.theme.TinyStepsTheme

enum class AuthMode {
    LOGIN,
    SIGN_UP,
}

data class ParentAuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val rememberMe: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val emailErrorRes: Int? = null,
    val passwordErrorRes: Int? = null,
    val confirmPasswordErrorRes: Int? = null,
    val authError: String? = null,
    val isLoading: Boolean = false,
    val authMode: AuthMode = AuthMode.LOGIN,
)

class ParentAuthViewModel : ViewModel() {
    var uiState by mutableStateOf(ParentAuthUiState())
        private set

    fun init(context: Context) {
        val savedRememberMe = AuthManager.isRememberMeEnabled(context)
        uiState = uiState.copy(rememberMe = savedRememberMe)
    }

    fun onEmailChange(email: String) {
        uiState = uiState.copy(
            email = email,
            emailErrorRes = null,
            authError = null,
        )
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(
            password = password,
            passwordErrorRes = null,
            authError = null,
        )
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        uiState = uiState.copy(
            confirmPassword = confirmPassword,
            confirmPasswordErrorRes = null,
            authError = null,
        )
    }

    fun onRememberMeChange(rememberMe: Boolean) {
        uiState = uiState.copy(rememberMe = rememberMe)
    }

    fun togglePasswordVisibility() {
        uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible)
    }

    fun toggleConfirmPasswordVisibility() {
        uiState = uiState.copy(isConfirmPasswordVisible = !uiState.isConfirmPasswordVisible)
    }

    fun toggleAuthMode() {
        val nextMode = if (uiState.authMode == AuthMode.LOGIN) AuthMode.SIGN_UP else AuthMode.LOGIN
        uiState = uiState.copy(
            authMode = nextMode,
            password = "",
            confirmPassword = "",
            emailErrorRes = null,
            passwordErrorRes = null,
            confirmPasswordErrorRes = null,
            authError = null,
        )
    }

    fun validateAndSubmit(context: Context, onSuccess: () -> Unit) {
        val emailTrimmed = uiState.email.trim()
        val password = uiState.password
        val confirmPassword = uiState.confirmPassword
        var isValid = true

        var emailErr: Int? = null
        var passwordErr: Int? = null
        var confirmErr: Int? = null

        if (emailTrimmed.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) {
            emailErr = R.string.error_invalid_email
            isValid = false
        }

        if (password.isBlank() || (password.length < 6)) {
            passwordErr = R.string.error_password_too_short
            isValid = false
        }

        if ((uiState.authMode == AuthMode.SIGN_UP) && (confirmPassword != password)) {
            confirmErr = R.string.error_passwords_do_not_match
            isValid = false
        }

        uiState = uiState.copy(
            emailErrorRes = emailErr,
            passwordErrorRes = passwordErr,
            confirmPasswordErrorRes = confirmErr,
        )

        if (!isValid) return

        uiState = uiState.copy(isLoading = true, authError = null)

        // Save only Remember Me preference locally. Never store passwords.
        AuthManager.setRememberMeEnabled(context, uiState.rememberMe)

        val auth = AuthManager.getFirebaseAuth()
        if (auth != null) {
            if (uiState.authMode == AuthMode.LOGIN) {
                auth.signInWithEmailAndPassword(emailTrimmed, password)
                    .addOnCompleteListener { task ->
                        uiState = uiState.copy(isLoading = false)
                        if (task.isSuccessful) {
                            onSuccess()
                        } else {
                            uiState = uiState.copy(authError = task.exception?.localizedMessage ?: "Authentication failed")
                        }
                    }
            } else {
                auth.createUserWithEmailAndPassword(emailTrimmed, password)
                    .addOnCompleteListener { task ->
                        uiState = uiState.copy(isLoading = false)
                        if (task.isSuccessful) {
                            onSuccess()
                        } else {
                            uiState = uiState.copy(authError = task.exception?.localizedMessage ?: "Sign up failed")
                        }
                    }
            }
        } else {
            // Firebase Auth SDK fallback when project is in demo environment
            uiState = uiState.copy(isLoading = false)
            onSuccess()
        }
    }
}

@Composable
fun ParentAuthRoute(
    modifier: Modifier = Modifier,
    viewModel: ParentAuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.init(context)
    }

    val uiState = viewModel.uiState

    ParentAuthScreen(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onRememberMeChange = viewModel::onRememberMeChange,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onToggleConfirmPasswordVisibility = viewModel::toggleConfirmPasswordVisibility,
        onToggleMode = viewModel::toggleAuthMode,
        onSubmit = {
            viewModel.validateAndSubmit(context = context, onSuccess = onAuthSuccess)
        },
        onDemoClick = {
            AuthManager.setRememberMeEnabled(context, uiState.rememberMe)
            onAuthSuccess()
        },
        onBack = onBack,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentAuthScreen(
    uiState: ParentAuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onToggleMode: () -> Unit,
    onSubmit: () -> Unit,
    onDemoClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSignUp = uiState.authMode == AuthMode.SIGN_UP
    val titleText = if (isSignUp) stringResource(R.string.create_parent_account) else stringResource(R.string.welcome_back_parent)
    val subtitleText = if (isSignUp) stringResource(R.string.signup_subtitle) else stringResource(R.string.login_subtitle)
    val buttonText = if (isSignUp) stringResource(R.string.signup_button) else stringResource(R.string.login_button)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Image(
                    painter = painterResource(id = R.drawable.tinysteps_logo),
                    contentDescription = stringResource(id = R.string.app_title),
                    modifier = Modifier.size(72.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = titleText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitleText,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Email field
                Text(
                    text = stringResource(R.string.email_label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    placeholder = { Text(stringResource(R.string.email_placeholder)) },
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
                    isError = uiState.emailErrorRes != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
                if (uiState.emailErrorRes != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(uiState.emailErrorRes),
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Password field
                Text(
                    text = stringResource(R.string.password_label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    placeholder = { Text("••••••••") },
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
                    isError = uiState.passwordErrorRes != null,
                    visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = onTogglePasswordVisibility) {
                            Icon(
                                imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = stringResource(R.string.toggle_password_visibility),
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                )
                if (uiState.passwordErrorRes != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(uiState.passwordErrorRes),
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (isSignUp) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Confirm Password field
                    Text(
                        text = stringResource(R.string.confirm_password_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = uiState.confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        placeholder = { Text("••••••••") },
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
                        isError = uiState.confirmPasswordErrorRes != null,
                        visualTransformation = if (uiState.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = onToggleConfirmPasswordVisibility) {
                                Icon(
                                    imageVector = if (uiState.isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = stringResource(R.string.toggle_password_visibility),
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    )
                    if (uiState.confirmPasswordErrorRes != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(uiState.confirmPasswordErrorRes),
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Remember Me on this device checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onRememberMeChange(!uiState.rememberMe) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = uiState.rememberMe,
                        onCheckedChange = onRememberMeChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryGreen,
                        ),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.remember_me),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                if (uiState.authError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.authError,
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Submit Button
                Button(
                    onClick = onSubmit,
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        contentColor = Color.White,
                    ),
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                        )
                    } else {
                        Text(
                            text = buttonText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Temporary Demo Mode Button
                OutlinedButton(
                    onClick = onDemoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(1.dp, PrimaryGreen),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryGreen,
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.demo_mode_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Toggle Mode Option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isSignUp) stringResource(R.string.has_account_prompt) else stringResource(R.string.no_account_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = onToggleMode) {
                    Text(
                        text = if (isSignUp) stringResource(R.string.login_button) else stringResource(R.string.signup_button),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ParentAuthScreenLoginPreview() {
    TinyStepsTheme {
        ParentAuthScreen(
            uiState = ParentAuthUiState(
                email = "parent@example.com",
                authMode = AuthMode.LOGIN,
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onRememberMeChange = {},
            onTogglePasswordVisibility = {},
            onToggleConfirmPasswordVisibility = {},
            onToggleMode = {},
            onSubmit = {},
            onDemoClick = {},
            onBack = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ParentAuthScreenSignUpPreview() {
    TinyStepsTheme {
        ParentAuthScreen(
            uiState = ParentAuthUiState(
                email = "parent@example.com",
                authMode = AuthMode.SIGN_UP,
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onRememberMeChange = {},
            onTogglePasswordVisibility = {},
            onToggleConfirmPasswordVisibility = {},
            onToggleMode = {},
            onSubmit = {},
            onDemoClick = {},
            onBack = {},
        )
    }
}
