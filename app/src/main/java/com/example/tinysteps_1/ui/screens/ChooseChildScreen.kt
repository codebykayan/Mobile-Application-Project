package com.example.tinysteps_1.ui

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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

data class ChooseChildUiState(
    val children: List<ChildUi> = emptyList(),
    val selectedChildId: String? = "1",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ChooseChildViewModel : ViewModel() {
    private val sampleChildren = listOf(
        ChildUi(
            id = "1",
            name = "Sara",
            avatarEmoji = "🐰"
        ),
        ChildUi(
            id = "2",
            name = "Omar",
            avatarEmoji = "🐻"
        )
    )

    var uiState by mutableStateOf(
        ChooseChildUiState(
            children = sampleChildren,
            selectedChildId = "1"
        )
    )
        private set

    fun onChildSelected(childId: String) {
        uiState = uiState.copy(selectedChildId = childId)
    }
}

@Composable
fun ChooseChildRoute(
    onChildChosen: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChooseChildViewModel = viewModel()
) {
    ChooseChildScreen(
        uiState = viewModel.uiState,
        onChildSelected = viewModel::onChildSelected,
        onContinue = {
            val selectedId = viewModel.uiState.selectedChildId
            if (selectedId != null) {
                onChildChosen(selectedId)
            }
        },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun ChooseChildScreen(
    uiState: ChooseChildUiState,
    onChildSelected: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedChild = uiState.children.find { it.id == uiState.selectedChildId }

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

            // Header Bar: Circular Back Arrow Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_content_description),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Heading: Who are you?
            Text(
                text = stringResource(id = R.string.who_are_you_heading),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle: Tap your picture
            Text(
                text = stringResource(id = R.string.tap_your_picture_subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Error Message (if any)
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Child Selection Cards List
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                uiState.children.forEach { child ->
                    ChildSelectionCard(
                        child = child,
                        selected = child.id == uiState.selectedChildId,
                        onClick = { onChildSelected(child.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(48.dp))

            // Large Green Button at Bottom ("Let's go, Sara!" or "Continue")
            val isEnabled = uiState.selectedChildId != null && !uiState.isLoading
            Button(
                onClick = onContinue,
                enabled = isEnabled,
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
                    val buttonText = if (selectedChild != null) {
                        stringResource(id = R.string.btn_lets_go_format, selectedChild.name)
                    } else {
                        stringResource(id = R.string.btn_continue)
                    }
                    Text(
                        text = buttonText,
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
fun ChildSelectionCard(
    child: ChildUi,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) Color(0xFFFFC94A) else Color(0xFFE2E8F0)
    val borderWidth = if (selected) 2.5.dp else 1.dp
    val avatarBgColor = if (child.avatarEmoji.contains("🐰") || child.name == "Sara") {
        Color(0xFFFEF3C7) // Light yellow
    } else {
        Color(0xFFDBEAFE) // Light blue
    }
    val ageText = if (child.id == "2" || child.name == "Omar") "10 years old" else "8 years old"

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(borderWidth, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Child Avatar Circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(avatarBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = child.avatarEmoji, fontSize = 32.sp)
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Child Name & Age Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = child.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = ageText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Yellow Checkmark Badge if Selected
            if (selected) {
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFC94A)),
                    contentAlignment = Alignment.Center
                ) {
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

@Preview(showBackground = true)
@Composable
private fun ChooseChildSaraSelectedPreview() {
    TinyStepsTheme {
        ChooseChildScreen(
            uiState = ChooseChildUiState(
                children = listOf(
                    ChildUi("1", "Sara", "🐰"),
                    ChildUi("2", "Omar", "🐻")
                ),
                selectedChildId = "1"
            ),
            onChildSelected = {},
            onContinue = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChooseChildOmarSelectedPreview() {
    TinyStepsTheme {
        ChooseChildScreen(
            uiState = ChooseChildUiState(
                children = listOf(
                    ChildUi("1", "Sara", "🐰"),
                    ChildUi("2", "Omar", "🐻")
                ),
                selectedChildId = "2"
            ),
            onChildSelected = {},
            onContinue = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChooseChildNoSelectionPreview() {
    TinyStepsTheme {
        ChooseChildScreen(
            uiState = ChooseChildUiState(
                children = listOf(
                    ChildUi("1", "Sara", "🐰"),
                    ChildUi("2", "Omar", "🐻")
                ),
                selectedChildId = null
            ),
            onChildSelected = {},
            onContinue = {},
            onBack = {}
        )
    }
}
