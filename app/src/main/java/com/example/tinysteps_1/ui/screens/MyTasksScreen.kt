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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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

data class MyTasksUiState(
    val childId: String = "1",
    val inProgressTasks: List<ChildTaskUi> = emptyList(),
    val notStartedTasks: List<ChildTaskUi> = emptyList(),
    val completedTasks: List<ChildTaskUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: ChildTab = ChildTab.MY_TASKS
)

class MyTasksViewModel : ViewModel() {
    private val saraInProgress = listOf(
        ChildTaskUi("t1", "Get Ready for School", "🎒", 5, 6, "5 of 6 steps", 0.83f)
    )
    private val saraNotStarted = listOf(
        ChildTaskUi("t3", "Homework Time", "✏️", 0, 5, "5 steps", 0.0f)
    )
    private val saraCompleted = listOf(
        ChildTaskUi("t5", "Water the Plants", "🌱", 3, 3, "3 of 3 steps", 1.0f),
        ChildTaskUi("t4", "Set the Table", "🍽️", 3, 3, "3 of 3 steps", 1.0f)
    )

    private val omarInProgress: List<ChildTaskUi> = emptyList()
    private val omarNotStarted = listOf(
        ChildTaskUi("t2", "Clean My Room", "🧹", 0, 4, "4 steps", 0.0f),
        ChildTaskUi("t3", "Homework Time", "✏️", 0, 5, "5 steps", 0.0f)
    )
    private val omarCompleted = listOf(
        ChildTaskUi("t4", "Set the Table", "🍽️", 3, 3, "3 of 3 steps", 1.0f)
    )

    var uiState by mutableStateOf(MyTasksUiState())
        private set

    fun initialize(childId: String) {
        if (childId == "2") {
            uiState = MyTasksUiState(
                childId = childId,
                inProgressTasks = omarInProgress,
                notStartedTasks = omarNotStarted,
                completedTasks = omarCompleted,
                selectedTab = ChildTab.MY_TASKS
            )
        } else {
            uiState = MyTasksUiState(
                childId = childId,
                inProgressTasks = saraInProgress,
                notStartedTasks = saraNotStarted,
                completedTasks = saraCompleted,
                selectedTab = ChildTab.MY_TASKS
            )
        }
    }

    fun startTask(taskId: String) {
        val target = uiState.notStartedTasks.find { it.id == taskId } ?: return
        val updatedNotStarted = uiState.notStartedTasks.filter { it.id != taskId }
        val updatedInProgress = uiState.inProgressTasks + target.copy(
            currentStep = 1,
            statusText = "1 of ${target.totalSteps} steps",
            progressRatio = 1.0f / target.totalSteps
        )
        uiState = uiState.copy(
            notStartedTasks = updatedNotStarted,
            inProgressTasks = updatedInProgress
        )
    }

    fun onTabSelected(tab: ChildTab) {
        uiState = uiState.copy(selectedTab = tab)
    }
}

