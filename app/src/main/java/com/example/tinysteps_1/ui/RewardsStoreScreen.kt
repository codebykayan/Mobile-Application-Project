package com.example.tinysteps_1.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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

enum class ChildRewardStatus {
    CAN_ASK,
    WAITING,
    NEED_MORE_STARS
}

data class ChildRewardUi(
    val rewardId: String,
    val name: String,
    val image: String,
    val starsRequired: Int,
    val requestStatus: ChildRewardStatus = ChildRewardStatus.CAN_ASK
)

data class RewardsStoreUiState(
    val childId: String = "1",
    val availableStars: Int = 45,
    val rewards: List<ChildRewardUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: ChildTab = ChildTab.REWARDS
)

class RewardsStoreViewModel : ViewModel() {
    private val sampleRewards = listOf(
        ChildRewardUi(
            rewardId = "r1",
            name = "Ice cream trip",
            image = "🍦",
            starsRequired = 30,
            requestStatus = ChildRewardStatus.WAITING
        ),
        ChildRewardUi(
            rewardId = "r2",
            name = "30 min extra game time",
            image = "🎮",
            starsRequired = 20,
            requestStatus = ChildRewardStatus.CAN_ASK
        ),
        ChildRewardUi(
            rewardId = "r3",
            name = "Afternoon at the park",
            image = "🌳",
            starsRequired = 50,
            requestStatus = ChildRewardStatus.NEED_MORE_STARS
        ),
        ChildRewardUi(
            rewardId = "r4",
            name = "A new story book",
            image = "📚",
            starsRequired = 60,
            requestStatus = ChildRewardStatus.NEED_MORE_STARS
        )
    )

    var uiState by mutableStateOf(
        RewardsStoreUiState(
            availableStars = 45,
            rewards = sampleRewards,
            selectedTab = ChildTab.REWARDS
        )
    )
        private set

    fun initialize(childId: String) {
        val stars = if (childId == "2") 30 else 45
        val updatedRewards = sampleRewards.map { reward ->
            val status = when {
                reward.requestStatus == ChildRewardStatus.WAITING -> ChildRewardStatus.WAITING
                stars >= reward.starsRequired -> ChildRewardStatus.CAN_ASK
                else -> ChildRewardStatus.NEED_MORE_STARS
            }
            reward.copy(requestStatus = status)
        }

        uiState = uiState.copy(
            childId = childId,
            availableStars = stars,
            rewards = updatedRewards,
            selectedTab = ChildTab.REWARDS
        )
    }

    fun requestReward(rewardId: String) {
        val updatedRewards = uiState.rewards.map { reward ->
            if (reward.rewardId == rewardId && reward.requestStatus == ChildRewardStatus.CAN_ASK) {
                reward.copy(requestStatus = ChildRewardStatus.WAITING)
            } else reward
        }
        uiState = uiState.copy(rewards = updatedRewards)
    }

    fun onTabSelected(tab: ChildTab) {
        uiState = uiState.copy(selectedTab = tab)
    }
}

@Composable
fun RewardsStoreRoute(
    childId: String,
    onMyRewardsClick: () -> Unit,
    onTabSelected: (ChildTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RewardsStoreViewModel = viewModel()
) {
    LaunchedEffect(childId) {
        viewModel.initialize(childId)
    }

    RewardsStoreScreen(
        uiState = viewModel.uiState,
        onRequestReward = viewModel::requestReward,
        onMyRewardsClick = onMyRewardsClick,
        onTabSelected = { tab ->
            viewModel.onTabSelected(tab)
            onTabSelected(tab)
        },
        modifier = modifier
    )
}

@Composable
fun RewardsStoreScreen(
    uiState: RewardsStoreUiState,
    onRequestReward: (String) -> Unit,
    onMyRewardsClick: (() -> Unit)? = null,
    onTabSelected: (ChildTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF1FAF4), // Soft mint background
        bottomBar = {
            ChildBottomBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(span = { GridItemSpan(2) }) {
                        Column {
                            // Heading: Rewards
                            Text(
                                text = stringResource(id = R.string.rewards_title),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Large Yellow Stars Balance Banner
                            StarsBalanceBanner(
                                availableStars = uiState.availableStars,
                                onClick = onMyRewardsClick
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 2-Column Reward Cards List
                    items(uiState.rewards) { reward ->
                        ChildRewardCard(
                            reward = reward,
                            availableStars = uiState.availableStars,
                            onRequest = { onRequestReward(reward.rewardId) }
                        )
                    }

                    item(span = { GridItemSpan(2) }) {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StarsBalanceBanner(
    availableStars: Int,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val bannerModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    } else {
        modifier.fillMaxWidth()
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)), // Light yellow
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = bannerModifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFEAB308),
                modifier = Modifier.size(44.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.my_stars_label),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = Color(0xFF5B6474)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = availableStars.toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFFD97706)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(id = R.string.finish_tasks_get_more),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                ),
                color = Color(0xFFB45309),
                textAlign = TextAlign.End,
                modifier = Modifier.width(120.dp)
            )
        }
    }
}

