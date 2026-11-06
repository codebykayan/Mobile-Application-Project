package com.example.tinystep_stage2

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import com.example.tinystep_stage2.ui.theme.PrimaryGreen
import com.example.tinystep_stage2.ui.theme.SecondaryAccent
import com.example.tinystep_stage2.ui.theme.TinyStepsTheme

data class StepByStepTaskUiState(
    val childName: String = "",
    val taskTitle: String = "",
    val stepNumber: Int = 1,
    val totalSteps: Int = 1,
    val instruction: String = "",
    val detail: String = "",
    @param:DrawableRes val imageRes: Int? = null,
    val showFeedback: Boolean = false,
)

data class StepItem(
    val instruction: String,
    val detail: String,
)

class StepByStepTaskViewModel : ViewModel() {
    private val steps = listOf(
        StepItem("Make your bed", "Pull up your sheets and fluff your pillow"),
        StepItem("Brush your teeth", "Use your toothbrush and toothpaste"),
        StepItem("Wash your face", "Splash some water and wipe clean"),
        StepItem("Put on your uniform", "Shirt, pants and socks ready"),
        StepItem("Eat your breakfast", "Finish your healthy meal"),
        StepItem("Pack your school bag", "Books, pencil case and water bottle"),
    )

    var uiState by mutableStateOf(
        StepByStepTaskUiState(
            childName = "Sara",
            taskTitle = "Get Ready for School",
            stepNumber = 1,
            totalSteps = 6,
            instruction = steps[0].instruction,
            detail = steps[0].detail,
            showFeedback = false,
        )
    )
        private set

    fun completeStep() {
        if (uiState.stepNumber < uiState.totalSteps) {
            val nextStep = uiState.stepNumber + 1
            val index = nextStep - 1
            val step = steps[index]
            uiState = uiState.copy(
                stepNumber = nextStep,
                instruction = step.instruction,
                detail = step.detail,
            )
        } else {
            uiState = uiState.copy(showFeedback = true)
        }
    }
}

@Composable
fun StepByStepTaskRoute(
    taskId: String,
    modifier: Modifier = Modifier,
    viewModel: StepByStepTaskViewModel = viewModel(),
    onTaskFinished: () -> Unit = {},
    onClose: () -> Unit = {},
) {
    val uiState = viewModel.uiState
    val currentTaskId = taskId // Reference taskId to avoid unused parameter warning

    StepByStepTaskScreen(
        uiState = uiState.copy(taskTitle = if (currentTaskId.isNotBlank()) uiState.taskTitle else uiState.taskTitle),
        onDone = {
            viewModel.completeStep()
        },
        onFinishTask = onTaskFinished,
        onClose = onClose,
        modifier = modifier,
    )
}

@Composable
fun StepProgressBar(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        for (i in 1..total) {
            val color = when {
                i < current -> PrimaryGreen
                i == current -> SecondaryAccent
                else -> Color.LightGray.copy(alpha = 0.5f)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun FeedbackSheetContent(
    childName: String,
    onFinishTask: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = SecondaryAccent.copy(alpha = 0.2f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = SecondaryAccent,
                    modifier = Modifier.size(48.dp),
                )
            }
        }
        Text(
            text = "Great job, $childName!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "That was the last step!",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onFinishTask,
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
                text = "Finish task →",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepByStepTaskScreen(
    uiState: StepByStepTaskUiState,
    onDone: () -> Unit,
    onFinishTask: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ChildScreenBackground,
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
                // Top bar: Close button (X) and task title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        onClick = onClose,
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 2.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = uiState.taskTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Segmented progress bar
                StepProgressBar(
                    current = uiState.stepNumber,
                    total = uiState.totalSteps,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Step chip
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE3F5E8),
                ) {
                    Text(
                        text = "Step ${uiState.stepNumber} of ${uiState.totalSteps}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Large rounded image card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFEEE9FE),
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = uiState.instruction,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(120.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Instruction text
                Text(
                    text = uiState.instruction,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Detail text
                if (uiState.detail.isNotBlank()) {
                    Text(
                        text = uiState.detail,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // Bottom Done button (72dp high)
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = OnPrimaryGreen,
                ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Done",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        if (uiState.showFeedback) {
            ModalBottomSheet(
                onDismissRequest = onFinishTask,
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                FeedbackSheetContent(
                    childName = uiState.childName,
                    onFinishTask = onFinishTask,
                )
            }
        }
    }
}

// Previews
@Preview(showBackground = true)
@Composable
private fun StepByStepTaskScreenMiddlePreview() {
    TinyStepsTheme {
        StepByStepTaskScreen(
            uiState = StepByStepTaskUiState(
                childName = "Sara",
                taskTitle = "Get Ready for School",
                stepNumber = 3,
                totalSteps = 6,
                instruction = "Brush your teeth",
                detail = "Use your toothbrush and toothpaste",
                showFeedback = false,
            ),
            onDone = {},
            onFinishTask = {},
            onClose = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StepByStepTaskScreenLastPreview() {
    TinyStepsTheme {
        StepByStepTaskScreen(
            uiState = StepByStepTaskUiState(
                childName = "Sara",
                taskTitle = "Get Ready for School",
                stepNumber = 6,
                totalSteps = 6,
                instruction = "Pack your school bag",
                detail = "Books, pencil case and water bottle",
                showFeedback = false,
            ),
            onDone = {},
            onFinishTask = {},
            onClose = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedbackSheetContentPreview() {
    TinyStepsTheme {
        FeedbackSheetContent(
            childName = "Sara",
            onFinishTask = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StepProgressBarPreview() {
    TinyStepsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            StepProgressBar(
                current = 3,
                total = 6,
            )
        }
    }
}
