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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

enum class ReportPeriod {
    WEEK,
    MONTH
}

data class DailyTaskCountUi(
    val dayLabel: String,
    val count: Int,
    val maxCount: Int = 5
)

data class RecentTaskUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val isCompleted: Boolean = false,
    val rewardStars: Int? = null,
    val iconEmoji: String = "🎒"
)

data class ChildReportUiState(
    val childId: String = "",
    val name: String = "Sara",
    val age: Int = 8,
    val avatar: String = "🐰",
    val tasksCompleted: Int = 17,
    val starsEarned: Int = 45,
    val dayStreak: Int = 4,
    val selectedPeriod: ReportPeriod = ReportPeriod.WEEK,
    val periodLabel: String = "1–7 Oct",
    val chartData: List<DailyTaskCountUi> = emptyList(),
    val recentTasks: List<RecentTaskUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ChildReportViewModel : ViewModel() {
    private val weekChartDataSara = listOf(
        DailyTaskCountUi("S", 4, 5),
        DailyTaskCountUi("M", 5, 5),
        DailyTaskCountUi("T", 3, 5),
        DailyTaskCountUi("W", 5, 5),
        DailyTaskCountUi("T", 4, 5),
        DailyTaskCountUi("F", 1, 5),
        DailyTaskCountUi("S", 0, 5)
    )

    private val monthChartDataSara = listOf(
        DailyTaskCountUi("W1", 12, 15),
        DailyTaskCountUi("W2", 15, 15),
        DailyTaskCountUi("W3", 10, 15),
        DailyTaskCountUi("W4", 8, 15)
    )

    private val sampleRecentTasksSara = listOf(
        RecentTaskUi(
            id = "t1",
            title = "Get Ready for School",
            subtitle = "5 of 6 steps · today",
            isCompleted = false,
            iconEmoji = "🎒"
        ),
        RecentTaskUi(
            id = "t_plants",
            title = "Water the Plants",
            subtitle = "Yesterday",
            isCompleted = true,
            rewardStars = 10,
            iconEmoji = "🌱"
        )
    )

    var uiState by mutableStateOf(ChildReportUiState())
        private set

    fun initialize(childId: String) {
        val (name, age, avatar, tasks, stars, streak) = when (childId) {
            "2" -> Tuple6("Omar", 10, "🐻", 12, 30, 2)
            else -> Tuple6("Sara", 8, "🐰", 17, 45, 4)
        }

        uiState = ChildReportUiState(
            childId = childId,
            name = name,
            age = age,
            avatar = avatar,
            tasksCompleted = tasks,
            starsEarned = stars,
            dayStreak = streak,
            selectedPeriod = ReportPeriod.WEEK,
            periodLabel = "1–7 Oct",
            chartData = weekChartDataSara,
            recentTasks = sampleRecentTasksSara
        )
    }

    fun onPeriodSelected(period: ReportPeriod) {
        if (period == ReportPeriod.WEEK) {
            uiState = uiState.copy(
                selectedPeriod = ReportPeriod.WEEK,
                periodLabel = "1–7 Oct",
                chartData = weekChartDataSara
            )
        } else {
            uiState = uiState.copy(
                selectedPeriod = ReportPeriod.MONTH,
                periodLabel = "October 2026",
                chartData = monthChartDataSara
            )
        }
    }
}

private data class Tuple6<A, B, C, D, E, F>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F
)