@Composable
fun MyTasksRoute(
    childId: String,
    onOpenTask: (String) -> Unit,
    onTabSelected: (ChildTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyTasksViewModel = viewModel()
) {
    LaunchedEffect(childId) {
        viewModel.initialize(childId)
    }

    MyTasksScreen(
        uiState = viewModel.uiState,
        onStartTask = { taskId ->
            viewModel.startTask(taskId)
            onOpenTask(taskId)
        },
        onContinueTask = onOpenTask,
        onTabSelected = { tab ->
            viewModel.onTabSelected(tab)
            onTabSelected(tab)
        },
        modifier = modifier
    )
}

@Composable
fun MyTasksScreen(
    uiState: MyTasksUiState,
    onStartTask: (String) -> Unit,
    onContinueTask: (String) -> Unit,
    onTabSelected: (ChildTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val isEmpty = uiState.inProgressTasks.isEmpty() &&
            uiState.notStartedTasks.isEmpty() &&
            uiState.completedTasks.isEmpty()

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
            } else if (isEmpty) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.empty_child_tasks_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
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
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Heading: My Tasks
                        Text(
                            text = stringResource(id = R.string.my_tasks_title),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Section 1: Working on it (In Progress)
                    if (uiState.inProgressTasks.isNotEmpty()) {
                        item {
                            TaskSectionHeader(
                                title = stringResource(id = R.string.section_working_on_it),
                                dotColor = Color(0xFF9333EA) // Purple dot
                            )
                        }

                        items(uiState.inProgressTasks) { task ->
                            InProgressTaskCard(
                                task = task,
                                onContinue = { onContinueTask(task.id) }
                            )
                        }
                    }

                    // Section 2: Not started
                    if (uiState.notStartedTasks.isNotEmpty()) {
                        item {
                            TaskSectionHeader(
                                title = stringResource(id = R.string.section_not_started),
                                dotColor = Color(0xFF94A3B8) // Grey dot
                            )
                        }

                        items(uiState.notStartedTasks) { task ->
                            NotStartedTaskCard(
                                task = task,
                                onStart = { onStartTask(task.id) }
                            )
                        }
                    }

                    // Section 3: Completed
                    if (uiState.completedTasks.isNotEmpty()) {
                        item {
                            TaskSectionHeader(
                                title = stringResource(id = R.string.section_completed),
                                dotColor = MaterialTheme.colorScheme.primary // Green dot
                            )
                        }

                        items(uiState.completedTasks) { task ->
                            CompletedTaskCard(taskTitle = task.title)
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
fun TaskSectionHeader(
    title: String,
    dotColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun InProgressTaskCard(
    task: ChildTaskUi,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, Color(0xFFDDD6FE)), // Purple outline
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Task Icon Box
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = task.icon, fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.statusText,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Purple Progress Bar
            LinearProgressIndicator(
                progress = { task.progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color(0xFF9333EA), // Purple
                trackColor = Color(0xFFF3E8FF) // Soft purple track
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Green Continue Button
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.btn_continue_plain),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun NotStartedTaskCard(
    task: ChildTaskUi,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Task Icon Box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF3E8FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = task.icon, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Task Name & Step Count
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(id = R.string.steps_count_simple, task.totalSteps),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Yellow Start Button
            Button(
                onClick = onStart,
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC94A), // Yellow
                    contentColor = Color(0xFF1B2430)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.btn_start),
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
fun CompletedTaskCard(
    taskTitle: String,
    starsEarned: Int = 10,
    modifier: Modifier = Modifier
) {
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
            // Green Checkmark Circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Task Title
            Text(
                text = taskTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Star Badge (+10 ★)
            Text(
                text = stringResource(id = R.string.reward_stars_format, starsEarned),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFFD97706),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFEF3C7))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTasksDefaultPreview() {
    TinyStepsTheme {
        MyTasksScreen(
            uiState = MyTasksUiState(
                inProgressTasks = listOf(
                    ChildTaskUi("t1", "Get Ready for School", "🎒", 5, 6, "5 of 6 steps", 0.83f)
                ),
                notStartedTasks = listOf(
                    ChildTaskUi("t3", "Homework Time", "✏️", 0, 5, "5 steps", 0.0f)
                ),
                completedTasks = listOf(
                    ChildTaskUi("t5", "Water the Plants", "🌱", 3, 3, "3 of 3 steps", 1.0f),
                    ChildTaskUi("t4", "Set the Table", "🍽️", 3, 3, "3 of 3 steps", 1.0f)
                )
            ),
            onStartTask = {},
            onContinueTask = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTasksAllCompletedPreview() {
    TinyStepsTheme {
        MyTasksScreen(
            uiState = MyTasksUiState(
                inProgressTasks = emptyList(),
                notStartedTasks = emptyList(),
                completedTasks = listOf(
                    ChildTaskUi("t1", "Get Ready for School", "🎒", 6, 6, "6 of 6 steps", 1.0f),
                    ChildTaskUi("t5", "Water the Plants", "🌱", 3, 3, "3 of 3 steps", 1.0f),
                    ChildTaskUi("t4", "Set the Table", "🍽️", 3, 3, "3 of 3 steps", 1.0f)
                )
            ),
            onStartTask = {},
            onContinueTask = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTasksEmptyPreview() {
    TinyStepsTheme {
        MyTasksScreen(
            uiState = MyTasksUiState(
                inProgressTasks = emptyList(),
                notStartedTasks = emptyList(),
                completedTasks = emptyList()
            ),
            onStartTask = {},
            onContinueTask = {},
            onTabSelected = {}
        )
    }
}
