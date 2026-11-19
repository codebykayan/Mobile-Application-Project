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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
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

data class ChildBadgeUi(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = true
)

data class ChildProfileUiState(
    val childId: String = "1",
    val childName: String = "Sara",
    val age: Int = 8,
    val buddyAvatar: String = "🐰",
    val stars: Int = 45,
    val completedTasks: Int = 17,
    val dayStreak: Int = 4,
    val favoriteColor: Long = 0xFF25844F,
    val availableColors: List<Long> = listOf(
        0xFF25844F, // Green
        0xFF3B82F6, // Blue
        0xFFF97316, // Orange
        0xFF0EA5E9, // Light Blue
        0xFFF59E0B, // Gold
        0xFFA855F7  // Purple
    ),
    val badges: List<ChildBadgeUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: ChildTab = ChildTab.ME
)

class ChildProfileViewModel : ViewModel() {
    private val saraBadges = listOf(
        ChildBadgeUi("b1", "First task", "🌟", isUnlocked = true),
        ChildBadgeUi("b2", "4 days", "🔥", isUnlocked = true),
        ChildBadgeUi("b3", "Early bird", "🌅", isUnlocked = true),
        ChildBadgeUi("b4", "10 tasks", "🏆", isUnlocked = false)
    )

    private val omarBadges = listOf(
        ChildBadgeUi("b1", "First task", "🌟", isUnlocked = true),
        ChildBadgeUi("b2", "4 days", "🔥", isUnlocked = false),
        ChildBadgeUi("b3", "Early bird", "🌅", isUnlocked = true),
        ChildBadgeUi("b4", "10 tasks", "🏆", isUnlocked = true)
    )

    var uiState by mutableStateOf(ChildProfileUiState())
        private set

    fun initialize(childId: String) {
        val name: String
        val age: Int
        val avatar: String
        val stars: Int
        val tasks: Int
        val streak: Int
        val badgeList: List<ChildBadgeUi>

        if (childId == "2") {
            name = "Omar"
            age = 10
            avatar = "🐻"
            stars = 30
            tasks = 10
            streak = 2
            badgeList = omarBadges
        } else {
            name = "Sara"
            age = 8
            avatar = "🐰"
            stars = 45
            tasks = 17
            streak = 4
            badgeList = saraBadges
        }

        uiState = ChildProfileUiState(
            childId = childId,
            childName = name,
            age = age,
            buddyAvatar = avatar,
            stars = stars,
            completedTasks = tasks,
            dayStreak = streak,
            badges = badgeList,
            selectedTab = ChildTab.ME
        )
    }

    fun onColorSelected(colorHex: Long) {
        uiState = uiState.copy(favoriteColor = colorHex)
    }

    fun onTabSelected(tab: ChildTab) {
        uiState = uiState.copy(selectedTab = tab)
    }
}

