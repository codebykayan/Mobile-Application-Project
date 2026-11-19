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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mood
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

enum class ChildTab {
    HOME,
    MY_TASKS,
    REWARDS,
    ME
}

data class ChildTaskUi(
    val id: String,
    val title: String,
    val icon: String = "🎒",
    val currentStep: Int = 6,
    val totalSteps: Int = 6,
    val statusText: String = "Step 6 of 6 · Almost done!",
    val progressRatio: Float = 0.83f
)

data class ChildHomeUiState(
    val childId: String = "1",
    val childName: String = "Sara",
    val buddyAvatar: String = "🐰",
    val stars: Int = 45,
    val favoriteColor: Long = 0xFF25844F,
    val availableColors: List<Long> = listOf(
        0xFF25844F, // Green
        0xFF3B82F6, // Blue
        0xFFF97316, // Orange
        0xFF0EA5E9, // Light Blue
        0xFFF59E0B, // Gold
        0xFFA855F7  // Purple
    ),
    val notStartedCount: Int = 1,
    val inProgressCount: Int = 1,
    val completedCount: Int = 2,
    val currentTask: ChildTaskUi? = ChildTaskUi(
        id = "t1",
        title = "Get Ready for School",
        icon = "🎒",
        currentStep = 6,
        totalSteps = 6,
        statusText = "Step 6 of 6 · Almost done!",
        progressRatio = 0.83f
    ),
    val selectedTab: ChildTab = ChildTab.HOME,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ChildHomeViewModel : ViewModel() {
    var uiState by mutableStateOf(ChildHomeUiState())
        private set

    fun initialize(childId: String) {
        val name: String
        val avatar: String
        val stars: Int
        val notStarted: Int
        val inProgress: Int
        val completed: Int
        val task: ChildTaskUi?

        if (childId == "2") {
            name = "Omar"
            avatar = "🐻"
            stars = 30
            notStarted = 2
            inProgress = 0
            completed = 1
            task = ChildTaskUi("t2", "Clean My Room", "🧹", 2, 4, "Step 2 of 4 · In progress", 0.5f)
        } else {
            name = "Sara"
            avatar = "🐰"
            stars = 45
            notStarted = 1
            inProgress = 1
            completed = 2
            task = ChildTaskUi("t1", "Get Ready for School", "🎒", 6, 6, "Step 6 of 6 · Almost done!", 0.83f)
        }

        uiState = uiState.copy(
            childId = childId,
            childName = name,
            buddyAvatar = avatar,
            stars = stars,
            notStartedCount = notStarted,
            inProgressCount = inProgress,
            completedCount = completed,
            currentTask = task
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
fun ChildHomeRoute(
    childId: String,
    onOpenTask: (String) -> Unit,
    onTabSelected: (ChildTab) -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChildHomeViewModel = viewModel()
) {
    LaunchedEffect(childId) {
        viewModel.initialize(childId)
    }

    ChildHomeScreen(
        uiState = viewModel.uiState,
        onColorSelected = viewModel::onColorSelected,
        onContinueTask = onOpenTask,
        onAvatarClick = onAvatarClick,
        onTabSelected = { tab ->
            viewModel.onTabSelected(tab)
            onTabSelected(tab)
        },
        modifier = modifier
    )
}

@Composable
fun ChildHomeScreen(
    uiState: ChildHomeUiState,
    onColorSelected: (Long) -> Unit,
    onContinueTask: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onTabSelected: (ChildTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarBgColor = if (uiState.buddyAvatar.contains("🐰") || uiState.childName == "Sara") {
        Color(0xFFFEF3C7) // Light yellow
    } else {
        Color(0xFFDBEAFE) // Light blue
    }

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
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Header Row: Avatar, Greeting, Stars Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar Circle (Clickable to change child)
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(avatarBgColor)
                                .clickable(onClick = onAvatarClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = uiState.buddyAvatar, fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Greeting Column
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(id = R.string.greeting_child_format, uiState.childName),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(id = R.string.ready_for_today_subtitle),
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Yellow Stars Badge
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFEAB308),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uiState.stars.toString(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFFD97706)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // My Color Selector Row
                    ColorSelectionRow(
                        selectedColor = uiState.favoriteColor,
                        colors = uiState.availableColors,
                        onColorSelected = onColorSelected
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3 Task Summary Cards Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ChildSummaryCard(
                            count = uiState.notStartedCount,
                            label = stringResource(id = R.string.label_not_started_summary),
                            isWorkingOnIt = false,
                            modifier = Modifier.weight(1f)
                        )
                        ChildSummaryCard(
                            count = uiState.inProgressCount,
                            label = stringResource(id = R.string.label_working_on_it_summary),
                            isWorkingOnIt = true,
                            modifier = Modifier.weight(1f)
                        )
                        ChildSummaryCard(
                            count = uiState.completedCount,
                            label = stringResource(id = R.string.label_completed_summary),
                            isWorkingOnIt = false,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Keep Going Section
                    Text(
                        text = stringResource(id = R.string.keep_going_heading),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Current Task Card / Empty State
                    if (uiState.currentTask != null) {
                        CurrentTaskCard(
                            task = uiState.currentTask,
                            onContinue = { onContinueTask(uiState.currentTask.id) }
                        )
                    } else {
                        // Empty state when all tasks completed
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(id = R.string.empty_child_tasks_title),
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(id = R.string.empty_child_tasks_subtitle),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun ColorSelectionRow(
    selectedColor: Long,
    colors: List<Long>,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.label_my_color),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            colors.forEach { colorHex ->
                val isSelected = selectedColor == colorHex
                Box(
                    modifier = Modifier
                        .size(36.dp)
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
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChildSummaryCard(
    count: Int,
    label: String,
    isWorkingOnIt: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isWorkingOnIt) Color(0xFFEDE9FE) else Color.White
    val borderColor = if (isWorkingOnIt) Color(0xFFDDD6FE) else Color(0xFFE2E8F0)
    val countColor = if (isWorkingOnIt) Color(0xFF5B3FD9) else MaterialTheme.colorScheme.onSurface
    val labelColor = if (isWorkingOnIt) Color(0xFF5B3FD9) else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier.height(96.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = countColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    fontWeight = if (isWorkingOnIt) FontWeight.Bold else FontWeight.Medium
                ),
                color = labelColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CurrentTaskCard(
    task: ChildTaskUi,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5EE)),
        border = BorderStroke(1.dp, Color(0xFFC6EAD7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Task Icon Box (backpack 🎒)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = task.icon, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Task Title
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Step ratio / status text
            Text(
                text = task.statusText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { task.progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color(0xFFC6EAD7)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Green Continue Button
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.btn_continue_arrow),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChildBottomBar(
    selectedTab: ChildTab,
    onTabSelected: (ChildTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0))
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Pair(ChildTab.HOME, Pair(Icons.Outlined.Home, stringResource(id = R.string.tab_home))),
                Pair(ChildTab.MY_TASKS, Pair(Icons.AutoMirrored.Outlined.FormatListBulleted, stringResource(id = R.string.tab_my_tasks))),
                Pair(ChildTab.REWARDS, Pair(Icons.Outlined.CardGiftcard, stringResource(id = R.string.tab_rewards))),
                Pair(ChildTab.ME, Pair(Icons.Outlined.Mood, stringResource(id = R.string.tab_me)))
            )

            tabs.forEach { (tab, iconAndTitle) ->
                val isSelected = selectedTab == tab
                val (icon, label) = iconAndTitle

                val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF64748B)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0xFFE8F5EE) else Color.Transparent)
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = contentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = contentColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildHomeInProgressPreview() {
    TinyStepsTheme {
        ChildHomeScreen(
            uiState = ChildHomeUiState(
                childName = "Sara",
                buddyAvatar = "🐰",
                stars = 45,
                notStartedCount = 1,
                inProgressCount = 1,
                completedCount = 2,
                currentTask = ChildTaskUi("t1", "Get Ready for School", "🎒", 6, 6, "Step 6 of 6 · Almost done!", 0.83f)
            ),
            onColorSelected = {},
            onContinueTask = {},
            onAvatarClick = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildHomeNoActiveTasksPreview() {
    TinyStepsTheme {
        ChildHomeScreen(
            uiState = ChildHomeUiState(
                childName = "Sara",
                buddyAvatar = "🐰",
                stars = 45,
                notStartedCount = 0,
                inProgressCount = 0,
                completedCount = 4,
                currentTask = null
            ),
            onColorSelected = {},
            onContinueTask = {},
            onAvatarClick = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildHomeCompletedTasksPreview() {
    TinyStepsTheme {
        ChildHomeScreen(
            uiState = ChildHomeUiState(
                childName = "Omar",
                buddyAvatar = "🐻",
                stars = 30,
                notStartedCount = 2,
                inProgressCount = 0,
                completedCount = 1,
                currentTask = ChildTaskUi("t2", "Clean My Room", "🧹", 2, 4, "Step 2 of 4 · In progress", 0.5f)
            ),
            onColorSelected = {},
            onContinueTask = {},
            onAvatarClick = {},
            onTabSelected = {}
        )
    }
}
