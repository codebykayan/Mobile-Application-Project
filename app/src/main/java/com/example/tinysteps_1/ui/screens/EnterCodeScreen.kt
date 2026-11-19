package com.example.tinysteps_1.ui

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
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinysteps_1.R
import com.example.tinysteps_1.ui.theme.TinyStepsTheme

object MockPairingRepository {
    private val validCodes = mutableMapOf<String, Boolean>(
        "482913" to true,
        "123456" to true
    )

    fun registerCode(code: String) {
        validCodes[code] = true
    }

    fun isCodeValid(code: String): Boolean {
        return validCodes[code] == true
    }

    fun consumeCode(code: String): Boolean {
        if (validCodes[code] == true) {
            validCodes[code] = false
            return true
        }
        return false
    }
}

data class EnterCodeUiState(
    val enteredCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isConnected: Boolean = false
)

class EnterCodeViewModel : ViewModel() {
    var uiState by mutableStateOf(EnterCodeUiState())
        private set

    fun onDigitEntered(digit: Int) {
        if (uiState.enteredCode.length < 6) {
            val updatedCode = uiState.enteredCode + digit
            uiState = uiState.copy(enteredCode = updatedCode, errorMessage = null)
        }
    }

    fun onDeleteDigit() {
        if (uiState.enteredCode.isNotEmpty()) {
            val updatedCode = uiState.enteredCode.dropLast(1)
            uiState = uiState.copy(enteredCode = updatedCode, errorMessage = null)
        }
    }

    fun validateAndConnect(): Boolean {
        val code = uiState.enteredCode
        if (code.length != 6) {
            uiState = uiState.copy(errorMessage = "Please enter all 6 digits")
            return false
        }

        if (MockPairingRepository.isCodeValid(code)) {
            MockPairingRepository.consumeCode(code)
            uiState = uiState.copy(isConnected = true, errorMessage = null)
            return true
        } else {
            uiState = uiState.copy(errorMessage = "Invalid or expired code. Please ask Mom or Dad for a new code.")
            return false
        }
    }
}

@Composable
fun EnterCodeRoute(
    onConnectSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EnterCodeViewModel = viewModel()
) {
    EnterCodeScreen(
        uiState = viewModel.uiState,
        onDigitEntered = viewModel::onDigitEntered,
        onDeleteDigit = viewModel::onDeleteDigit,
        onConnect = {
            if (viewModel.validateAndConnect()) {
                onConnectSuccess()
            }
        },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun EnterCodeScreen(
    uiState: EnterCodeUiState,
    onDigitEntered: (Int) -> Unit,
    onDeleteDigit: () -> Unit,
    onConnect: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1FAF4)) // Soft mint background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Bar: Back Arrow
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Three Animal Avatars Row (Fox 🦊, Panda 🐼, Frog 🐸)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fox 🦊
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEDD5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🦊", fontSize = 28.sp)
                }

                // Panda 🐼
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🐼", fontSize = 28.sp)
                }

                // Frog 🐸
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🐸", fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Heading: Let's connect!
            Text(
                text = stringResource(id = R.string.connect_heading),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle: Ask Mom or Dad for the 6-digit code
            Text(
                text = stringResource(id = R.string.connect_subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error Banner (if any)
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // 6-Digit Code Input Row
            CodeInputRow(code = uiState.enteredCode)

            Spacer(modifier = Modifier.height(36.dp))

            // Custom Number Keypad (0–9 + Delete)
            NumberKeypad(
                onDigitClick = onDigitEntered,
                onDeleteClick = onDeleteDigit
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(28.dp))

            // Connect Button
            val isCodeComplete = uiState.enteredCode.length == 6
            Button(
                onClick = onConnect,
                enabled = isCodeComplete && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFA8D5BA),
                    disabledContentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.btn_connect),
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
fun CodeInputRow(
    code: String,
    codeLength: Int = 6,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val currentFocusIndex = code.length.coerceAtMost(codeLength - 1)

        for (i in 0 until codeLength) {
            val digit = code.getOrNull(i)
            val isFocused = (i == currentFocusIndex && code.length < codeLength)

            CodeDigitBox(
                digit = digit,
                isFocused = isFocused
            )
        }
    }
}

@Composable
fun CodeDigitBox(
    digit: Char?,
    isFocused: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isFocused -> MaterialTheme.colorScheme.primary
        digit != null -> Color(0xFFA8D5BA)
        else -> Color(0xFFE2E8F0)
    }
    val borderWidth = if (isFocused) 2.dp else 1.dp

    Box(
        modifier = modifier
            .size(width = 48.dp, height = 56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(borderWidth, borderColor, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (digit != null) {
            Text(
                text = digit.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun NumberKeypad(
    onDigitClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val rows = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9)
        )

        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { number ->
                    KeypadButton(
                        text = number.toString(),
                        onClick = { onDigitClick(number) }
                    )
                }
            }
        }

        // Bottom Row: Empty Spacer, "0", Delete Key
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(width = 96.dp, height = 54.dp))

            KeypadButton(
                text = "0",
                onClick = { onDigitClick(0) }
            )

            // Delete Key Box
            Box(
                modifier = Modifier
                    .size(width = 96.dp, height = 54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onDeleteClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Backspace,
                    contentDescription = stringResource(id = R.string.delete_digit_content_desc),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier
            .size(width = 96.dp, height = 54.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EnterCodeEmptyPreview() {
    TinyStepsTheme {
        EnterCodeScreen(
            uiState = EnterCodeUiState(enteredCode = ""),
            onDigitEntered = {},
            onDeleteDigit = {},
            onConnect = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EnterCodePartiallyEnteredPreview() {
    TinyStepsTheme {
        EnterCodeScreen(
            uiState = EnterCodeUiState(enteredCode = "482"),
            onDigitEntered = {},
            onDeleteDigit = {},
            onConnect = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EnterCodeInvalidPreview() {
    TinyStepsTheme {
        EnterCodeScreen(
            uiState = EnterCodeUiState(
                enteredCode = "999999",
                errorMessage = "Invalid or expired code. Please ask Mom or Dad for a new code."
            ),
            onDigitEntered = {},
            onDeleteDigit = {},
            onConnect = {},
            onBack = {}
        )
    }
}
