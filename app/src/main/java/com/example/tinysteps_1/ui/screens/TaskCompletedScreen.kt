package com.example.tinysteps_1.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

data class TaskCompletedUiState(
    val taskId: String = "t1",
    val childId: String = "1",
    val childName: String = "Sara",
    val taskTitle: String = "Get Ready for School",
    val totalSteps: Int = 6,
    val earnedStars: Int = 10,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TaskCompletedViewModel : ViewModel() {
    var uiState by mutableStateOf(TaskCompletedUiState())
        private set

    fun initialize(taskId: String, childId: String) {
        val (name, title, stepsCount) = when (childId) {
            "2" -> Triple("Omar", if (taskId == "t2") "Clean My Room" else "Get Ready for School", if (taskId == "t2") 4 else 6)
            else -> Triple("Sara", if (taskId == "t2") "Clean My Room" else "Get Ready for School", if (taskId == "t2") 4 else 6)
        }

        uiState = TaskCompletedUiState(
            taskId = taskId,
            childId = childId,
            childName = name,
            taskTitle = title,
            totalSteps = stepsCount,
            earnedStars = 10,
            isLoading = false
        )
    }
}

@Composable
fun TaskCompletedRoute(
    taskId: String,
    childId: String,
    onBackToMyTasks: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskCompletedViewModel = viewModel()
) {
    LaunchedEffect(taskId, childId) {
        viewModel.initialize(taskId, childId)
    }

    TaskCompletedScreen(
        uiState = viewModel.uiState,
        onBackToMyTasks = onBackToMyTasks,
        onGoHome = onGoHome,
        modifier = modifier
    )
}

@Composable
fun TaskCompletedScreen(
    uiState: TaskCompletedUiState,
    onBackToMyTasks: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1FAF4)) // Soft mint background
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
                Spacer(modifier = Modifier.height(36.dp))

                // Top Completion Progress Circle (100% Star)
                CompletionProgressCircle()

                Spacer(modifier = Modifier.height(32.dp))

                // Heading & Completed Task Subtitle
                CompletionMessage(
                    childName = uiState.childName,
                    taskTitle = uiState.taskTitle,
                    totalSteps = uiState.totalSteps
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stars Earned Badge (+10 stars)
                StarsEarnedBadge(earnedStars = uiState.earnedStars)

                Spacer(modifier = Modifier.height(16.dp))

                // Parent Notification Note ("Mom can see you finished!")
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE8F5EE))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(id = R.string.mom_can_see_finished),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.weight(1f, fill = false))
                Spacer(modifier = Modifier.height(36.dp))

                // Bottom Buttons Column: "Back to My Tasks" & "Go Home"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Button 1: Back to My Tasks
                    Button(
                        onClick = onBackToMyTasks,
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
                        Text(
                            text = stringResource(id = R.string.btn_back_to_my_tasks),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Button 2: Go Home
                    OutlinedButton(
                        onClick = onGoHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.btn_go_home),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CompletionProgressCircle(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Decorative floating sparkles
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFEAB308),
            modifier = Modifier
                .size(24.dp)
                .offset(x = (-95).dp, y = (-50).dp)
        )
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFEAB308),
            modifier = Modifier
                .size(20.dp)
                .offset(x = 90.dp, y = (-70).dp)
        )
        Box(
            modifier = Modifier
                .size(10.dp)
                .offset(x = (-98).dp, y = 40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        // Main Large White Circle with Thick Yellow Border
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(6.dp, Color(0xFFFFC94A), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFEAB308),
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "100%",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CompletionMessage(
    childName: String,
    taskTitle: String,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // "You did it, Sara!"
        Text(
            text = stringResource(id = R.string.you_did_it_format, childName),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // "You finished all 6 steps of"
        Text(
            text = stringResource(id = R.string.finished_steps_format, totalSteps),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        // "Get Ready for School"
        Text(
            text = taskTitle,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StarsEarnedBadge(
    earnedStars: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFFEF3C7))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFEAB308),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.plus_stars_format, earnedStars),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color(0xFFD97706)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskCompletedDefaultPreview() {
    TinyStepsTheme {
        TaskCompletedScreen(
            uiState = TaskCompletedUiState(
                childName = "Sara",
                taskTitle = "Get Ready for School",
                totalSteps = 6,
                earnedStars = 10
            ),
            onBackToMyTasks = {},
            onGoHome = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskCompletedOmarPreview() {
    TinyStepsTheme {
        TaskCompletedScreen(
            uiState = TaskCompletedUiState(
                childId = "2",
                childName = "Omar",
                taskTitle = "Clean My Room",
                totalSteps = 4,
                earnedStars = 10
            ),
            onBackToMyTasks = {},
            onGoHome = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskCompletedLoadingPreview() {
    TinyStepsTheme {
        TaskCompletedScreen(
            uiState = TaskCompletedUiState(isLoading = true),
            onBackToMyTasks = {},
            onGoHome = {}
        )
    }
}
