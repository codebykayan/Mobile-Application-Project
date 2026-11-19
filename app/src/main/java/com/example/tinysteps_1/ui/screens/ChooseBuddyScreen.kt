package com.example.tinysteps_1.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
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

data class BuddyUi(
    val id: String,
    val name: String,
    val avatarEmoji: String,
    val backgroundColorHex: Long = 0xFFFEF3C7
)

data class ChooseBuddyUiState(
    val buddies: List<BuddyUi> = emptyList(),
    val selectedBuddyId: String? = "b1",
    val childId: String = "1",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ChooseBuddyViewModel : ViewModel() {
    private val defaultBuddies = listOf(
        BuddyUi("b1", "Bunny", "🐰", 0xFFFEF3C7),
        BuddyUi("b2", "Bear", "🐻", 0xFFDBEAFE),
        BuddyUi("b3", "Kitty", "🐱", 0xFFFCE7F3),
        BuddyUi("b4", "Fox", "🦊", 0xFFFFEDD5),
        BuddyUi("b5", "Panda", "🐼", 0xFFF1F5F9),
        BuddyUi("b6", "Froggy", "🐸", 0xFFDCFCE7),
        BuddyUi("b7", "Owl", "🦉", 0xFFEDE9FE),
        BuddyUi("b8", "Lion", "🦁", 0xFFFEF3C7),
        BuddyUi("b9", "Dino", "🦕", 0xFFCCFBF1)
    )

    private val childBuddyMap = mutableMapOf<String, String>(
        "1" to "b1",
        "2" to "b2"
    )

    var uiState by mutableStateOf(ChooseBuddyUiState(buddies = defaultBuddies))
        private set

    fun initialize(childId: String) {
        val selectedBuddy = childBuddyMap[childId] ?: "b1"
        uiState = ChooseBuddyUiState(
            buddies = defaultBuddies,
            selectedBuddyId = selectedBuddy,
            childId = childId
        )
    }

    fun onBuddySelected(buddyId: String) {
        childBuddyMap[uiState.childId] = buddyId
        uiState = uiState.copy(selectedBuddyId = buddyId)
    }
}

@Composable
fun ChooseBuddyRoute(
    childId: String,
    onContinue: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChooseBuddyViewModel = viewModel()
) {
    LaunchedEffect(childId) {
        viewModel.initialize(childId)
    }

    ChooseBuddyScreen(
        uiState = viewModel.uiState,
        onBuddySelected = viewModel::onBuddySelected,
        onContinue = { onContinue(viewModel.uiState.childId) },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun ChooseBuddyScreen(
    uiState: ChooseBuddyUiState,
    onBuddySelected: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedBuddy = uiState.buddies.find { it.id == uiState.selectedBuddyId }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1FAF4)) // Soft mint background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Header Bar: Back Arrow
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

            Spacer(modifier = Modifier.height(8.dp))

            // Large Selected Buddy Avatar
            SelectedBuddyAvatar(buddy = selectedBuddy)

            Spacer(modifier = Modifier.height(20.dp))

            // Heading: Pick your buddy!
            Text(
                text = stringResource(id = R.string.pick_buddy_heading),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle: This picture will be you in TinySteps
            Text(
                text = stringResource(id = R.string.pick_buddy_subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

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

            // 3-Column Grid of 9 Animal Buddies
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(uiState.buddies) { buddy ->
                    BuddyCard(
                        buddy = buddy,
                        selected = buddy.id == uiState.selectedBuddyId,
                        onClick = { onBuddySelected(buddy.id) }
                    )
                }
            }

            // Bottom Green Button ("That's me! ✓")
            val isEnabled = uiState.selectedBuddyId != null && !uiState.isLoading
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
                    Text(
                        text = stringResource(id = R.string.btn_thats_me),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SelectedBuddyAvatar(
    buddy: BuddyUi?,
    modifier: Modifier = Modifier
) {
    val bgHex = buddy?.backgroundColorHex ?: 0xFFFEF3C7
    val emoji = buddy?.avatarEmoji ?: "🐰"

    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color(bgHex))
            .border(3.5.dp, Color(0xFFFFC94A), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 60.sp)
    }
}

@Composable
fun BuddyCard(
    buddy: BuddyUi,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) Color(0xFFFFC94A) else Color(0xFFE2E8F0)
    val borderWidth = if (selected) 2.5.dp else 1.dp
    val cardElevation = if (selected) 2.dp else 0.5.dp

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(borderWidth, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        modifier = modifier
            .fillMaxWidth()
            .height(112.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(buddy.backgroundColorHex)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = buddy.avatarEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = buddy.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChooseBuddyBunnySelectedPreview() {
    val sampleBuddies = listOf(
        BuddyUi("b1", "Bunny", "🐰", 0xFFFEF3C7),
        BuddyUi("b2", "Bear", "🐻", 0xFFDBEAFE),
        BuddyUi("b3", "Kitty", "🐱", 0xFFFCE7F3),
        BuddyUi("b4", "Fox", "🦊", 0xFFFFEDD5),
        BuddyUi("b5", "Panda", "🐼", 0xFFF1F5F9),
        BuddyUi("b6", "Froggy", "🐸", 0xFFDCFCE7),
        BuddyUi("b7", "Owl", "🦉", 0xFFEDE9FE),
        BuddyUi("b8", "Lion", "🦁", 0xFFFEF3C7),
        BuddyUi("b9", "Dino", "🦕", 0xFFCCFBF1)
    )
    TinyStepsTheme {
        ChooseBuddyScreen(
            uiState = ChooseBuddyUiState(
                buddies = sampleBuddies,
                selectedBuddyId = "b1"
            ),
            onBuddySelected = {},
            onContinue = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChooseBuddyBearSelectedPreview() {
    val sampleBuddies = listOf(
        BuddyUi("b1", "Bunny", "🐰", 0xFFFEF3C7),
        BuddyUi("b2", "Bear", "🐻", 0xFFDBEAFE),
        BuddyUi("b3", "Kitty", "🐱", 0xFFFCE7F3),
        BuddyUi("b4", "Fox", "🦊", 0xFFFFEDD5),
        BuddyUi("b5", "Panda", "🐼", 0xFFF1F5F9),
        BuddyUi("b6", "Froggy", "🐸", 0xFFDCFCE7),
        BuddyUi("b7", "Owl", "🦉", 0xFFEDE9FE),
        BuddyUi("b8", "Lion", "🦁", 0xFFFEF3C7),
        BuddyUi("b9", "Dino", "🦕", 0xFFCCFBF1)
    )
    TinyStepsTheme {
        ChooseBuddyScreen(
            uiState = ChooseBuddyUiState(
                buddies = sampleBuddies,
                selectedBuddyId = "b2"
            ),
            onBuddySelected = {},
            onContinue = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChooseBuddyNoSelectionPreview() {
    val sampleBuddies = listOf(
        BuddyUi("b1", "Bunny", "🐰", 0xFFFEF3C7),
        BuddyUi("b2", "Bear", "🐻", 0xFFDBEAFE),
        BuddyUi("b3", "Kitty", "🐱", 0xFFFCE7F3),
        BuddyUi("b4", "Fox", "🦊", 0xFFFFEDD5),
        BuddyUi("b5", "Panda", "🐼", 0xFFF1F5F9),
        BuddyUi("b6", "Froggy", "🐸", 0xFFDCFCE7),
        BuddyUi("b7", "Owl", "🦉", 0xFFEDE9FE),
        BuddyUi("b8", "Lion", "🦁", 0xFFFEF3C7),
        BuddyUi("b9", "Dino", "🦕", 0xFFCCFBF1)
    )
    TinyStepsTheme {
        ChooseBuddyScreen(
            uiState = ChooseBuddyUiState(
                buddies = sampleBuddies,
                selectedBuddyId = null
            ),
            onBuddySelected = {},
            onContinue = {},
            onBack = {}
        )
    }
}
