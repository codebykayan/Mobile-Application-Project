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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

enum class TaskStatus {
    ALL,
    ACTIVE,
    NOT_STARTED,
    COMPLETED
}

enum class ParentTab {
    HOME,
    CHILDREN,
    REWARDS,
    SETTINGS
}

data class ChildUi(
    val id: String,
    val name: String,
    val avatarEmoji: String
)

data class ParentTaskUi(
    val id: String,
    val title: String,
    val child: ChildUi,
    val status: TaskStatus,
    val completedSteps: Int,
    val totalSteps: Int
) {
    val progress: Float
        get() = if (totalSteps > 0) completedSteps.toFloat() / totalSteps else 0f
}

data class ParentHomeUiState(
    val greetingName: String = "Huda",
    val activeTasksCount: Int = 3,
    val completedTasksCount: Int = 1,
    val children: List<ChildUi> = emptyList(),
    val selectedChildId: String? = null,
    val selectedStatus: TaskStatus = TaskStatus.ACTIVE,
    val tasks: List<ParentTaskUi> = emptyList(),
    val selectedTab: ParentTab = ParentTab.HOME,
    val unreadNotificationCount: Int = 3
)

class ParentHomeViewModel : ViewModel() {
    private val allTasks = listOf(
        ParentTaskUi(
            id = "t1",
            title = "Get Ready for School",
            child = ChildUi("1", "Sara", "🐰"),
            status = TaskStatus.ACTIVE,
            completedSteps = 5,
            totalSteps = 6
        ),
        ParentTaskUi(
            id = "t2",
            title = "Clean My Room",
            child = ChildUi("2", "Omar", "🐻"),
            status = TaskStatus.ACTIVE,
            completedSteps = 2,
            totalSteps = 4
        ),
        ParentTaskUi(
            id = "t3",
            title = "Homework Time",
            child = ChildUi("1", "Sara", "🐰"),
            status = TaskStatus.NOT_STARTED,
            completedSteps = 0,
            totalSteps = 5
        ),
        ParentTaskUi(
            id = "t4",
            title = "Feed the Cat",
            child = ChildUi("2", "Omar", "🐻"),
            status = TaskStatus.COMPLETED,
            completedSteps = 3,
            totalSteps = 3
        )
    )

    private val childrenList = listOf(
        ChildUi("1", "Sara", "🐰"),
        ChildUi("2", "Omar", "🐻")
    )

    var uiState by mutableStateOf(
        ParentHomeUiState(
            greetingName = "Huda",
            activeTasksCount = 3,
            completedTasksCount = 1,
            children = childrenList,
            selectedChildId = null,
            selectedStatus = TaskStatus.ACTIVE,
            tasks = filterTasks(allTasks, null, TaskStatus.ACTIVE)
        )
    )
        private set

    fun onChildFilterSelected(childId: String?) {
        uiState = uiState.copy(
            selectedChildId = childId,
            tasks = filterTasks(allTasks, childId, uiState.selectedStatus)
        )
    }

    fun onStatusFilterSelected(status: TaskStatus) {
        uiState = uiState.copy(
            selectedStatus = status,
            tasks = filterTasks(allTasks, uiState.selectedChildId, status)
        )
    }

    fun onTabSelected(tab: ParentTab) {
        uiState = uiState.copy(selectedTab = tab)
    }

    private fun filterTasks(
        tasks: List<ParentTaskUi>,
        childId: String?,
        status: TaskStatus
    ): List<ParentTaskUi> {
        return tasks.filter { task ->
            val childMatches = childId == null || task.child.id == childId
            val statusMatches = when (status) {
                TaskStatus.ALL -> true
                TaskStatus.ACTIVE -> task.status == TaskStatus.ACTIVE || task.status == TaskStatus.NOT_STARTED
                TaskStatus.NOT_STARTED -> task.status == TaskStatus.NOT_STARTED
                TaskStatus.COMPLETED -> task.status == TaskStatus.COMPLETED
            }
            childMatches && statusMatches
        }
    }
}

