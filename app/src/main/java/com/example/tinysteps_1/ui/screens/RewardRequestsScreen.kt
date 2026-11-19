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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

enum class RewardRequestStatus {
    PENDING,
    APPROVED,
    REJECTED
}

data class RewardRequestUi(
    val requestId: String,
    val childId: String,
    val childName: String,
    val childAvatar: String,
    val rewardId: String,
    val rewardName: String,
    val rewardImage: String,
    val starsRequired: Int,
    val childStars: Int,
    val requestedAt: String,
    val status: RewardRequestStatus = RewardRequestStatus.PENDING
)

data class RewardRequestsUiState(
    val pendingRequests: List<RewardRequestUi> = emptyList(),
    val earlierRequests: List<RewardRequestUi> = emptyList(),
    val pendingRequestsCount: Int = 2,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: ParentTab = ParentTab.REWARDS
)

class RewardRequestsViewModel : ViewModel() {
    private val initialPendingRequests = listOf(
        RewardRequestUi(
            requestId = "req1",
            childId = "1",
            childName = "Sara",
            childAvatar = "🐰",
            rewardId = "r1",
            rewardName = "Ice cream trip",
            rewardImage = "🍦",
            starsRequired = 30,
            childStars = 45,
            requestedAt = "5 min ago",
            status = RewardRequestStatus.PENDING
        ),
        RewardRequestUi(
            requestId = "req2",
            childId = "2",
            childName = "Omar",
            childAvatar = "🐻",
            rewardId = "r2",
            rewardName = "30 min extra game time",
            rewardImage = "🎮",
            starsRequired = 20,
            childStars = 30,
            requestedAt = "1 hour ago",
            status = RewardRequestStatus.PENDING
        )
    )

    private val initialEarlierRequests = listOf(
        RewardRequestUi(
            requestId = "req3",
            childId = "1",
            childName = "Sara",
            childAvatar = "🐰",
            rewardId = "r3",
            rewardName = "Afternoon at the park",
            rewardImage = "🌳",
            starsRequired = 50,
            childStars = 45,
            requestedAt = "2 days ago",
            status = RewardRequestStatus.APPROVED
        )
    )

    var uiState by mutableStateOf(
        RewardRequestsUiState(
            pendingRequests = initialPendingRequests,
            earlierRequests = initialEarlierRequests,
            pendingRequestsCount = initialPendingRequests.size,
            selectedTab = ParentTab.REWARDS
        )
    )
        private set

    fun approveRequest(requestId: String) {
        val target = uiState.pendingRequests.find { it.requestId == requestId } ?: return
        val updatedPending = uiState.pendingRequests.filter { it.requestId != requestId }
        val updatedEarlier = listOf(target.copy(status = RewardRequestStatus.APPROVED)) + uiState.earlierRequests

        uiState = uiState.copy(
            pendingRequests = updatedPending,
            earlierRequests = updatedEarlier,
            pendingRequestsCount = updatedPending.size
        )
    }

    fun rejectRequest(requestId: String) {
        val target = uiState.pendingRequests.find { it.requestId == requestId } ?: return
        val updatedPending = uiState.pendingRequests.filter { it.requestId != requestId }
        val updatedEarlier = listOf(target.copy(status = RewardRequestStatus.REJECTED)) + uiState.earlierRequests

        uiState = uiState.copy(
            pendingRequests = updatedPending,
            earlierRequests = updatedEarlier,
            pendingRequestsCount = updatedPending.size
        )
    }

    fun onTabSelected(tab: ParentTab) {
        uiState = uiState.copy(selectedTab = tab)
    }
}

