package com.example.tinystep_stage2

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.tinystep_stage2.ui.theme.TinyStepsTheme
import kotlinx.serialization.Serializable

@Serializable
object RoleSelection

@Serializable
object ParentAuth

@Serializable
object ChooseChild

@Serializable
object ChildPairing

@Serializable
data class AddEditChild(val childId: String? = null)

@Serializable
object ParentHome

@Serializable
object ParentProfile

@Serializable
data class CreateTask(val taskId: String? = null)

@Serializable
object ChildHome

@Serializable
object MyTasks

@Serializable
data class StepByStepTask(val taskId: String)

@Serializable
data class TaskCompleted(val taskId: String)

@Composable
fun TinyStepsNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RoleSelection,
        modifier = modifier,
    ) {
        composable<RoleSelection> {
            val context = LocalContext.current
            RoleSelectionRoute(
                onParentContinue = {
                    if (AuthManager.isUserSignedInOnLaunch(context)) {
                        navController.navigate(ParentHome)
                    } else {
                        navController.navigate(ParentAuth)
                    }
                },
                onChildContinue = {
                    navController.navigate(ChooseChild)
                },
            )
        }
        composable<ChildPairing> {
            ChildPairingRoute(
                onPairingSuccess = {
                    navController.navigate(ChildHome) {
                        popUpTo<ChildPairing> {
                            inclusive = true
                        }
                    }
                },
                onBack = {
                    navController.popBackStack()
                },
            )
        }
        composable<ParentAuth> {
            ParentAuthRoute(
                onAuthSuccess = {
                    navController.navigate(ParentHome) {
                        popUpTo<ParentAuth> {
                            inclusive = true
                        }
                    }
                },
                onBack = {
                    navController.popBackStack()
                },
            )
        }
        composable<ChooseChild> {
            ChooseChildRoute(
                onChildChosen = { _ ->
                    navController.navigate(ChildHome)
                },
                onAddChildClick = {
                    navController.navigate(ChildPairing)
                },
                onBack = {
                    navController.popBackStack()
                },
            )
        }
        composable<AddEditChild> { backStackEntry ->
            val route = backStackEntry.toRoute<AddEditChild>()
            AddEditChildRoute(
                childId = route.childId,
                onSaveSuccess = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                },
            )
        }
        composable<ParentHome> {
            ParentHomeRoute(
                onOpenTask = { taskId ->
                    navController.navigate(CreateTask(taskId = taskId))
                },
                onCreateTask = {
                    navController.navigate(CreateTask())
                },
                onManageChildren = {
                    navController.navigate(AddEditChild())
                },
                onSwitchRole = {
                    navController.navigate(ParentProfile)
                },
            )
        }
        composable<ParentProfile> {
            ParentProfileRoute(
                onSwitchAccount = {
                    navController.navigate(RoleSelection) {
                        popUpTo<RoleSelection> {
                            inclusive = true
                        }
                    }
                },
                onLogOut = {
                    navController.navigate(RoleSelection) {
                        popUpTo<RoleSelection> {
                            inclusive = true
                        }
                    }
                },
                onBack = {
                    navController.popBackStack()
                },
            )
        }
        composable<CreateTask> { backStackEntry ->
            val route = backStackEntry.toRoute<CreateTask>()
            CreateTaskRoute(
                taskId = route.taskId,
                onTaskAssigned = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                },
            )
        }
        composable<ChildHome> {
            ChildHomeRoute(
                onOpenTask = { taskId ->
                    navController.navigate(StepByStepTask(taskId))
                },
                onOpenMyTasks = {
                    navController.navigate(MyTasks)
                },
                onSwitchChild = {
                    navController.navigate(ChooseChild)
                },
            )
        }
        composable<MyTasks> {
            MyTasksRoute(
                onOpenTask = { taskId ->
                    navController.navigate(StepByStepTask(taskId))
                },
                onGoHome = {
                    navController.popBackStack(ChildHome, inclusive = false)
                },
            )
        }
        composable<StepByStepTask> { backStackEntry ->
            val route = backStackEntry.toRoute<StepByStepTask>()
            StepByStepTaskRoute(
                taskId = route.taskId,
                onTaskFinished = {
                    navController.navigate(TaskCompleted(route.taskId)) {
                        popUpTo<StepByStepTask> {
                            inclusive = true
                        }
                    }
                },
                onClose = {
                    navController.popBackStack()
                },
            )
        }
        composable<TaskCompleted> { backStackEntry ->
            val route = backStackEntry.toRoute<TaskCompleted>()
            TaskCompletedRoute(
                taskId = route.taskId,
                onBackToMyTasks = {
                    navController.navigate(MyTasks) {
                        popUpTo<ChildHome> {
                            inclusive = false
                        }
                    }
                },
                onGoHome = {
                    navController.popBackStack(ChildHome, inclusive = false)
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TinyStepsNavHostPreview() {
    TinyStepsTheme {
        TinyStepsNavHost()
    }
}
