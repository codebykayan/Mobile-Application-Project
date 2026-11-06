package com.example.tinystep_stage2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tinystep_stage2.ui.theme.TinyStepsTheme

enum class UserRole {
    PARENT,
    CHILD
}

data class RoleSelectionUiState(
    val selectedRole: UserRole = UserRole.PARENT
)

@Composable
fun RoleSelectionRoute(
    onParentContinue: () -> Unit,
    onChildContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRole by rememberSaveable { mutableStateOf(UserRole.PARENT) }
    val uiState = RoleSelectionUiState(selectedRole = selectedRole)

    RoleSelectionScreen(
        uiState = uiState,
        onRoleSelected = { role -> selectedRole = role },
        onContinue = {
            when (selectedRole) {
                UserRole.PARENT -> onParentContinue()
                UserRole.CHILD -> onChildContinue()
            }
        },
        modifier = modifier
    )
}

@Composable
fun RoleSelectionScreen(
    uiState: RoleSelectionUiState,
    onRoleSelected: (UserRole) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.tinysteps_logo),
                    contentDescription = stringResource(id = R.string.app_title),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.app_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.tagline),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = stringResource(id = R.string.role_selection_heading),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(16.dp))
                RoleCard(
                    icon = painterResource(id = R.drawable.ic_parent),
                    title = stringResource(id = R.string.parent_role_title),
                    subtitle = stringResource(id = R.string.parent_role_subtitle),
                    selected = uiState.selectedRole == UserRole.PARENT,
                    onClick = { onRoleSelected(UserRole.PARENT) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                RoleCard(
                    icon = painterResource(id = R.drawable.ic_child),
                    title = stringResource(id = R.string.child_role_title),
                    subtitle = stringResource(id = R.string.child_role_subtitle),
                    selected = uiState.selectedRole == UserRole.CHILD,
                    onClick = { onRoleSelected(UserRole.CHILD) }
                )
            }

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.continue_button),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RoleCard(
    icon: Painter,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val backgroundColor = if (selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        shape = MaterialTheme.shapes.medium,
        color = backgroundColor,
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        tonalElevation = if (selected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoleSelectionScreenParentSelectedPreview() {
    TinyStepsTheme {
        RoleSelectionScreen(
            uiState = RoleSelectionUiState(selectedRole = UserRole.PARENT),
            onRoleSelected = {},
            onContinue = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoleSelectionScreenChildSelectedPreview() {
    TinyStepsTheme {
        RoleSelectionScreen(
            uiState = RoleSelectionUiState(selectedRole = UserRole.CHILD),
            onRoleSelected = {},
            onContinue = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoleCardPreview() {
    TinyStepsTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            RoleCard(
                icon = painterResource(id = R.drawable.ic_parent),
                title = "Parent / Teacher",
                subtitle = "Create, assign and follow tasks",
                selected = true,
                onClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            RoleCard(
                icon = painterResource(id = R.drawable.ic_child),
                title = "Child",
                subtitle = "See and finish my tasks",
                selected = false,
                onClick = {}
            )
        }
    }
}
