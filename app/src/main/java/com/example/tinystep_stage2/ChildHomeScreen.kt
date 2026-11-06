package com.example.tinystep_stage2

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinystep_stage2.ui.theme.ChildScreenBackground
import com.example.tinystep_stage2.ui.theme.OnPrimaryGreen
import com.example.tinystep_stage2.ui.theme.PrimaryGreen
import com.example.tinystep_stage2.ui.theme.SecondaryAccent
import com.example.tinystep_stage2.ui.theme.TertiaryInProgress
import com.example.tinystep_stage2.ui.theme.TinyStepsTheme

data class CurrentTask(
    val id: String = "",
    val title: String = "",
    val currentStep: Int = 0,
    val totalSteps: Int = 0,
)

data class ChildHomeUiState(
    val childName: String = "",
    val favoriteColor: Long = 0xFF25844F,
    val toDoCount: Int = 0,
    val doingCount: Int = 0,
    val doneCount: Int = 0,
    val currentTask: CurrentTask? = null,
)

class ChildHomeViewModel : ViewModel() {
    var uiState by mutableStateOf(
        ChildHomeUiState(
            childName = "Sara",
            favoriteColor = 0xFF25844F,
            toDoCount = 1,
            doingCount = 1,
            doneCount = 2,
            currentTask = CurrentTask(
                id = "1",
                title = "Get Ready for School",
                currentStep = 6,
                totalSteps = 6,
            ),
        )
    )
        private set
}

@Composable
fun ChildHomeRoute(
    viewModel: ChildHomeViewModel = viewModel(),
    onOpenTask: (String) -> Unit = {},
    onOpenMyTasks: () -> Unit = {},
) {
    val uiState = viewModel.uiState

    ChildHomeScreen(
        uiState = uiState,
        onContinueTask = onOpenTask,
        onColorClick = {},
        onMyTasksClick = onOpenMyTasks,
    )
}

@Composable
fun ChildHomeScreen(
    uiState: ChildHomeUiState,
    onContinueTask: (String) -> Unit,
    onColorClick: () -> Unit,
    onMyTasksClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ChildScreenBackground,
        bottomBar = {
            ChildBottomBar(
                selectedTab = 0,
                onHomeClick = {},
                onMyTasksClick = onMyTasksClick,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                // Header: Avatar and Greeting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = SecondaryAccent,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = uiState.childName.take(1).uppercase(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondary,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Hi ${uiState.childName}!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "Ready for today?",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // My color row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onColorClick),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "My color",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    // Color wheel button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                color = Color(uiState.favoriteColor)
                            )
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Tap to pick any color",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Three Counter Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CounterCard(
                        count = uiState.toDoCount,
                        label = "To do",
                        containerColor = Color(0xFFF0F2F5),
                        modifier = Modifier.weight(1f),
                    )
                    CounterCard(
                        count = uiState.doingCount,
                        label = "Doing",
                        containerColor = Color(0xFFEEE9FE),
                        modifier = Modifier.weight(1f),
                    )
                    CounterCard(
                        count = uiState.doneCount,
                        label = "Done",
                        containerColor = Color(0xFFE3F5E8),
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Heading "Keep going!"
                Text(
                    text = "Keep going!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Current task card or No task message
                if (uiState.currentTask != null) {
                    CurrentTaskCard(
                        task = uiState.currentTask,
                        onContinue = { onContinueTask(uiState.currentTask.id) },
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        color = Color(0xFFDFF2E5),
                        border = BorderStroke(1.dp, Color(0xFFB7E0C4)),
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "No task in progress",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CounterCard(
    count: Int,
    label: String,
    containerColor: Color,
    modifier: Modifier = Modifier,
) {
    val numberColor = when (label.lowercase()) {
        "doing" -> TertiaryInProgress
        "done" -> PrimaryGreen
        else -> MaterialTheme.colorScheme.onBackground
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = containerColor,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = count.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = numberColor,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun CurrentTaskCard(
    task: CurrentTask,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color(0xFFDFF2E5),
        border = BorderStroke(1.dp, Color(0xFFB7E0C4)),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = SecondaryAccent,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = task.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Step ${task.currentStep} of ${task.totalSteps} · Almost done!",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            val progress = if (task.totalSteps > 0) task.currentStep.toFloat() / task.totalSteps.toFloat() else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = SecondaryAccent,
                trackColor = Color(0xFFC8E6D1),
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
fun ChildBottomBar(
    selectedTab: Int,
    onHomeClick: () -> Unit,
    onMyTasksClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = onHomeClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                )
            },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0xFFDFF2E5),
            ),
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = onMyTasksClick,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = "My Tasks",
                )
            },
            label = { Text("My Tasks") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0xFFDFF2E5),
            ),
        )
    }
}

// Previews
@Preview(showBackground = true)
@Composable
private fun ChildHomeScreenWithTaskPreview() {
    TinyStepsTheme {
        ChildHomeScreen(
            uiState = ChildHomeUiState(
                childName = "Sara",
                favoriteColor = 0xFF25844F,
                toDoCount = 1,
                doingCount = 1,
                doneCount = 2,
                currentTask = CurrentTask(
                    id = "1",
                    title = "Get Ready for School",
                    currentStep = 6,
                    totalSteps = 6,
                ),
            ),
            onContinueTask = {},
            onColorClick = {},
            onMyTasksClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildHomeScreenWithoutTaskPreview() {
    TinyStepsTheme {
        ChildHomeScreen(
            uiState = ChildHomeUiState(
                childName = "Sara",
                favoriteColor = 0xFF25844F,
                toDoCount = 1,
                doingCount = 1,
                doneCount = 2,
                currentTask = null,
            ),
            onContinueTask = {},
            onColorClick = {},
            onMyTasksClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CounterCardPreview() {
    TinyStepsTheme {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CounterCard(
                count = 1,
                label = "To do",
                containerColor = Color(0xFFF0F2F5),
                modifier = Modifier.weight(1f),
            )
            CounterCard(
                count = 1,
                label = "Doing",
                containerColor = Color(0xFFEEE9FE),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentTaskCardPreview() {
    TinyStepsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CurrentTaskCard(
                task = CurrentTask(
                    id = "1",
                    title = "Get Ready for School",
                    currentStep = 6,
                    totalSteps = 6,
                ),
                onContinue = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildBottomBarPreview() {
    TinyStepsTheme {
        ChildBottomBar(
            selectedTab = 0,
            onHomeClick = {},
            onMyTasksClick = {},
        )
    }
}
