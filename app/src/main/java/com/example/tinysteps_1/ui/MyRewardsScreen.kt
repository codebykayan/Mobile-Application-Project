package com.example.tinysteps_1.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinysteps_1.R
import com.example.tinysteps_1.ui.theme.TinyStepsTheme

data class ChildRewardRequestUi(
    val requestId: String,
    val rewardName: String,
    val rewardImage: String,
    val status: RewardRequestStatus = RewardRequestStatus.PENDING,
    val parentMessage: String? = null
)

data class MyRewardsUiState(
    val childId: String = "1",
    val requests: List<ChildRewardRequestUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: ChildTab = ChildTab.REWARDS
)

class MyRewardsViewModel : ViewModel() {
    private val saraRequests = listOf(
        ChildRewardRequestUi(
            requestId = "req1",
            rewardName = "Ice cream trip",
            rewardImage = "🍦",
            status = RewardRequestStatus.PENDING,
            parentMessage = null
        ),
        ChildRewardRequestUi(
            requestId = "req2",
            rewardName = "Afternoon at the park",
            rewardImage = "🌳",
            status = RewardRequestStatus.APPROVED,
            parentMessage = "Mom: \"Let's go on Friday!\""
        ),
        ChildRewardRequestUi(
            requestId = "req3",
            rewardName = "Movie night",
            rewardImage = "🎬",
            status = RewardRequestStatus.REJECTED,
            parentMessage = "Mom: \"Maybe next week, finish your homework first.\""
        )
    )

    private val omarRequests = listOf(
        ChildRewardRequestUi(
            requestId = "req4",
            rewardName = "30 min extra game time",
            rewardImage = "🎮",
            status = RewardRequestStatus.PENDING,
            parentMessage = null
        )
    )

    var uiState by mutableStateOf(MyRewardsUiState())
        private set

    fun initialize(childId: String) {
        val requestsList = if (childId == "2") omarRequests else saraRequests

        uiState = MyRewardsUiState(
            childId = childId,
            requests = requestsList,
            selectedTab = ChildTab.REWARDS
        )
    }

    fun onTabSelected(tab: ChildTab) {
        uiState = uiState.copy(selectedTab = tab)
    }
}

@Composable
fun MyRewardsRoute(
    childId: String,
    onBack: () -> Unit,
    onTabSelected: (ChildTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyRewardsViewModel = viewModel()
) {
    LaunchedEffect(childId) {
        viewModel.initialize(childId)
    }

    MyRewardsScreen(
        uiState = viewModel.uiState,
        onBack = onBack,
        onTabSelected = { tab ->
            viewModel.onTabSelected(tab)
            onTabSelected(tab)
        },
        modifier = modifier
    )
}

@Composable
fun MyRewardsScreen(
    uiState: MyRewardsUiState,
    onBack: () -> Unit,
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
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
                                text = stringResource(id = R.string.my_rewards_title),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Empty State
                    if (uiState.requests.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(id = R.string.empty_my_rewards_title),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(id = R.string.empty_my_rewards_subtitle),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(uiState.requests) { request ->
                            MyRewardCard(request = request)
                        }

                        // Information Banner at bottom of list
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5EE)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFEAB308),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    val noteText = buildAnnotatedString {
                                        append("Your stars come back when a reward is ")
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color(0xFF25844F),
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append("not approved.")
                                        }
                                    }
                                    Text(
                                        text = noteText,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                        color = Color(0xFF5B6474)
                                    )
                                }
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
fun MyRewardCard(
    request: ChildRewardRequestUi,
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
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Reward Emoji Box
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8FAFC)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = request.rewardImage, fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = request.rewardName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    RewardStatusBadge(status = request.status)
                }
            }

            // Optional Parent Message
            if (!request.parentMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                ParentMessageCard(message = request.parentMessage)
            }
        }
    }
}

@Composable
fun RewardStatusBadge(
    status: RewardRequestStatus,
    modifier: Modifier = Modifier
) {
    when (status) {
        RewardRequestStatus.PENDING -> {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFEF3C7))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.status_waiting_for_mom),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFFD97706)
                )
            }
        }
        RewardRequestStatus.APPROVED -> {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE8F5EE))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.status_approved_yes),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        RewardRequestStatus.REJECTED -> {
            Text(
                text = stringResource(id = R.string.status_not_this_time),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF64748B),
                modifier = modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun ParentMessageCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFC))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = null,
                tint = Color(0xFF5B6474),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = Color(0xFF5B6474)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyRewardsMixedStatusPreview() {
    TinyStepsTheme {
        MyRewardsScreen(
            uiState = MyRewardsUiState(
                requests = listOf(
                    ChildRewardRequestUi("req1", "Ice cream trip", "🍦", RewardRequestStatus.PENDING),
                    ChildRewardRequestUi("req2", "Afternoon at the park", "🌳", RewardRequestStatus.APPROVED, "Mom: \"Let's go on Friday!\""),
                    ChildRewardRequestUi("req3", "Movie night", "🎬", RewardRequestStatus.REJECTED, "Mom: \"Maybe next week, finish your homework first.\"")
                )
            ),
            onBack = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyRewardsPendingOnlyPreview() {
    TinyStepsTheme {
        MyRewardsScreen(
            uiState = MyRewardsUiState(
                requests = listOf(
                    ChildRewardRequestUi("req1", "Ice cream trip", "🍦", RewardRequestStatus.PENDING)
                )
            ),
            onBack = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyRewardsEmptyPreview() {
    TinyStepsTheme {
        MyRewardsScreen(
            uiState = MyRewardsUiState(requests = emptyList()),
            onBack = {},
            onTabSelected = {}
        )
    }
}
