package com.example.tinysteps_1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes {
    const val SPLASH = "splash"
    const val ROLE_SELECTION = "role_selection"
    const val PARENT_SIGNIN = "parent_signin"
    const val CHILD_ENTERCODE = "child_entercode"
    const val DEMO_MODE = "demo_mode"
    const val CREATE_ACCOUNT = "create_account"
    const val PARENT_HOME = "parent_home"
    const val TASK_DETAILS = "task_details/{taskId}"
    const val CREATE_TASK = "create_task"
    const val EDIT_TASK = "edit_task/{taskId}"
    const val ADD_EDIT_STEP = "add_edit_step?taskId={taskId}&stepId={stepId}"
    const val NOTIFICATIONS = "notifications"
    const val CHILDREN_LIST = "children_list"
    const val CHILD_REPORT = "child_report/{childId}"
    const val ADD_EDIT_CHILD = "add_edit_child?childId={childId}"
    const val LINK_DEVICE = "link_device?childId={childId}"
    const val REWARDS_LIST = "rewards_list"
    const val ADD_EDIT_REWARD = "add_edit_reward?rewardId={rewardId}"
    const val REWARD_REQUESTS = "reward_requests"
    const val SETTINGS_LIST = "settings_list"
    const val PROFILE = "profile"
    const val CHANGE_PASSWORD = "change_password"
    const val HELP_SUPPORT = "help_support"
    const val CHOOSE_CHILD = "choose_child"
    const val CHOOSE_BUDDY = "choose_buddy?childId={childId}"
    const val CHILD_HOME = "child_home?childId={childId}"
    const val STEP_BY_STEP_TASK = "step_by_step_task/{taskId}?childId={childId}"
    const val TASK_COMPLETED = "task_completed?taskId={taskId}&childId={childId}"
    const val CHILD_TASKS = "child_tasks?childId={childId}"
    const val CHILD_REWARDS = "child_rewards?childId={childId}"
    const val MY_REWARDS = "my_rewards?childId={childId}"
    const val CHILD_PROFILE = "child_profile?childId={childId}"
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier
    ) {
        composable(Routes.SPLASH) {
            SplashRoute(
                onTimeout = {
                    navController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(Routes.SPLASH) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Routes.ROLE_SELECTION) {
            RoleSelectionRoute(
                onParentContinue = {
                    navController.navigate(Routes.PARENT_SIGNIN)
                },
                onChildContinue = {
                    navController.navigate(Routes.CHILD_ENTERCODE)
                },
                onTryDemo = {
                    navController.navigate(Routes.DEMO_MODE)
                }
            )
        }
        composable(Routes.PARENT_SIGNIN) {
            SignInRoute(
                onSignInSuccess = {
                    navController.navigate(Routes.PARENT_HOME) {
                        popUpTo(Routes.ROLE_SELECTION) {
                            inclusive = true
                        }
                    }
                },
                onCreateAccount = {
                    navController.navigate(Routes.CREATE_ACCOUNT)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.CREATE_ACCOUNT) {
            CreateAccountRoute(
                onAccountCreated = {
                    navController.navigate(Routes.PARENT_HOME) {
                        popUpTo(Routes.ROLE_SELECTION) {
                            inclusive = true
                        }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.PARENT_HOME) {
            ParentHomeRoute(
                onTaskClick = { taskId ->
                    navController.navigate("task_details/$taskId")
                },
                onNewTask = {
                    navController.navigate(Routes.CREATE_TASK)
                },
                onNotifications = {
                    navController.navigate(Routes.NOTIFICATIONS)
                },
                onTabSelected = { tab ->
                    when (tab) {
                        ParentTab.HOME -> { /* Already on Home */ }
                        ParentTab.CHILDREN -> navController.navigate(Routes.CHILDREN_LIST)
                        ParentTab.REWARDS -> navController.navigate(Routes.REWARDS_LIST)
                        ParentTab.SETTINGS -> navController.navigate(Routes.SETTINGS_LIST)
                    }
                }
            )
        }
        composable(Routes.CHILDREN_LIST) {
            MyChildrenRoute(
                onChildClick = { childId ->
                    navController.navigate("child_report/$childId")
                },
                onAddChild = {
                    navController.navigate("add_edit_child?childId=new")
                },
                onTabSelected = { tab ->
                    when (tab) {
                        ParentTab.HOME -> navController.navigate(Routes.PARENT_HOME)
                        ParentTab.CHILDREN -> { /* Already on Children */ }
                        ParentTab.REWARDS -> navController.navigate(Routes.REWARDS_LIST)
                        ParentTab.SETTINGS -> navController.navigate(Routes.SETTINGS_LIST)
                    }
                }
            )
        }
        composable(Routes.REWARDS_LIST) {
            RewardsRoute(
                onRewardClick = { rewardId ->
                    navController.navigate("add_edit_reward?rewardId=$rewardId")
                },
                onAddReward = {
                    navController.navigate("add_edit_reward?rewardId=new")
                },
                onRequestsClick = {
                    navController.navigate(Routes.REWARD_REQUESTS)
                },
                onTabSelected = { tab ->
                    when (tab) {
                        ParentTab.HOME -> navController.navigate(Routes.PARENT_HOME)
                        ParentTab.CHILDREN -> navController.navigate(Routes.CHILDREN_LIST)
                        ParentTab.REWARDS -> { /* Already on Rewards */ }
                        ParentTab.SETTINGS -> navController.navigate(Routes.SETTINGS_LIST)
                    }
                }
            )
        }
        composable(Routes.REWARD_REQUESTS) {
            RewardRequestsRoute(
                onRewardsClick = {
                    navController.navigate(Routes.REWARDS_LIST) {
                        popUpTo(Routes.REWARDS_LIST) { inclusive = true }
                    }
                },
                onTabSelected = { tab ->
                    when (tab) {
                        ParentTab.HOME -> navController.navigate(Routes.PARENT_HOME)
                        ParentTab.CHILDREN -> navController.navigate(Routes.CHILDREN_LIST)
                        ParentTab.REWARDS -> navController.navigate(Routes.REWARDS_LIST)
                        ParentTab.SETTINGS -> navController.navigate(Routes.SETTINGS_LIST)
                    }
                }
            )
        }
        composable(Routes.CHILD_REPORT) { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: "1"
            ChildReportRoute(
                childId = childId,
                onTaskClick = { taskId ->
                    navController.navigate("task_details/$taskId")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.ADD_EDIT_CHILD) { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId")
            AddEditChildRoute(
                childId = if (childId.isNullOrBlank() || childId == "new") null else childId,
                onSaveSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                onOpenLinkDevice = { id ->
                    navController.navigate("link_device?childId=$id")
                }
            )
        }
        composable(Routes.LINK_DEVICE) { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: "1"
            LinkDeviceRoute(
                childId = childId,
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.TASK_DETAILS) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: "t1"
            TaskDetailsRoute(
                taskId = taskId,
                onEditTask = { id ->
                    navController.navigate("edit_task/$id")
                },
                onDeleteSuccess = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.CREATE_TASK) {
            CreateTaskRoute(
                taskId = null,
                onBack = { navController.popBackStack() },
                onTaskSaved = { navController.popBackStack() },
                onAddEditStep = { stepId ->
                    val targetStep = stepId ?: ""
                    navController.navigate("add_edit_step?taskId=new&stepId=$targetStep")
                }
            )
        }
        composable(Routes.EDIT_TASK) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            CreateTaskRoute(
                taskId = taskId,
                onBack = { navController.popBackStack() },
                onTaskSaved = { navController.popBackStack() },
                onAddEditStep = { stepId ->
                    val targetStep = stepId ?: ""
                    navController.navigate("add_edit_step?taskId=$taskId&stepId=$targetStep")
                }
            )
        }
        composable(Routes.ADD_EDIT_STEP) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            val stepId = backStackEntry.arguments?.getString("stepId")
            AddEditStepRoute(
                taskId = taskId,
                stepId = if (stepId.isNullOrBlank()) null else stepId,
                onSaveSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.ADD_EDIT_REWARD) { backStackEntry ->
            val rewardId = backStackEntry.arguments?.getString("rewardId")
            AddEditRewardRoute(
                rewardId = if (rewardId.isNullOrBlank() || rewardId == "new") null else rewardId,
                onSaveSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsRoute(
                onNotificationClick = { _, type, relatedId ->
                    when (type) {
                        NotificationType.REWARD_REQUEST -> navController.navigate(Routes.REWARD_REQUESTS)
                        NotificationType.STEP_COMPLETED, NotificationType.TASK_STARTED, NotificationType.TASK_COMPLETED -> {
                            val targetTask = relatedId ?: "t1"
                            navController.navigate("task_details/$targetTask")
                        }
                        NotificationType.STARS_EARNED -> {
                            val targetChild = relatedId ?: "1"
                            navController.navigate("child_report/$targetChild")
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS_LIST) {
            SettingsRoute(
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onChangePassword = { navController.navigate(Routes.CHANGE_PASSWORD) },
                onManageChildren = { navController.navigate(Routes.CHILDREN_LIST) },
                onLinkDevice = { navController.navigate("link_device?childId=1") },
                onHelpSupport = { navController.navigate(Routes.HELP_SUPPORT) },
                onSwitchAccount = {
                    navController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSignOut = {
                    navController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onTabSelected = { tab ->
                    when (tab) {
                        ParentTab.HOME -> navController.navigate(Routes.PARENT_HOME)
                        ParentTab.CHILDREN -> navController.navigate(Routes.CHILDREN_LIST)
                        ParentTab.REWARDS -> navController.navigate(Routes.REWARDS_LIST)
                        ParentTab.SETTINGS -> { /* Already on Settings */ }
                    }
                }
            )
        }
        composable(Routes.PROFILE) {
            ParentProfileRoute(
                onSaveSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                onChildrenClick = { navController.navigate(Routes.CHILDREN_LIST) }
            )
        }
        composable(Routes.CHANGE_PASSWORD) {
            ChangePasswordRoute(
                onUpdateSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.CHILD_ENTERCODE) {
            EnterCodeRoute(
                onConnectSuccess = {
                    navController.navigate(Routes.CHOOSE_CHILD) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.CHOOSE_CHILD) {
            ChooseChildRoute(
                onChildChosen = { childId ->
                    navController.navigate("choose_buddy?childId=$childId")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.CHOOSE_BUDDY) { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: "1"
            ChooseBuddyRoute(
                childId = childId,
                onContinue = { id ->
                    navController.navigate("child_home?childId=$id") {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.CHILD_HOME) { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: "1"
            ChildHomeRoute(
                childId = childId,
                onOpenTask = { taskId ->
                    navController.navigate("step_by_step_task/$taskId?childId=$childId")
                },
                onAvatarClick = {
                    navController.navigate(Routes.CHOOSE_CHILD)
                },
                onTabSelected = { tab ->
                    when (tab) {
                        ChildTab.HOME -> { /* Already on Child Home */ }
                        ChildTab.MY_TASKS -> navController.navigate("child_tasks?childId=$childId")
                        ChildTab.REWARDS -> navController.navigate("child_rewards?childId=$childId")
                        ChildTab.ME -> navController.navigate("child_profile?childId=$childId")
                    }
                }
            )
        }
        composable(Routes.CHILD_TASKS) { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: "1"
            MyTasksRoute(
                childId = childId,
                onOpenTask = { taskId ->
                    navController.navigate("step_by_step_task/$taskId?childId=$childId")
                },
                onTabSelected = { tab ->
                    when (tab) {
                        ChildTab.HOME -> navController.navigate("child_home?childId=$childId")
                        ChildTab.MY_TASKS -> { /* Already on My Tasks */ }
                        ChildTab.REWARDS -> navController.navigate("child_rewards?childId=$childId")
                        ChildTab.ME -> navController.navigate("child_profile?childId=$childId")
                    }
                }
            )
        }
        composable(Routes.STEP_BY_STEP_TASK) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: "t1"
            val childId = backStackEntry.arguments?.getString("childId") ?: "1"
            StepByStepTaskRoute(
                taskId = taskId,
                childId = childId,
                onTaskCompleted = { tId, cId ->
                    navController.navigate("task_completed?taskId=$tId&childId=$cId") {
                        popUpTo("child_tasks?childId=$cId")
                    }
                },
                onClose = { navController.popBackStack() }
            )
        }
        composable(Routes.TASK_COMPLETED) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: "t1"
            val childId = backStackEntry.arguments?.getString("childId") ?: "1"
            TaskCompletedRoute(
                taskId = taskId,
                childId = childId,
                onBackToMyTasks = {
                    navController.navigate("child_tasks?childId=$childId") {
                        popUpTo("child_tasks?childId=$childId") { inclusive = true }
                    }
                },
                onGoHome = {
                    navController.navigate("child_home?childId=$childId") {
                        popUpTo("child_home?childId=$childId") { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.CHILD_REWARDS) { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: "1"
            RewardsStoreRoute(
                childId = childId,
                onMyRewardsClick = {
                    navController.navigate("my_rewards?childId=$childId")
                },
                onTabSelected = { tab ->
                    when (tab) {
                        ChildTab.HOME -> navController.navigate("child_home?childId=$childId")
                        ChildTab.MY_TASKS -> navController.navigate("child_tasks?childId=$childId")
                        ChildTab.REWARDS -> { /* Already on Rewards Store */ }
                        ChildTab.ME -> navController.navigate("child_profile?childId=$childId")
                    }
                }
            )
        }
        composable(Routes.MY_REWARDS) { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: "1"
            MyRewardsRoute(
                childId = childId,
                onBack = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        ChildTab.HOME -> navController.navigate("child_home?childId=$childId")
                        ChildTab.MY_TASKS -> navController.navigate("child_tasks?childId=$childId")
                        ChildTab.REWARDS -> navController.navigate("child_rewards?childId=$childId")
                        ChildTab.ME -> navController.navigate("child_profile?childId=$childId")
                    }
                }
            )
        }
        composable(Routes.CHILD_PROFILE) { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: "1"
            ChildProfileRoute(
                childId = childId,
                onChangeBuddy = { id ->
                    navController.navigate("choose_buddy?childId=$id")
                },
                onTabSelected = { tab ->
                    when (tab) {
                        ChildTab.HOME -> navController.navigate("child_home?childId=$childId")
                        ChildTab.MY_TASKS -> navController.navigate("child_tasks?childId=$childId")
                        ChildTab.REWARDS -> navController.navigate("child_rewards?childId=$childId")
                        ChildTab.ME -> { /* Already on Child Profile */ }
                    }
                }
            )
        }
        composable(Routes.HELP_SUPPORT) {
            PlaceholderScreen(title = "Help & Support")
        }
        composable(Routes.DEMO_MODE) {
            PlaceholderScreen(title = "Demo Mode")
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
