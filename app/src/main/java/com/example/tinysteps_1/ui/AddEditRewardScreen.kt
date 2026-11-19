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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinysteps_1.R
import com.example.tinysteps_1.ui.theme.TinyStepsTheme

data class AddEditRewardUiState(
    val rewardId: String? = null,
    val name: String = "",
    val selectedImage: String = "🍦",
    val starsRequired: Int = 30,
    val availableImages: List<String> = listOf("🍦", "🎮", "🌳", "📚", "🍕", "🎨", "🧸", "🎬"),
    val children: List<ChildUi> = emptyList(),
    val selectedChildIds: Set<String> = emptySet(),
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AddEditRewardViewModel : ViewModel() {
    private val defaultChildren = listOf(
        ChildUi("1", "Sara", "🐰"),
        ChildUi("2", "Omar", "🐻")
    )

    var uiState by mutableStateOf(AddEditRewardUiState(children = defaultChildren))
        private set

    fun initialize(rewardId: String?) {
        if (rewardId != null && rewardId.isNotBlank() && rewardId != "new") {
            uiState = AddEditRewardUiState(
                rewardId = rewardId,
                name = "Ice cream trip",
                selectedImage = "🍦",
                starsRequired = 30,
                children = defaultChildren,
                selectedChildIds = setOf("1", "2"),
                isEditMode = true
            )
        } else {
            uiState = AddEditRewardUiState(
                rewardId = null,
                name = "",
                selectedImage = "🍦",
                starsRequired = 30,
                children = defaultChildren,
                selectedChildIds = setOf("1", "2"),
                isEditMode = false
            )
        }
    }

    fun onNameChange(newName: String) {
        uiState = uiState.copy(name = newName, errorMessage = null)
    }

    fun onImageSelected(image: String) {
        uiState = uiState.copy(selectedImage = image)
    }

    fun onStarsChange(newStars: Int) {
        if (newStars >= 5) {
            uiState = uiState.copy(starsRequired = newStars, errorMessage = null)
        }
    }

    fun onChildSelected(childId: String) {
        val currentSelected = uiState.selectedChildIds.toMutableSet()
        if (currentSelected.contains(childId)) {
            currentSelected.remove(childId)
        } else {
            currentSelected.add(childId)
        }
        uiState = uiState.copy(selectedChildIds = currentSelected, errorMessage = null)
    }

    fun validateAndSave(): Boolean {
        if (uiState.name.isBlank()) {
            uiState = uiState.copy(errorMessage = "Please enter a reward name")
            return false
        }
        if (uiState.starsRequired <= 0) {
            uiState = uiState.copy(errorMessage = "Stars required must be greater than zero")
            return false
        }
        if (uiState.selectedChildIds.isEmpty()) {
            uiState = uiState.copy(errorMessage = "Please select at least one child")
            return false
        }
        return true
    }
}

@Composable
fun AddEditRewardRoute(
    rewardId: String? = null,
    onSaveSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditRewardViewModel = viewModel()
) {
    LaunchedEffect(rewardId) {
        viewModel.initialize(rewardId)
    }

    AddEditRewardScreen(
        uiState = viewModel.uiState,
        onNameChange = viewModel::onNameChange,
        onImageSelected = viewModel::onImageSelected,
        onStarsChange = viewModel::onStarsChange,
        onChildSelected = viewModel::onChildSelected,
        onSave = {
            if (viewModel.validateAndSave()) {
                onSaveSuccess()
            }
        },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun AddEditRewardScreen(
    uiState: AddEditRewardUiState,
    onNameChange: (String) -> Unit,
    onImageSelected: (String) -> Unit,
    onStarsChange: (Int) -> Unit,
    onChildSelected: (String) -> Unit,
    onSave: () -> Unit,
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
                    text = if (uiState.isEditMode) {
                        stringResource(id = R.string.edit_reward_title)
                    } else {
                        stringResource(id = R.string.new_reward_title)
                    },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error Message Banner (if present)
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Reward Name Label & Input
            Text(
                text = stringResource(id = R.string.label_reward_name),
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
                        text = stringResource(id = R.string.hint_reward_name),
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

            Spacer(modifier = Modifier.height(24.dp))

            // Pick a Picture Section (2-row grid with 8 items)
            Text(
                text = stringResource(id = R.string.label_pick_picture),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val row1 = uiState.availableImages.take(4)
            val row2 = uiState.availableImages.drop(4).take(4)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row1.forEach { emoji ->
                        RewardImageOption(
                            emoji = emoji,
                            selected = uiState.selectedImage == emoji,
                            onClick = { onImageSelected(emoji) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row2.forEach { emoji ->
                        RewardImageOption(
                            emoji = emoji,
                            selected = uiState.selectedImage == emoji,
                            onClick = { onImageSelected(emoji) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stars Needed Section
            Text(
                text = stringResource(id = R.string.label_stars_needed),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            StarsSelector(
                starsRequired = uiState.starsRequired,
                onStarsChange = onStarsChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Who Can Get It Section
            Text(
                text = stringResource(id = R.string.label_who_can_get_it),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                uiState.children.forEach { child ->
                    RewardChildChip(
                        name = child.name,
                        avatar = child.avatarEmoji,
                        selected = uiState.selectedChildIds.contains(child.id),
                        onClick = { onChildSelected(child.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(36.dp))

            // Save Reward Button
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
                            stringResource(id = R.string.btn_save_reward)
                        },
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
fun RewardImageOption(
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerBg = if (selected) Color(0xFFE8F5EE) else Color.White
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0)
    val borderWidth = if (selected) 2.dp else 1.dp

    Box(
        modifier = modifier
            .size(width = 72.dp, height = 72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(containerBg)
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 32.sp)
    }
}

@Composable
fun StarsSelector(
    starsRequired: Int,
    onStarsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val approxTasks = maxOf(1, starsRequired / 10)

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
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Minus Button
                IconButton(
                    onClick = { if (starsRequired > 5) onStarsChange(starsRequired - 5) },
                    enabled = starsRequired > 5,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = null,
                        tint = if (starsRequired > 5) MaterialTheme.colorScheme.onSurface else Color(0xFFCBD5E1),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Star Icon + Count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFEAB308),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = starsRequired.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Plus Button
                IconButton(
                    onClick = { onStarsChange(starsRequired + 5) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Approximate Task Count Label on Right
            Text(
                text = stringResource(id = R.string.approx_tasks_format, approxTasks),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RewardChildChip(
    name: String,
    avatar: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = avatar, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                )
            }
        },
        trailingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else null,
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedContainerColor = Color(0xFFE8F5EE),
            selectedLabelColor = MaterialTheme.colorScheme.primary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = Color(0xFFCBD5E1),
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            borderWidth = 1.dp,
            selectedBorderWidth = 1.5.dp
        ),
        modifier = modifier.heightIn(min = 48.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun AddEditRewardNewPreview() {
    TinyStepsTheme {
        AddEditRewardScreen(
            uiState = AddEditRewardUiState(
                rewardId = null,
                name = "",
                selectedImage = "🍦",
                starsRequired = 30,
                children = listOf(ChildUi("1", "Sara", "🐰"), ChildUi("2", "Omar", "🐻")),
                selectedChildIds = setOf("1", "2"),
                isEditMode = false
            ),
            onNameChange = {},
            onImageSelected = {},
            onStarsChange = {},
            onChildSelected = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditRewardEditPreview() {
    TinyStepsTheme {
        AddEditRewardScreen(
            uiState = AddEditRewardUiState(
                rewardId = "r1",
                name = "Ice cream trip",
                selectedImage = "🍦",
                starsRequired = 30,
                children = listOf(ChildUi("1", "Sara", "🐰"), ChildUi("2", "Omar", "🐻")),
                selectedChildIds = setOf("1", "2"),
                isEditMode = true
            ),
            onNameChange = {},
            onImageSelected = {},
            onStarsChange = {},
            onChildSelected = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditRewardValidationErrorPreview() {
    TinyStepsTheme {
        AddEditRewardScreen(
            uiState = AddEditRewardUiState(
                rewardId = null,
                name = "",
                selectedImage = "🍦",
                starsRequired = 0,
                children = listOf(ChildUi("1", "Sara", "🐰"), ChildUi("2", "Omar", "🐻")),
                selectedChildIds = emptySet(),
                errorMessage = "Please enter a reward name"
            ),
            onNameChange = {},
            onImageSelected = {},
            onStarsChange = {},
            onChildSelected = {},
            onSave = {},
            onBack = {}
        )
    }
}
