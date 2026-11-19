package com.example.tinysteps_1.ui

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

data class StepByStepTaskUiState(
    val taskId: String = "t1",
    val childId: String = "1",
    val taskTitle: String = "Get Ready for School",
    val currentStepIndex: Int = 5,
    val steps: List<TaskStepUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class StepByStepTaskViewModel : ViewModel() {
    private val defaultSchoolSteps = listOf(
        TaskStepUi("s1", "Make your bed", stepNumber = 1, iconEmoji = "🛏️", isCompleted = true, hint = "Pull up sheets and arrange pillows"),
        TaskStepUi("s2", "Brush your teeth", stepNumber = 2, iconEmoji = "🪥", isCompleted = true, hint = "Use toothpaste and brush for 2 minutes"),
        TaskStepUi("s3", "Wash your face", stepNumber = 3, iconEmoji = "🧼", isCompleted = true, hint = "Use warm water and soft towel"),
        TaskStepUi("s4", "Put on your clothes", stepNumber = 4, iconEmoji = "👕", isCompleted = true, hint = "School uniform and blue socks"),
        TaskStepUi("s5", "Eat your breakfast", stepNumber = 5, iconEmoji = "🥣", isCompleted = true, hint = "Finish your cereal and milk"),
        TaskStepUi("s6", "Pack your school bag", stepNumber = 6, iconEmoji = "🎒", isCompleted = false, hint = "Books, pencil case and water bottle")
    )

    private val defaultRoomSteps = listOf(
        TaskStepUi("rs1", "Pick up your toys", stepNumber = 1, iconEmoji = "🧸", isCompleted = true, hint = "Put them in the storage box"),
        TaskStepUi("rs2", "Fold your clothes", stepNumber = 2, iconEmoji = "👔", isCompleted = false, hint = "Put them neatly in drawers"),
        TaskStepUi("rs3", "Make your bed", stepNumber = 3, iconEmoji = "🛏️", isCompleted = false, hint = "Tidy blankets and pillows"),
        TaskStepUi("rs4", "Sweep the floor", stepNumber = 4, iconEmoji = "🧹", isCompleted = false, hint = "Clear away any dust")
    )

    var uiState by mutableStateOf(StepByStepTaskUiState())
        private set

    fun initialize(taskId: String, childId: String) {
        val (title, stepsList) = when (taskId) {
            "t2" -> Pair("Clean My Room", defaultRoomSteps)
            else -> Pair("Get Ready for School", defaultSchoolSteps)
        }

        val firstIncompleteIndex = stepsList.indexOfFirst { !it.isCompleted }.let { if (it == -1) stepsList.size - 1 else it }

        uiState = StepByStepTaskUiState(
            taskId = taskId,
            childId = childId,
            taskTitle = title,
            currentStepIndex = firstIncompleteIndex,
            steps = stepsList
        )
    }

    fun completeCurrentStep(): Boolean {
        val currentIndex = uiState.currentStepIndex
        val updatedSteps = uiState.steps.mapIndexed { idx, step ->
            if (idx == currentIndex) step.copy(isCompleted = true) else step
        }

        return if (currentIndex < updatedSteps.size - 1) {
            uiState = uiState.copy(
                steps = updatedSteps,
                currentStepIndex = currentIndex + 1
            )
            false
        } else {
            uiState = uiState.copy(
                steps = updatedSteps
            )
            true
        }
    }
}

@Composable
fun StepByStepTaskRoute(
    taskId: String,
    childId: String,
    onTaskCompleted: (String, String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StepByStepTaskViewModel = viewModel()
) {
    LaunchedEffect(taskId, childId) {
        viewModel.initialize(taskId, childId)
    }

    StepByStepTaskScreen(
        uiState = viewModel.uiState,
        onDone = {
            val isFinished = viewModel.completeCurrentStep()
            if (isFinished) {
                onTaskCompleted(viewModel.uiState.taskId, viewModel.uiState.childId)
            }
        },
        onClose = onClose,
        modifier = modifier
    )
}

@Composable
fun StepByStepTaskScreen(
    uiState: StepByStepTaskUiState,
    onDone: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentStep = uiState.steps.getOrNull(uiState.currentStepIndex)
    val totalSteps = uiState.steps.size

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
                Spacer(modifier = Modifier.height(16.dp))

                // Header Bar: Circular X Close Button & Task Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.back_content_description),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = uiState.taskTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Segmented Progress Indicator Bar
                StepProgressIndicator(
                    currentStepIndex = uiState.currentStepIndex,
                    totalSteps = totalSteps
                )

                Spacer(modifier = Modifier.height(12.dp))

                // "Step X of Y" Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE8F5EE))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.step_progress_badge,
                            uiState.currentStepIndex + 1,
                            totalSteps
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Large Step Image Card
                if (currentStep != null) {
                    StepImageCard(step = currentStep)

                    Spacer(modifier = Modifier.height(28.dp))

                    // Step Instruction Title & Short Hint
                    StepInstruction(
                        title = currentStep.instruction,
                        hint = currentStep.hint
                    )
                }

                Spacer(modifier = Modifier.weight(1f, fill = false))
                Spacer(modifier = Modifier.height(36.dp))

                // Large Green Done Button
                Button(
                    onClick = onDone,
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
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.btn_done),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StepProgressIndicator(
    currentStepIndex: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalSteps) {
            val segmentColor = when {
                i < currentStepIndex -> MaterialTheme.colorScheme.primary // Green completed
                i == currentStepIndex -> Color(0xFFFFC94A)               // Yellow active
                else -> Color(0xFFCBD5E1)                                 // Grey upcoming
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(segmentColor)
            )
        }
    }
}

@Composable
fun StepImageCard(
    step: TaskStepUi,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FE)), // Soft pastel blue
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val emoji = step.iconEmoji ?: "🎒"
            if (emoji == "🎒") {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Main Backpack 🎒
                    Text(
                        text = "🎒",
                        fontSize = 110.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    // Books 📚 bottom-left
                    Text(
                        text = "📚",
                        fontSize = 44.sp,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 36.dp, bottom = 44.dp)
                    )
                    // Juice Box 🧃 top-right
                    Text(
                        text = "🧃",
                        fontSize = 44.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 40.dp, top = 36.dp)
                    )
                }
            } else {
                Text(
                    text = emoji,
                    fontSize = 120.sp
                )
            }
        }
    }
}

