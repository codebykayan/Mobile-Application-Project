package com.example.tinystep_stage2

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tinystep_stage2.ui.theme.TinyStepsTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable
object RoleSelection

@Serializable
object ChooseChild

@Serializable
object ParentHome

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
            RoleSelectionRoute(
                onParentContinue = {
                    navController.navigate(ParentHome)
                },
                onChildContinue = {
                    navController.navigate(ChooseChild)
                },
            )
        }
        composable<ChooseChild> {
            ChooseChildRoute(
                onChildChosen = {},
                onBack = {
                    navController.popBackStack()
                },
            )
        }
        composable<ParentHome> {
            ParentHomeRoute(
                onOpenTask = {},
                onCreateTask = {},
                onSwitchRole = {
                    navController.popBackStack()
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
