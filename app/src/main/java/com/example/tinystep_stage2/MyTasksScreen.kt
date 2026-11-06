package com.example.tinystep_stage2

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinystep_stage2.ui.theme.ChildScreenBackground
import com.example.tinystep_stage2.ui.theme.OnPrimaryGreen
import com.example.tinystep_stage2.ui.theme.OnSecondaryAccent
import com.example.tinystep_stage2.ui.theme.PrimaryGreen
import com.example.tinystep_stage2.ui.theme.SecondaryAccent
import com.example.tinystep_stage2.ui.theme.TertiaryInProgress
import com.example.tinystep_stage2.ui.theme.TinyStepsTheme

data class ChildTask(
    val id: String = "",
    val title: String = "",
    val completedSteps: Int = 0,
    val totalSteps: Int = 0,
)

data class MyTasksUiState(
    val doingNow: ChildTask? = null,
    val toDo: List<ChildTask> = emptyList(),
    val done: List<ChildTask> = emptyList(),
)

class MyTasksViewModel : ViewModel() {
    var uiState by mutableStateOf(
        MyTasksUiState(
            doingNow = ChildTask(
                id = "1",
                title = "Get Ready for School",
                completedSteps = 5,
                totalSteps = 6,
            ),
            toDo = listOf(
                ChildTask(
                    id = "2",
                    title = "Homework Time",
                    completedSteps = 0,
                    totalSteps = 5,
                )
            ),
            done = listOf(
                ChildTask(
                    id = "3",
                    title = "Water the Plants",
                    completedSteps = 3,
                    totalSteps = 3,
                ),
                ChildTask(
                    id = "4",
                    title = "Set the Table",
                    completedSteps = 2,
                    totalSteps = 2,
                ),
            ),
        )
    )
        private set
}

@Composable
fun MyTasksRoute(
    modifier: Modifier = Modifier,
    viewModel: MyTasksViewModel = viewModel(),
    onOpenTask: (String) -> Unit = {},
    onGoHome: () -> Unit = {},
) {
    val uiState = viewModel.uiState

    MyTasksScreen(
        uiState = uiState,
        onContinueTask = onOpenTask,
        onStartTask = onOpenTask,
        onHomeClick = onGoHome,
        modifier = modifier,
    )
}

@Composable
fun MyTasksScreen(
    uiState: MyTasksUiState,
    onContinueTask: (String) -> Unit,
    onStartTask: (String) -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ChildScreenBackground,
        bottomBar = {
            ChildBottomBar(
                selectedTab = 1,
                onHomeClick = onHomeClick,
                onMyTasksClick = {},
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                Text(
                    text = "My Tasks",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            val isEmpty = uiState.doingNow == null && uiState.toDo.isEmpty() && uiState.done.isEmpty()
            if (isEmpty) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "No tasks yet!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "When your parent or teacher gives you a task, it will show up here.",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else {
                if (uiState.doingNow != null) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Doing now",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TertiaryInProgress,
                            )
                            DoingNowCard(
                                task = uiState.doingNow,
                                onContinue = { onContinueTask(uiState.doingNow.id) },
                            )
                        }
                    }
                }

                if (uiState.toDo.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "To do",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                    items(uiState.toDo.size) { index ->
                        val task = uiState.toDo[index]
                        ToDoCard(
                            task = task,
                            onStart = { onStartTask(task.id) },
                        )
                    }
                }

                if (uiState.done.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Done",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen,
                            )
                        }
                    }
                    items(uiState.done.size) { index ->
                        val task = uiState.done[index]
                        DoneRow(task = task)
                    }
                }
            }
        }
    }
}

@Composable
fun DoingNowCard(
    task: ChildTask,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color(0xFFEEE9FE),
        border = BorderStroke(2.dp, TertiaryInProgress),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Text(
                text = "${task.title} · ${task.completedSteps} of ${task.totalSteps} steps",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(16.dp))

            val progress = if (task.totalSteps > 0) task.completedSteps.toFloat() / task.totalSteps.toFloat() else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = PrimaryGreen,
                trackColor = Color(0xFFDCD5F6),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = OnPrimaryGreen,
                ),
            ) {
                Text(
                    text = "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun ToDoCard(
    task: ChildTask,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Text(
                text = "${task.title} · ${task.totalSteps} steps",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryAccent,
                    contentColor = OnSecondaryAccent,
                ),
            ) {
                Text(
                    text = "Start",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun DoneRow(
    task: ChildTask,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color(0xFFE3F5E8),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = PrimaryGreen.copy(alpha = 0.15f),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = task.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = SecondaryAccent,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

// Previews
@Preview(showBackground = true)
@Composable
private fun MyTasksScreenNormalPreview() {
    TinyStepsTheme {
        MyTasksScreen(
            uiState = MyTasksUiState(
                doingNow = ChildTask(
                    id = "1",
                    title = "Get Ready for School",
                    completedSteps = 5,
                    totalSteps = 6,
                ),
                toDo = listOf(
                    ChildTask(
                        id = "2",
                        title = "Homework Time",
                        totalSteps = 5,
                    )
                ),
                done = listOf(
                    ChildTask(id = "3", title = "Water the Plants", totalSteps = 3),
                    ChildTask(id = "4", title = "Set the Table", totalSteps = 2),
                ),
            ),
            onContinueTask = {},
            onStartTask = {},
            onHomeClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTasksScreenEmptyPreview() {
    TinyStepsTheme {
        MyTasksScreen(
            uiState = MyTasksUiState(
                doingNow = null,
                toDo = emptyList(),
                done = emptyList(),
            ),
            onContinueTask = {},
            onStartTask = {},
            onHomeClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DoingNowCardPreview() {
    TinyStepsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DoingNowCard(
                task = ChildTask(
                    id = "1",
                    title = "Get Ready for School",
                    completedSteps = 5,
                    totalSteps = 6,
                ),
                onContinue = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ToDoCardPreview() {
    TinyStepsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ToDoCard(
                task = ChildTask(
                    id = "2",
                    title = "Homework Time",
                    totalSteps = 5,
                ),
                onStart = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DoneRowPreview() {
    TinyStepsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DoneRow(
                task = ChildTask(
                    id = "3",
                    title = "Water the Plants",
                    totalSteps = 3,
                ),
            )
        }
    }
}
