package com.example.tinystep_stage2

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinystep_stage2.ui.theme.PrimaryGreen
import com.example.tinystep_stage2.ui.theme.SecondaryAccent
import com.example.tinystep_stage2.ui.theme.TinyStepsTheme

data class ChildProfile(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val avatarColor: Long = 0xFFFFE3A3,
)

data class ChooseChildUiState(
    val children: List<ChildProfile> = emptyList(),
    val selectedChildId: String? = null,
)

class ChooseChildViewModel : ViewModel() {
    var uiState by mutableStateOf(
        ChooseChildUiState(
            children = listOf(
                ChildProfile(id = "1", name = "Sara", age = 8, avatarColor = 0xFFFFE3A3),
                ChildProfile(id = "2", name = "Omar", age = 10, avatarColor = 0xFFCDE4FA),
            ),
        )
    )
        private set
}

@Composable
fun ChooseChildRoute(
    modifier: Modifier = Modifier,
    viewModel: ChooseChildViewModel = viewModel(),
    onChildChosen: (String) -> Unit = {},
    onAddChildClick: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    var selectedChildId by rememberSaveable { mutableStateOf<String?>(null) }
    val uiState = ChooseChildUiState(
        children = viewModel.uiState.children,
        selectedChildId = selectedChildId,
    )

    ChooseChildScreen(
        uiState = uiState,
        onChildSelected = { childId ->
            selectedChildId = childId
        },
        onContinue = {
            selectedChildId?.let { onChildChosen(it) }
        },
        onAddChild = onAddChildClick,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun ChooseChildScreen(
    uiState: ChooseChildUiState,
    onChildSelected: (String) -> Unit,
    onContinue: () -> Unit,
    onAddChild: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedChild = uiState.children.find { it.id == uiState.selectedChildId }
    val buttonText = selectedChild?.let { stringResource(R.string.lets_go_name, it.name) } ?: stringResource(R.string.lets_go)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        onClick = onBack,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(48.dp),
                        tonalElevation = 1.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.go_back),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.who_are_you),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.tap_your_picture),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(24.dp))

                uiState.children.forEach { child ->
                    ChildCard(
                        child = child,
                        selected = child.id == uiState.selectedChildId,
                        onClick = { onChildSelected(child.id) },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Connect My Account option
                Surface(
                    onClick = onAddChild,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, PrimaryGreen),
                    tonalElevation = 0.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.connect_my_account),
                            tint = PrimaryGreen,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.connect_my_account),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                        )
                    }
                }
            }

            Button(
                onClick = onContinue,
                enabled = uiState.selectedChildId != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                ),
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
        }
    }
}

@Composable
fun ChildCard(
    child: ChildProfile,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) SecondaryAccent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val backgroundColor = MaterialTheme.colorScheme.surface
    val letterColor = if (child.name == "Omar") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = backgroundColor,
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        tonalElevation = if (selected) 2.dp else 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(child.avatarColor),
                    modifier = Modifier.size(64.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = child.name.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = letterColor,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = child.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.years_old, child.age),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                    )
                }
            }

            if (selected) {
                Surface(
                    shape = CircleShape,
                    color = SecondaryAccent,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.TopEnd),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChooseChildScreenSaraSelectedPreview() {
    val sampleChildren = listOf(
        ChildProfile(id = "1", name = "Sara", age = 8, avatarColor = 0xFFFFE3A3),
        ChildProfile(id = "2", name = "Omar", age = 10, avatarColor = 0xFFCDE4FA),
    )
    TinyStepsTheme {
        ChooseChildScreen(
            uiState = ChooseChildUiState(
                children = sampleChildren,
                selectedChildId = "1",
            ),
            onChildSelected = {},
            onContinue = {},
            onAddChild = {},
            onBack = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChooseChildScreenNothingSelectedPreview() {
    val sampleChildren = listOf(
        ChildProfile(id = "1", name = "Sara", age = 8, avatarColor = 0xFFFFE3A3),
        ChildProfile(id = "2", name = "Omar", age = 10, avatarColor = 0xFFCDE4FA),
    )
    TinyStepsTheme {
        ChooseChildScreen(
            uiState = ChooseChildUiState(
                children = sampleChildren,
                selectedChildId = null,
            ),
            onChildSelected = {},
            onContinue = {},
            onAddChild = {},
            onBack = {},
        )
    }
}