@Composable
fun ParentHomeRoute(
    onTaskClick: (String) -> Unit,
    onNewTask: () -> Unit,
    onNotifications: () -> Unit,
    onTabSelected: (ParentTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentHomeViewModel = viewModel()
) {
    ParentHomeScreen(
        uiState = viewModel.uiState,
        onChildFilterSelected = viewModel::onChildFilterSelected,
        onStatusFilterSelected = viewModel::onStatusFilterSelected,
        onTaskClick = onTaskClick,
        onNewTask = onNewTask,
        onNotifications = onNotifications,
        onTabSelected = { tab ->
            viewModel.onTabSelected(tab)
            onTabSelected(tab)
        },
        modifier = modifier
    )
}

@Composable
fun ParentHomeScreen(
    uiState: ParentHomeUiState,
    onChildFilterSelected: (String?) -> Unit,
    onStatusFilterSelected: (TaskStatus) -> Unit,
    onTaskClick: (String) -> Unit,
    onNewTask: () -> Unit,
    onNotifications: () -> Unit,
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Top Bar: Greeting & Notifications Bell
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(id = R.string.greeting_huda),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(id = R.string.home_heading),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Notification Bell with Badge
                        IconButton(
                            onClick = onNotifications,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                        ) {
                            BadgedBox(
                                badge = {
                                    if (uiState.unreadNotificationCount > 0) {
                                        Badge(
                                            containerColor = Color(0xFFFF5252),
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = uiState.unreadNotificationCount.toString(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = stringResource(id = R.string.notifications_content_desc),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Summary Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SummaryCard(
                            count = uiState.activeTasksCount,
                            label = stringResource(id = R.string.active_tasks_label),
                            icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            count = uiState.completedTasksCount,
                            label = stringResource(id = R.string.completed_label),
                            icon = Icons.Outlined.Check,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Child Filter Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.filter_child_label),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(60.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                ChildFilterChip(
                                    name = stringResource(id = R.string.filter_all),
                                    avatar = null,
                                    selected = uiState.selectedChildId == null,
                                    onClick = { onChildFilterSelected(null) }
                                )
                            }
                            items(uiState.children) { child ->
                                ChildFilterChip(
                                    name = child.name,
                                    avatar = child.avatarEmoji,
                                    selected = uiState.selectedChildId == child.id,
                                    onClick = { onChildFilterSelected(child.id) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Status Filter Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.filter_status_label),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(60.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                StatusFilterChip(
                                    label = stringResource(id = R.string.status_active),
                                    selected = uiState.selectedStatus == TaskStatus.ACTIVE,
                                    onClick = { onStatusFilterSelected(TaskStatus.ACTIVE) }
                                )
                            }
                            item {
                                StatusFilterChip(
                                    label = stringResource(id = R.string.status_not_started),
                                    selected = uiState.selectedStatus == TaskStatus.NOT_STARTED,
                                    onClick = { onStatusFilterSelected(TaskStatus.NOT_STARTED) }
                                )
                            }
                            item {
                                StatusFilterChip(
                                    label = stringResource(id = R.string.status_completed),
                                    selected = uiState.selectedStatus == TaskStatus.COMPLETED,
                                    onClick = { onStatusFilterSelected(TaskStatus.COMPLETED) }
                                )
                            }
                            item {
                                StatusFilterChip(
                                    label = stringResource(id = R.string.filter_all),
                                    selected = uiState.selectedStatus == TaskStatus.ALL,
                                    onClick = { onStatusFilterSelected(TaskStatus.ALL) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Task List Items
                if (uiState.tasks.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.empty_tasks_title),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(id = R.string.empty_tasks_subtitle),
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(uiState.tasks) { task ->
                        ParentTaskCard(
                            task = task,
                            onClick = { onTaskClick(task.id) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }

            // Green Floating "+ New Task" Button
            Button(
                onClick = onNewTask,
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
                    text = stringResource(id = R.string.btn_new_task),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun SummaryCard(
    count: Int,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ChildFilterChip(
    name: String,
    avatar: String?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (avatar != null) {
                    Text(text = avatar, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                )
            }
        },
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
        modifier = modifier.heightIn(min = 40.dp)
    )
}

@Composable
fun StatusFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                )
            )
        },
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
        modifier = modifier.heightIn(min = 40.dp)
    )
}

@Composable
fun ParentTaskCard(
    task: ParentTaskUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = task.status == TaskStatus.COMPLETED
    val progressColor = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Task Title & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Status Badge
                val (badgeBg, badgeText, badgeColor, badgeIcon) = when {
                    isCompleted -> Quadruple(
                        Color(0xFFE8F5EE),
                        stringResource(id = R.string.status_completed),
                        MaterialTheme.colorScheme.primary,
                        Icons.Outlined.Check
                    )
                    task.completedSteps > 0 -> Quadruple(
                        Color(0xFFEDE9FE),
                        stringResource(id = R.string.status_in_progress),
                        MaterialTheme.colorScheme.tertiary,
                        Icons.Outlined.Schedule
                    )
                    else -> Quadruple(
                        Color(0xFFF1F5F9),
                        stringResource(id = R.string.status_not_started),
                        Color(0xFF475569),
                        null
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(badgeBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (badgeIcon != null) {
                        Icon(
                            imageVector = badgeIcon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Child Avatar & Name + Steps Ratio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.child.avatarEmoji,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1FAF4))
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = task.child.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "${stringResource(id = R.string.steps_label)} ${task.completedSteps}/${task.totalSteps}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { task.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = Color(0xFFF1F5F9)
            )
        }
    }
}

@Composable
fun ParentBottomBar(
    selectedTab: ParentTab,
    onTabSelected: (ParentTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        NavigationBar(
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = selectedTab == ParentTab.HOME,
                onClick = { onTabSelected(ParentTab.HOME) },
                icon = {
                    Icon(
                        imageVector = if (selectedTab == ParentTab.HOME) Icons.Default.Home else Icons.Outlined.Home,
                        contentDescription = stringResource(id = R.string.tab_home)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.tab_home),
                        fontWeight = if (selectedTab == ParentTab.HOME) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = Color(0xFFE8F5EE),
                    unselectedIconColor = Color(0xFF94A3B8),
                    unselectedTextColor = Color(0xFF94A3B8)
                )
            )

            NavigationBarItem(
                selected = selectedTab == ParentTab.CHILDREN,
                onClick = { onTabSelected(ParentTab.CHILDREN) },
                icon = {
                    Icon(
                        imageVector = if (selectedTab == ParentTab.CHILDREN) Icons.Default.People else Icons.Outlined.People,
                        contentDescription = stringResource(id = R.string.tab_children)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.tab_children),
                        fontWeight = if (selectedTab == ParentTab.CHILDREN) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = Color(0xFFE8F5EE),
                    unselectedIconColor = Color(0xFF94A3B8),
                    unselectedTextColor = Color(0xFF94A3B8)
                )
            )

            NavigationBarItem(
                selected = selectedTab == ParentTab.REWARDS,
                onClick = { onTabSelected(ParentTab.REWARDS) },
                icon = {
                    Icon(
                        imageVector = if (selectedTab == ParentTab.REWARDS) Icons.Default.Redeem else Icons.Outlined.Redeem,
                        contentDescription = stringResource(id = R.string.tab_rewards)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.tab_rewards),
                        fontWeight = if (selectedTab == ParentTab.REWARDS) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = Color(0xFFE8F5EE),
                    unselectedIconColor = Color(0xFF94A3B8),
                    unselectedTextColor = Color(0xFF94A3B8)
                )
            )

            NavigationBarItem(
                selected = selectedTab == ParentTab.SETTINGS,
                onClick = { onTabSelected(ParentTab.SETTINGS) },
                icon = {
                    Icon(
                        imageVector = if (selectedTab == ParentTab.SETTINGS) Icons.Default.Settings else Icons.Outlined.Settings,
                        contentDescription = stringResource(id = R.string.tab_settings)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.tab_settings),
                        fontWeight = if (selectedTab == ParentTab.SETTINGS) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = Color(0xFFE8F5EE),
                    unselectedIconColor = Color(0xFF94A3B8),
                    unselectedTextColor = Color(0xFF94A3B8)
                )
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Preview(showBackground = true)
@Composable
private fun ParentHomeDefaultPreview() {
    TinyStepsTheme {
        ParentHomeScreen(
            uiState = ParentHomeUiState(
                greetingName = "Huda",
                activeTasksCount = 3,
                completedTasksCount = 1,
                children = listOf(ChildUi("1", "Sara", "🐰"), ChildUi("2", "Omar", "🐻")),
                selectedChildId = null,
                selectedStatus = TaskStatus.ACTIVE,
                tasks = listOf(
                    ParentTaskUi("1", "Get Ready for School", ChildUi("1", "Sara", "🐰"), TaskStatus.ACTIVE, 5, 6),
                    ParentTaskUi("2", "Clean My Room", ChildUi("2", "Omar", "🐻"), TaskStatus.ACTIVE, 2, 4),
                    ParentTaskUi("3", "Homework Time", ChildUi("1", "Sara", "🐰"), TaskStatus.NOT_STARTED, 0, 5)
                )
            ),
            onChildFilterSelected = {},
            onStatusFilterSelected = {},
            onTaskClick = {},
            onNewTask = {},
            onNotifications = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ParentHomeFilteredPreview() {
    TinyStepsTheme {
        ParentHomeScreen(
            uiState = ParentHomeUiState(
                greetingName = "Huda",
                activeTasksCount = 3,
                completedTasksCount = 1,
                children = listOf(ChildUi("1", "Sara", "🐰"), ChildUi("2", "Omar", "🐻")),
                selectedChildId = "1",
                selectedStatus = TaskStatus.ACTIVE,
                tasks = listOf(
                    ParentTaskUi("1", "Get Ready for School", ChildUi("1", "Sara", "🐰"), TaskStatus.ACTIVE, 5, 6),
                    ParentTaskUi("3", "Homework Time", ChildUi("1", "Sara", "🐰"), TaskStatus.NOT_STARTED, 0, 5)
                )
            ),
            onChildFilterSelected = {},
            onStatusFilterSelected = {},
            onTaskClick = {},
            onNewTask = {},
            onNotifications = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ParentHomeEmptyPreview() {
    TinyStepsTheme {
        ParentHomeScreen(
            uiState = ParentHomeUiState(
                greetingName = "Huda",
                activeTasksCount = 3,
                completedTasksCount = 1,
                children = listOf(ChildUi("1", "Sara", "🐰"), ChildUi("2", "Omar", "🐻")),
                selectedChildId = "2",
                selectedStatus = TaskStatus.COMPLETED,
                tasks = emptyList()
            ),
            onChildFilterSelected = {},
            onStatusFilterSelected = {},
            onTaskClick = {},
            onNewTask = {},
            onNotifications = {},
            onTabSelected = {}
        )
    }
}