@Composable
fun RewardRequestsRoute(
    onRewardsClick: () -> Unit,
    onTabSelected: (ParentTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RewardRequestsViewModel = viewModel()
) {
    RewardRequestsScreen(
        uiState = viewModel.uiState,
        onApprove = viewModel::approveRequest,
        onReject = viewModel::rejectRequest,
        onRewardsClick = onRewardsClick,
        onTabSelected = { tab ->
            viewModel.onTabSelected(tab)
            onTabSelected(tab)
        },
        modifier = modifier
    )
}

@Composable
fun RewardRequestsScreen(
    uiState: RewardRequestsUiState,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onRewardsClick: () -> Unit,
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

                        // Title: Rewards
                        Text(
                            text = stringResource(id = R.string.rewards_title),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sub-header Row: Rewards & Requests Chips (Requests Selected)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Unselected "Rewards" Chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(20.dp))
                                    .clickable(onClick = onRewardsClick)
                                    .padding(horizontal = 18.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.CardGiftcard,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(id = R.string.tab_rewards_chip),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Selected "Requests 2" Chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFE8F5EE))
                                    .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 18.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = stringResource(id = R.string.tab_requests_chip),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (uiState.pendingRequestsCount > 0) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF97316)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = uiState.pendingRequestsCount.toString(),
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

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Pending Requests List
                    if (uiState.pendingRequests.isEmpty() && uiState.earlierRequests.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(id = R.string.empty_requests_title),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(id = R.string.empty_requests_subtitle),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(uiState.pendingRequests) { request ->
                            RewardRequestCard(
                                request = request,
                                onApprove = { onApprove(request.requestId) },
                                onReject = { onReject(request.requestId) }
                            )
                        }

                        // Earlier Section
                        if (uiState.earlierRequests.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(id = R.string.section_earlier),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            items(uiState.earlierRequests) { request ->
                                RequestHistoryCard(request = request)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RewardRequestCard(
    request: RewardRequestUi,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarBgColor = if (request.childAvatar.contains("🐰") || request.childName == "Sara") {
        Color(0xFFFEF3C7) // Light yellow
    } else {
        Color(0xFFDBEAFE) // Light blue
    }

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
                .padding(20.dp)
        ) {
            // Header Row: Avatar + "Sara asked for a reward" + "5 min ago"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(avatarBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = request.childAvatar, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = stringResource(id = R.string.request_asked_format, request.childName),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = request.requestedAt,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inner Details Box
            RequestRewardDetails(
                rewardName = request.rewardName,
                rewardImage = request.rewardImage,
                starsRequired = request.starsRequired,
                childStars = request.childStars
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons Row: Not now & Approve
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.btn_not_now),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = onApprove,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.btn_approve),
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
}

@Composable
fun RequestRewardDetails(
    rewardName: String,
    rewardImage: String,
    starsRequired: Int,
    childStars: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8FAFC))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = rewardImage, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = rewardName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
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
                            id = R.string.request_stars_has_format,
                            starsRequired,
                            childStars
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFFD97706)
                    )
                }
            }
        }
    }
}

@Composable
fun RequestHistoryCard(
    request: RewardRequestUi,
    modifier: Modifier = Modifier
) {
    val avatarBgColor = if (request.childAvatar.contains("🐰") || request.childName == "Sara") {
        Color(0xFFFEF3C7) // Light yellow
    } else {
        Color(0xFFDBEAFE) // Light blue
    }

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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(avatarBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = request.childAvatar, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = stringResource(
                    id = R.string.history_child_reward_format,
                    request.childName,
                    request.rewardName
                ),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Status Badge (Approved vs Not approved)
            if (request.status == RewardRequestStatus.APPROVED) {
                Text(
                    text = stringResource(id = R.string.status_approved_badge),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5EE))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            } else {
                Text(
                    text = stringResource(id = R.string.status_rejected_badge),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF64748B),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RewardRequestsDefaultPreview() {
    TinyStepsTheme {
        RewardRequestsScreen(
            uiState = RewardRequestsUiState(
                pendingRequests = listOf(
                    RewardRequestUi("req1", "1", "Sara", "🐰", "r1", "Ice cream trip", "🍦", 30, 45, "5 min ago"),
                    RewardRequestUi("req2", "2", "Omar", "🐻", "r2", "30 min extra game time", "🎮", 20, 30, "1 hour ago")
                ),
                earlierRequests = listOf(
                    RewardRequestUi("req3", "1", "Sara", "🐰", "r3", "Afternoon at the park", "🌳", 50, 45, "2 days ago", RewardRequestStatus.APPROVED)
                ),
                pendingRequestsCount = 2
            ),
            onApprove = {},
            onReject = {},
            onRewardsClick = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RewardRequestsEmptyPreview() {
    TinyStepsTheme {
        RewardRequestsScreen(
            uiState = RewardRequestsUiState(
                pendingRequests = emptyList(),
                earlierRequests = emptyList(),
                pendingRequestsCount = 0
            ),
            onApprove = {},
            onReject = {},
            onRewardsClick = {},
            onTabSelected = {}
        )
    }
}