@Composable
fun ChildProfileRoute(
    childId: String,
    onChangeBuddy: (String) -> Unit,
    onTabSelected: (ChildTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChildProfileViewModel = viewModel()
) {
    LaunchedEffect(childId) {
        viewModel.initialize(childId)
    }

    ChildProfileScreen(
        uiState = viewModel.uiState,
        onColorSelected = viewModel::onColorSelected,
        onChangeBuddy = { onChangeBuddy(viewModel.uiState.childId) },
        onTabSelected = { tab ->
            viewModel.onTabSelected(tab)
            onTabSelected(tab)
        },
        modifier = modifier
    )
}

@Composable
fun ChildProfileScreen(
    uiState: ChildProfileUiState,
    onColorSelected: (Long) -> Unit,
    onChangeBuddy: () -> Unit,
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Top Profile Header (Avatar, Name, Age)
                    ChildProfileHeader(
                        name = uiState.childName,
                        age = uiState.age,
                        avatarEmoji = uiState.buddyAvatar
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Three Stat Summary Cards (Stars, Tasks done, Day streak)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Stat 1: Stars
                        ChildStatCard(
                            valueText = uiState.stars.toString(),
                            label = stringResource(id = R.string.label_stars_stat),
                            valueColor = Color(0xFFD97706),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFEAB308),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )

                        // Stat 2: Tasks done
                        ChildStatCard(
                            valueText = uiState.completedTasks.toString(),
                            label = stringResource(id = R.string.label_tasks_done_stat),
                            valueColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )

                        // Stat 3: Day streak
                        ChildStatCard(
                            valueText = uiState.dayStreak.toString(),
                            label = stringResource(id = R.string.label_day_streak_stat),
                            valueColor = Color(0xFFF97316),
                            icon = {
                                Text(text = "🔥", fontSize = 18.sp)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Section 1: My Color Selector
                    ProfileColorSelector(
                        selectedColor = uiState.favoriteColor,
                        colors = uiState.availableColors,
                        onColorSelected = onColorSelected
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Section 2: My Badges
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = stringResource(id = R.string.my_badges_heading),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            uiState.badges.forEach { badge ->
                                ChildBadgeCard(badge = badge)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f, fill = false))
                    Spacer(modifier = Modifier.height(36.dp))

                    // Change My Buddy Action Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onChangeBuddy)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.btn_change_my_buddy),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

@Composable
fun ChildProfileHeader(
    name: String,
    age: Int,
    avatarEmoji: String,
    modifier: Modifier = Modifier
) {
    val avatarBgColor = if (avatarEmoji.contains("🐰") || name == "Sara") {
        Color(0xFFFEF3C7) // Light yellow
    } else {
        Color(0xFFDBEAFE) // Light blue
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // Large Avatar Circle
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(avatarBgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(text = avatarEmoji, fontSize = 52.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Child Name
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Age Subtitle
        Text(
            text = "$age years old",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ChildStatCard(
    valueText: String,
    label: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    icon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier.height(88.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = valueColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProfileColorSelector(
    selectedColor: Long,
    colors: List<Long>,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.label_my_color),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.forEach { colorHex ->
                val isSelected = selectedColor == colorHex
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(colorHex))
                        .then(
                            if (isSelected) {
                                Modifier.border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onColorSelected(colorHex) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChildBadgeCard(
    badge: ChildBadgeUi,
    modifier: Modifier = Modifier
) {
    val bgCircleColor = if (badge.isUnlocked) Color(0xFFFEF3C7) else Color(0xFFF1F5F9)
    val borderStroke = if (badge.isUnlocked) BorderStroke(2.dp, Color(0xFFFFC94A)) else BorderStroke(1.dp, Color(0xFFE2E8F0))
    val titleColor = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else Color(0xFF94A3B8)
    val iconAlpha = if (badge.isUnlocked) 1.0f else 0.4f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(bgCircleColor)
                .border(borderStroke.width, borderStroke.brush, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = badge.iconEmoji,
                fontSize = 28.sp,
                modifier = Modifier.alpha(iconAlpha)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = badge.title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.sp,
                fontWeight = if (badge.isUnlocked) FontWeight.Bold else FontWeight.Medium
            ),
            color = titleColor,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildProfileDefaultPreview() {
    TinyStepsTheme {
        ChildProfileScreen(
            uiState = ChildProfileUiState(
                childName = "Sara",
                age = 8,
                buddyAvatar = "🐰",
                stars = 45,
                completedTasks = 17,
                dayStreak = 4,
                badges = listOf(
                    ChildBadgeUi("b1", "First task", "🌟", true),
                    ChildBadgeUi("b2", "4 days", "🔥", true),
                    ChildBadgeUi("b3", "Early bird", "🌅", true),
                    ChildBadgeUi("b4", "10 tasks", "🏆", false)
                )
            ),
            onColorSelected = {},
            onChangeBuddy = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildProfileLockedBadgesPreview() {
    TinyStepsTheme {
        ChildProfileScreen(
            uiState = ChildProfileUiState(
                childName = "Sara",
                age = 8,
                buddyAvatar = "🐰",
                stars = 10,
                completedTasks = 2,
                dayStreak = 1,
                badges = listOf(
                    ChildBadgeUi("b1", "First task", "🌟", true),
                    ChildBadgeUi("b2", "4 days", "🔥", false),
                    ChildBadgeUi("b3", "Early bird", "🌅", false),
                    ChildBadgeUi("b4", "10 tasks", "🏆", false)
                )
            ),
            onColorSelected = {},
            onChangeBuddy = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildProfileEmptyProgressPreview() {
    TinyStepsTheme {
        ChildProfileScreen(
            uiState = ChildProfileUiState(
                childName = "Omar",
                age = 10,
                buddyAvatar = "🐻",
                stars = 0,
                completedTasks = 0,
                dayStreak = 0,
                badges = listOf(
                    ChildBadgeUi("b1", "First task", "🌟", false),
                    ChildBadgeUi("b2", "4 days", "🔥", false),
                    ChildBadgeUi("b3", "Early bird", "🌅", false),
                    ChildBadgeUi("b4", "10 tasks", "🏆", false)
                )
            ),
            onColorSelected = {},
            onChangeBuddy = {},
            onTabSelected = {}
        )
    }
}
