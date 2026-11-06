package com.example.tinystep_stage2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinystep_stage2.ui.theme.OnPrimaryGreen
import com.example.tinystep_stage2.ui.theme.PrimaryGreen
import com.example.tinystep_stage2.ui.theme.SecondaryAccent
import com.example.tinystep_stage2.ui.theme.TinyStepsTheme

data class TaskCompletedUiState(
    val childName: String = "",
    val taskTitle: String = "",
    val totalSteps: Int = 0,
    val parentLabel: String = "",
)

class TaskCompletedViewModel : ViewModel() {
    var uiState by mutableStateOf(
        TaskCompletedUiState(
            childName = "Sara",
            taskTitle = "Get Ready for School",
            totalSteps = 6,
            parentLabel = "Mom",
        )
    )
        private set
}

@Composable
fun TaskCompletedRoute(
    taskId: String,
    modifier: Modifier = Modifier,
    viewModel: TaskCompletedViewModel = viewModel(),
    onBackToMyTasks: () -> Unit = {},
    onGoHome: () -> Unit = {},
) {
    val uiState = viewModel.uiState
    val currentTaskId = taskId

    TaskCompletedScreen(
        uiState = uiState.copy(taskTitle = if (currentTaskId.isNotBlank()) uiState.taskTitle else uiState.taskTitle),
        onBackToMyTasks = onBackToMyTasks,
        onGoHome = onGoHome,
        modifier = modifier,
    )
}

@Composable
fun CompletionBadge(
    percent: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .border(12.dp, SecondaryAccent, CircleShape)
        )
        Surface(
            modifier = Modifier.size(164.dp),
            shape = CircleShape,
            color = Color(0xFFEAF7EE),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = SecondaryAccent,
                    modifier = Modifier.size(56.dp),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$percent%",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen,
                )
            }
        }
    }
}

@Composable
fun TaskCompletedScreen(
    uiState: TaskCompletedUiState,
    onBackToMyTasks: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFDFF2E5))
            .padding(24.dp),
    ) {
        // Decorative stars
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = SecondaryAccent,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 8.dp),
        )
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = SecondaryAccent,
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 16.dp),
        )
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = SecondaryAccent,
            modifier = Modifier
                .size(18.dp)
                .align(Alignment.CenterStart)
                .padding(start = 12.dp, top = 40.dp),
        )
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = SecondaryAccent,
            modifier = Modifier
                .size(22.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp, bottom = 80.dp),
        )
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = SecondaryAccent,
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                CompletionBadge(percent = 100)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You did it, ${uiState.childName}!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "You finished all ${uiState.totalSteps} steps of",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = uiState.taskTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFC9DCCF),
                ) {
                    Text(
                        text = "${uiState.parentLabel} can see you finished!",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onBackToMyTasks,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        contentColor = OnPrimaryGreen,
                    ),
                ) {
                    Text(
                        text = "Back to My Tasks",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                OutlinedButton(
                    onClick = onGoHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = CircleShape,
                    border = BorderStroke(2.dp, PrimaryGreen),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryGreen,
                    ),
                ) {
                    Text(
                        text = "Go Home",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

// Previews
@Preview(showBackground = true)
@Composable
private fun TaskCompletedScreenPreview() {
    TinyStepsTheme {
        TaskCompletedScreen(
            uiState = TaskCompletedUiState(
                childName = "Sara",
                taskTitle = "Get Ready for School",
                totalSteps = 6,
                parentLabel = "Mom",
            ),
            onBackToMyTasks = {},
            onGoHome = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompletionBadgePreview() {
    TinyStepsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CompletionBadge(percent = 100)
        }
    }
}