@Composable
fun ChildReportRoute(
    childId: String,
    onTaskClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChildReportViewModel = viewModel()
) {
    LaunchedEffect(childId) {
        viewModel.initialize(childId)
    }

    ChildReportScreen(
        uiState = viewModel.uiState,
        onPeriodSelected = viewModel::onPeriodSelected,
        onTaskClick = onTaskClick,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun ChildReportScreen(
    uiState: ChildReportUiState,
    onPeriodSelected: (ReportPeriod) -> Unit,
    onTaskClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
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

                    // Top Bar: Back Arrow & Child Progress Title
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
                            text = stringResource(id = R.string.child_progress_title, uiState.name),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Profile Row: Avatar + Name/Age + Week/Month Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val avatarBgColor = if (uiState.avatar.contains("🐰") || uiState.name == "Sara") {
                                Color(0xFFFEF3C7) // Light yellow
                            } else {
                                Color(0xFFDBEAFE) // Light blue
                            }

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(avatarBgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = uiState.avatar, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = uiState.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${uiState.age} years old",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Period Selector Chips (Week / Month)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ReportPeriodChip(
                                label = stringResource(id = R.string.period_week),
                                selected = uiState.selectedPeriod == ReportPeriod.WEEK,
                                onClick = { onPeriodSelected(ReportPeriod.WEEK) }
                            )
                            ReportPeriodChip(
                                label = stringResource(id = R.string.period_month),
                                selected = uiState.selectedPeriod == ReportPeriod.MONTH,
                                onClick = { onPeriodSelected(ReportPeriod.MONTH) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Three Summary Cards Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReportSummaryCard(
                            valueText = uiState.tasksCompleted.toString(),
                            label = stringResource(id = R.string.label_tasks_done),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        ReportSummaryCard(
                            valueText = uiState.starsEarned.toString(),
                            label = stringResource(id = R.string.label_stars_earned),
                            iconEmoji = "⭐",
                            color = Color(0xFFD97706),
                            modifier = Modifier.weight(1f)
                        )
                        ReportSummaryCard(
                            valueText = uiState.dayStreak.toString(),
                            label = stringResource(id = R.string.label_day_streak),
                            iconEmoji = "🔥",
                            color = Color(0xFFEA580C),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Tasks per Day Bar Chart Card
                    TasksPerDayChart(
                        periodLabel = uiState.periodLabel,
                        chartData = uiState.chartData
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Recent Tasks Section Title
                    Text(
                        text = stringResource(id = R.string.section_recent_tasks),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Recent Tasks Cards
                if (uiState.recentTasks.isEmpty()) {
                    item {
                        Text(
                            text = "No recent tasks available.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(uiState.recentTasks) { task ->
                        RecentTaskCard(
                            task = task,
                            onClick = { onTaskClick(task.id) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun ReportSummaryCard(
    valueText: String,
    label: String,
    iconEmoji: String? = null,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = color
                )
                if (iconEmoji != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = iconEmoji, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ReportPeriodChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Color(0xFFE8F5EE) else Color.White)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TasksPerDayChart(
    periodLabel: String,
    chartData: List<DailyTaskCountUi>,
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
                .padding(20.dp)
        ) {
            // Chart Header: Tasks per day & Date range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.chart_title_tasks_per_day),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = periodLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bar Chart Container
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                chartData.forEach { item ->
                    val progressRatio = if (item.maxCount > 0) {
                        (item.count.toFloat() / item.maxCount).coerceIn(0f, 1f)
                    } else 0f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Vertical Bar Container
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height(125.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            if (progressRatio > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(progressRatio)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Day Label below Bar
                        Text(
                            text = item.dayLabel,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentTaskCard(
    task: RecentTaskUi,
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Task Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (task.isCompleted) Color(0xFFF1FAF4) else Color(0xFFFEF2F2)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = task.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title & Subtitle
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = task.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Badge ("In progress" or "+10 ★")
            if (!task.isCompleted) {
                Text(
                    text = stringResource(id = R.string.status_in_progress),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEDE9FE))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            } else if (task.rewardStars != null) {
                Text(
                    text = stringResource(id = R.string.reward_stars_format, task.rewardStars),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFFD97706),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildReportWeekPreview() {
    TinyStepsTheme {
        ChildReportScreen(
            uiState = ChildReportUiState(
                name = "Sara",
                age = 8,
                avatar = "🐰",
                tasksCompleted = 17,
                starsEarned = 45,
                dayStreak = 4,
                selectedPeriod = ReportPeriod.WEEK,
                periodLabel = "1–7 Oct",
                chartData = listOf(
                    DailyTaskCountUi("S", 4),
                    DailyTaskCountUi("M", 5),
                    DailyTaskCountUi("T", 3),
                    DailyTaskCountUi("W", 5),
                    DailyTaskCountUi("T", 4),
                    DailyTaskCountUi("F", 1),
                    DailyTaskCountUi("S", 0)
                ),
                recentTasks = listOf(
                    RecentTaskUi("t1", "Get Ready for School", "5 of 6 steps · today", false, iconEmoji = "🎒"),
                    RecentTaskUi("t2", "Water the Plants", "Yesterday", true, 10, iconEmoji = "🌱")
                )
            ),
            onPeriodSelected = {},
            onTaskClick = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildReportMonthPreview() {
    TinyStepsTheme {
        ChildReportScreen(
            uiState = ChildReportUiState(
                name = "Sara",
                age = 8,
                avatar = "🐰",
                tasksCompleted = 48,
                starsEarned = 180,
                dayStreak = 12,
                selectedPeriod = ReportPeriod.MONTH,
                periodLabel = "October 2026",
                chartData = listOf(
                    DailyTaskCountUi("W1", 12, 15),
                    DailyTaskCountUi("W2", 15, 15),
                    DailyTaskCountUi("W3", 10, 15),
                    DailyTaskCountUi("W4", 8, 15)
                )
            ),
            onPeriodSelected = {},
            onTaskClick = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildReportEmptyPreview() {
    TinyStepsTheme {
        ChildReportScreen(
            uiState = ChildReportUiState(
                name = "Omar",
                age = 10,
                avatar = "🐻",
                tasksCompleted = 0,
                starsEarned = 0,
                dayStreak = 0,
                chartData = emptyList(),
                recentTasks = emptyList()
            ),
            onPeriodSelected = {},
            onTaskClick = {},
            onBack = {}
        )
    }
}