@Composable
fun StepInstruction(
    title: String,
    hint: String?,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        if (!hint.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StepByStepTaskFirstStepPreview() {
    val sampleSteps = listOf(
        TaskStepUi("s1", "Make your bed", stepNumber = 1, iconEmoji = "🛏️", isCompleted = false, hint = "Pull up sheets and arrange pillows"),
        TaskStepUi("s2", "Brush your teeth", stepNumber = 2, iconEmoji = "🪥", isCompleted = false, hint = "2 minutes with toothpaste"),
        TaskStepUi("s3", "Wash your face", stepNumber = 3, iconEmoji = "🧼", isCompleted = false, hint = "Use warm water")
    )
    TinyStepsTheme {
        StepByStepTaskScreen(
            uiState = StepByStepTaskUiState(
                currentStepIndex = 0,
                steps = sampleSteps
            ),
            onDone = {},
            onClose = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StepByStepTaskInProgressPreview() {
    val sampleSteps = listOf(
        TaskStepUi("s1", "Make your bed", stepNumber = 1, iconEmoji = "🛏️", isCompleted = true, hint = "Pull up sheets"),
        TaskStepUi("s2", "Brush your teeth", stepNumber = 2, iconEmoji = "🪥", isCompleted = true, hint = "2 minutes with toothpaste"),
        TaskStepUi("s3", "Wash your face", stepNumber = 3, iconEmoji = "🧼", isCompleted = false, hint = "Use warm water")
    )
    TinyStepsTheme {
        StepByStepTaskScreen(
            uiState = StepByStepTaskUiState(
                currentStepIndex = 2,
                steps = sampleSteps
            ),
            onDone = {},
            onClose = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StepByStepTaskFinalStepPreview() {
    val sampleSteps = listOf(
        TaskStepUi("s1", "Make your bed", stepNumber = 1, iconEmoji = "🛏️", isCompleted = true),
        TaskStepUi("s2", "Brush your teeth", stepNumber = 2, iconEmoji = "🪥", isCompleted = true),
        TaskStepUi("s3", "Wash your face", stepNumber = 3, iconEmoji = "🧼", isCompleted = true),
        TaskStepUi("s4", "Put on your clothes", stepNumber = 4, iconEmoji = "👕", isCompleted = true),
        TaskStepUi("s5", "Eat your breakfast", stepNumber = 5, iconEmoji = "🥣", isCompleted = true),
        TaskStepUi("s6", "Pack your school bag", stepNumber = 6, iconEmoji = "🎒", isCompleted = false, hint = "Books, pencil case and water bottle")
    )
    TinyStepsTheme {
        StepByStepTaskScreen(
            uiState = StepByStepTaskUiState(
                currentStepIndex = 5,
                steps = sampleSteps
            ),
            onDone = {},
            onClose = {}
        )
    }
}