@Composable
fun ChildRewardCard(
    reward: ChildRewardUi,
    availableStars: Int,
    onRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val neededMore = (reward.starsRequired - availableStars).coerceAtLeast(0)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(264.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Reward Emoji Image Container
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = reward.image, fontSize = 30.sp)
            }

            // Reward Name
            Text(
                text = reward.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.height(40.dp)
            )

            // Star Price Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFEAB308),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = reward.starsRequired.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFFD97706)
                )
            }

            // Progress Bar
            RewardProgressBar(
                availableStars = availableStars,
                starsRequired = reward.starsRequired
            )

            // Action Button
            when {
                reward.requestStatus == ChildRewardStatus.WAITING -> {
                    // Yellow Waiting Button
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = Color(0xFFFEF3C7),
                            disabledContentColor = Color(0xFFD97706)
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = R.string.btn_waiting),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                }
                availableStars >= reward.starsRequired -> {
                    // Green Ask For It Button
                    Button(
                        onClick = onRequest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.btn_ask_for_it),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                else -> {
                    // Muted Grey Button (e.g. "5 more ★")
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = Color(0xFFF1F5F9),
                            disabledContentColor = Color(0xFF64748B)
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.more_stars_needed_format, neededMore),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RewardProgressBar(
    availableStars: Int,
    starsRequired: Int,
    modifier: Modifier = Modifier
) {
    val isEnough = availableStars >= starsRequired
    val progressRatio = (availableStars.toFloat() / starsRequired).coerceIn(0.0f, 1.0f)
    val barColor = if (isEnough) MaterialTheme.colorScheme.primary else Color(0xFFF59E0B)
    val trackBg = if (isEnough) Color(0xFFE8F5EE) else Color(0xFFE2E8F0)

    LinearProgressIndicator(
        progress = { progressRatio },
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape),
        color = barColor,
        trackColor = trackBg
    )
}

@Preview(showBackground = true)
@Composable
private fun RewardsStoreAvailablePreview() {
    TinyStepsTheme {
        RewardsStoreScreen(
            uiState = RewardsStoreUiState(
                availableStars = 45,
                rewards = listOf(
                    ChildRewardUi("r1", "Ice cream trip", "🍦", 30, ChildRewardStatus.WAITING),
                    ChildRewardUi("r2", "30 min extra game time", "🎮", 20, ChildRewardStatus.CAN_ASK),
                    ChildRewardUi("r3", "Afternoon at the park", "🌳", 50, ChildRewardStatus.NEED_MORE_STARS),
                    ChildRewardUi("r4", "A new story book", "📚", 60, ChildRewardStatus.NEED_MORE_STARS)
                )
            ),
            onRequestReward = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RewardsStorePendingPreview() {
    TinyStepsTheme {
        RewardsStoreScreen(
            uiState = RewardsStoreUiState(
                availableStars = 45,
                rewards = listOf(
                    ChildRewardUi("r1", "Ice cream trip", "🍦", 30, ChildRewardStatus.WAITING),
                    ChildRewardUi("r2", "30 min extra game time", "🎮", 20, ChildRewardStatus.WAITING)
                )
            ),
            onRequestReward = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RewardsStoreInsufficientStarsPreview() {
    TinyStepsTheme {
        RewardsStoreScreen(
            uiState = RewardsStoreUiState(
                availableStars = 10,
                rewards = listOf(
                    ChildRewardUi("r1", "Ice cream trip", "🍦", 30, ChildRewardStatus.NEED_MORE_STARS),
                    ChildRewardUi("r2", "30 min extra game time", "🎮", 20, ChildRewardStatus.NEED_MORE_STARS)
                )
            ),
            onRequestReward = {},
            onTabSelected = {}
        )
    }
}
