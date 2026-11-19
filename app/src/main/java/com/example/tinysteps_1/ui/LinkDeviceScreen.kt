package com.example.tinysteps_1.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinysteps_1.R
import com.example.tinysteps_1.ui.theme.TinyStepsTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LinkDeviceUiState(
    val childId: String = "",
    val childName: String = "Sara",
    val childAvatar: String = "🐰",
    val pairingCode: String = "482913",
    val remainingSeconds: Int = 582,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LinkDeviceViewModel : ViewModel() {
    var uiState by mutableStateOf(LinkDeviceUiState())
        private set

    private var timerJob: Job? = null

    fun initialize(childId: String) {
        val (name, avatar) = when (childId) {
            "2" -> Pair("Omar", "🐻")
            else -> Pair("Sara", "🐰")
        }
        val code = generateRandomCode()
        uiState = LinkDeviceUiState(
            childId = childId,
            childName = name,
            childAvatar = avatar,
            pairingCode = code,
            remainingSeconds = 582
        )
        startCountdownTimer()
    }

    fun generateNewCode() {
        val newCode = generateRandomCode()
        uiState = uiState.copy(
            pairingCode = newCode,
            remainingSeconds = 600,
            errorMessage = null
        )
        startCountdownTimer()
    }

    private fun startCountdownTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (uiState.remainingSeconds > 0) {
                delay(1000L)
                uiState = uiState.copy(remainingSeconds = uiState.remainingSeconds - 1)
            }
        }
    }

    private fun generateRandomCode(): String {
        return (100000..999999).random().toString()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

@Composable
fun LinkDeviceRoute(
    childId: String,
    onDone: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LinkDeviceViewModel = viewModel()
) {
    LaunchedEffect(childId) {
        viewModel.initialize(childId)
    }

    LinkDeviceScreen(
        uiState = viewModel.uiState,
        onGenerateNewCode = viewModel::generateNewCode,
        onDone = onDone,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun LinkDeviceScreen(
    uiState: LinkDeviceUiState,
    onGenerateNewCode: () -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
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
                        text = stringResource(id = R.string.link_device_title, uiState.childName),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Centered Animal Avatar
                val avatarBgColor = if (uiState.childAvatar.contains("🐰") || uiState.childName == "Sara") {
                    Color(0xFFFEF3C7) // Light yellow
                } else {
                    Color(0xFFDBEAFE) // Light blue
                }

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(avatarBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.childAvatar, fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Instruction Banner
                val instructionText = buildAnnotatedString {
                    append(stringResource(id = R.string.instruction_prefix, uiState.childName))
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1B2430))) {
                        append(stringResource(id = R.string.instruction_child_bold))
                    }
                    append(stringResource(id = R.string.instruction_suffix))
                }

                Text(
                    text = instructionText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 6-Digit Code Display
                PairingCodeDisplay(code = uiState.pairingCode)

                Spacer(modifier = Modifier.height(16.dp))

                // Countdown Timer Display
                CountdownDisplay(remainingSeconds = uiState.remainingSeconds)

                Spacer(modifier = Modifier.height(24.dp))

                // Numbered Pairing Instruction Card
                PairingInstructionCard(childName = uiState.childName)

                Spacer(modifier = Modifier.weight(1f, fill = false))
                Spacer(modifier = Modifier.height(36.dp))

                // Bottom Action Buttons Row: New code & Done
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onGenerateNewCode,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.btn_new_code),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = onDone,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = R.string.btn_done),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PairingCodeDisplay(
    code: String,
    modifier: Modifier = Modifier
) {
    val paddedCode = code.padEnd(6, ' ')

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        paddedCode.forEach { digit ->
            Box(
                modifier = Modifier
                    .size(width = 48.dp, height = 62.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF1FAF4))
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = digit.toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CountdownDisplay(
    remainingSeconds: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = Color(0xFF5B6474),
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        if (remainingSeconds > 0) {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            val formattedTime = String.format("%02d:%02d", minutes, seconds)

            Text(
                text = stringResource(id = R.string.code_expires_in),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = formattedTime,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                text = stringResource(id = R.string.code_expired),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFFDC2626)
            )
        }
    }
}

@Composable
fun PairingInstructionCard(
    childName: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Step 1
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5EE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "1",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = stringResource(id = R.string.pairing_step_1),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Step 2
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5EE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "2",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))

                val step2Text = buildAnnotatedString {
                    append(stringResource(id = R.string.pairing_step_2_prefix))
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(id = R.string.pairing_step_2_bold))
                    }
                    append(stringResource(id = R.string.pairing_step_2_suffix))
                }

                Text(
                    text = step2Text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Step 3
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5EE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "3",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = stringResource(id = R.string.pairing_step_3),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LinkDeviceActivePreview() {
    TinyStepsTheme {
        LinkDeviceScreen(
            uiState = LinkDeviceUiState(
                childId = "1",
                childName = "Sara",
                childAvatar = "🐰",
                pairingCode = "482913",
                remainingSeconds = 582,
                isLoading = false
            ),
            onGenerateNewCode = {},
            onDone = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LinkDeviceExpiredPreview() {
    TinyStepsTheme {
        LinkDeviceScreen(
            uiState = LinkDeviceUiState(
                childId = "1",
                childName = "Sara",
                childAvatar = "🐰",
                pairingCode = "482913",
                remainingSeconds = 0,
                isLoading = false
            ),
            onGenerateNewCode = {},
            onDone = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LinkDeviceLoadingPreview() {
    TinyStepsTheme {
        LinkDeviceScreen(
            uiState = LinkDeviceUiState(
                childId = "1",
                childName = "Sara",
                childAvatar = "🐰",
                isLoading = true
            ),
            onGenerateNewCode = {},
            onDone = {},
            onBack = {}
        )
    }
}
