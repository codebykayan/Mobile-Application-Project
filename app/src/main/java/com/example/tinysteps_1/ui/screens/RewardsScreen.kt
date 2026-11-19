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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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

data class RewardUi(
    val id: String,
    val name: String,
    val image: String,
    val starsRequired: Int,
    val childNames: List<String>
)

data class RewardsUiState(
    val rewards: List<RewardUi> = emptyList(),
    val pendingRequestsCount: Int = 2,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: ParentTab = ParentTab.REWARDS
)

class RewardsViewModel : ViewModel() {
    private val sampleRewards = listOf(
        RewardUi("r1", "Ice cream trip", "🍦", 30, listOf("Sara", "Omar")),
        RewardUi("r2", "30 min extra game time", "🎮", 20, listOf("Sara", "Omar")),
        RewardUi("r3", "Afternoon at the park", "🌳", 50, listOf("Sara", "Omar")),
        RewardUi("r4", "A new story book", "📚", 60, listOf("Sara"))
    )

    var uiState by mutableStateOf(
        RewardsUiState(
            rewards = sampleRewards,
            pendingRequestsCount = 2,
            selectedTab = ParentTab.REWARDS
        )
    )
        private set

    fun onTabSelected(tab: ParentTab) {
        uiState = uiState.copy(selectedTab = tab)
    }
}

@Composable
fun RewardsRoute(
    onRewardClick: (String) -> Unit,
    onAddReward: () -> Unit,
    onRequestsClick: () -> Unit,
    onTabSelected: (ParentTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RewardsViewModel = viewModel()
) {
    RewardsScreen(
        uiState = viewModel.uiState,
        onRewardClick = onRewardClick,
        onAddReward = onAddReward,
        onRequestsClick = onRequestsClick,
        onTabSelected = { tab ->
            viewModel.onTabSelected(tab)
            onTabSelected(tab)
        },
        modifier = modifier
    )
}

@Composable
fun RewardsScreen(
    uiState: RewardsUiState,
    onRewardClick: (String) -> Unit,
    onAddReward: () -> Unit,
    onRequestsClick: () -> Unit,
    onTabSelected: (ParentTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF7F9FB),
        bottomBar = {
            ParentBottomBar(
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Screen Title
                        Text(
                            text = stringResource(id = R.string.rewards_title),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sub-header Row: Rewards & Requests Chips
                        RewardsHeaderRow(
                            pendingRequestsCount = uiState.pendingRequestsCount,
                            onRequestsClick = onRequestsClick
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Reward Info Banner
                        RewardInfoBanner()

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Reward List Items
                    if (uiState.rewards.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(id = R.string.empty_rewards_title),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(id = R.string.empty_rewards_subtitle),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(uiState.rewards) { reward ->
                            RewardCard(
                                reward = reward,
                                onClick = { onRewardClick(reward.id) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }

                // Green Floating "+ Add reward" Button
                Button(
                    onClick = onAddReward,
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 20.dp)
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.btn_add_reward),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun RewardsHeaderRow(
    pendingRequestsCount: Int,
    onRequestsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Selected "Rewards" Chip
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFE8F5EE))
                .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                .padding(horizontal = 18.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CardGiftcard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.tab_rewards_chip),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Unselected "Requests" Chip with Orange Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(20.dp))
                .clickable(onClick = onRequestsClick)
                .padding(horizontal = 18.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.tab_requests_chip),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (pendingRequestsCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF97316)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pendingRequestsCount.toString(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RewardInfoBanner(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5EE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFEAB308),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.info_earn_stars_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(id = R.string.info_earn_stars_subtitle),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color(0xFF1E7844)
                )
            }
        }
    }
}

@Composable
fun RewardCard(
    reward: RewardUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reward Icon Box
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = reward.image, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title and Subtitle (Stars & Children names)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reward.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFEAB308),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(
                            id = R.string.reward_stars_children,
                            reward.starsRequired,
                            reward.childNames.joinToString(", ")
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = Color(0xFFD97706)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Edit Pencil Icon
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = stringResource(id = R.string.edit_reward_content_desc),
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RewardsDefaultPreview() {
    TinyStepsTheme {
        RewardsScreen(
            uiState = RewardsUiState(
                rewards = listOf(
                    RewardUi("r1", "Ice cream trip", "🍦", 30, listOf("Sara", "Omar")),
                    RewardUi("r2", "30 min extra game time", "🎮", 20, listOf("Sara", "Omar")),
                    RewardUi("r3", "Afternoon at the park", "🌳", 50, listOf("Sara", "Omar")),
                    RewardUi("r4", "A new story book", "📚", 60, listOf("Sara"))
                ),
                pendingRequestsCount = 2
            ),
            onRewardClick = {},
            onAddReward = {},
            onRequestsClick = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RewardsEmptyPreview() {
    TinyStepsTheme {
        RewardsScreen(
            uiState = RewardsUiState(
                rewards = emptyList(),
                pendingRequestsCount = 0
            ),
            onRewardClick = {},
            onAddReward = {},
            onRequestsClick = {},
            onTabSelected = {}
        )
    }
}
