package com.example.tinysteps_1.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tinysteps_1.R
import com.example.tinysteps_1.ui.theme.TinyStepsTheme

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
    onTryDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRole by rememberSaveable { mutableStateOf(UserRole.PARENT) }

    RoleSelectionScreen(
        uiState = RoleSelectionUiState(selectedRole = selectedRole),
        onRoleSelected = { selectedRole = it },
        onContinue = {
            if (selectedRole == UserRole.PARENT) {
                onParentContinue()
            } else {
                onChildContinue()
            }
        },
        onTryDemo = onTryDemo,
        modifier = modifier
    )
}

@Composable
fun RoleSelectionScreen(
    uiState: RoleSelectionUiState,
    onRoleSelected: (UserRole) -> Unit,
    onContinue: () -> Unit,
    onTryDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE4F6EC),
            Color(0xFFEFF9F3)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.tinysteps_logo),
                contentDescription = stringResource(id = R.string.logo_content_description),
                modifier = Modifier
                    .size(110.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color(0x1A25844F)
                    )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // App Title
            Text(
                text = stringResource(id = R.string.splash_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = stringResource(id = R.string.splash_tagline),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Section Heading
            Text(
                text = stringResource(id = R.string.role_selection_heading),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Parent / Teacher Card
            RoleCard(
                icon = Icons.Default.People,
                title = stringResource(id = R.string.role_parent_title),
                subtitle = stringResource(id = R.string.role_parent_subtitle),
                selected = uiState.selectedRole == UserRole.PARENT,
                onClick = { onRoleSelected(UserRole.PARENT) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Child Card
            RoleCard(
                icon = Icons.Default.Face,
                title = stringResource(id = R.string.role_child_title),
                subtitle = stringResource(id = R.string.role_child_subtitle),
                selected = uiState.selectedRole == UserRole.CHILD,
                onClick = { onRoleSelected(UserRole.CHILD) }
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(40.dp))

            // Continue Button
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.btn_continue),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Demo Mode Action
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onTryDemo)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.btn_try_demo),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RoleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBackground = if (selected) Color(0xFFE8F5EE) else Color.White
    val borderModifier = if (selected) {
        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
    } else {
        Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .then(borderModifier)
            .clickable(
                onClick = onClick,
                role = Role.RadioButton
            ),
        shape = RoundedCornerShape(20.dp),
        color = cardBackground,
        shadowElevation = if (selected) 0.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (selected) Color(0xFFCDECDA) else Color(0xFFF1FAF4)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.primary else Color(0xFF5B6474),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title and Subtitle
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Selection Circle Indicator
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFCBD5E1), CircleShape)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoleSelectionParentPreview() {
    TinyStepsTheme {
        RoleSelectionScreen(
            uiState = RoleSelectionUiState(selectedRole = UserRole.PARENT),
            onRoleSelected = {},
            onContinue = {},
            onTryDemo = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoleSelectionChildPreview() {
    TinyStepsTheme {
        RoleSelectionScreen(
            uiState = RoleSelectionUiState(selectedRole = UserRole.CHILD),
            onRoleSelected = {},
            onContinue = {},
            onTryDemo = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoleCardPreview() {
    TinyStepsTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            RoleCard(
                icon = Icons.Default.People,
                title = "Parent / Teacher",
                subtitle = "Create, assign and follow tasks",
                selected = true,
                onClick = {}
            )
            Spacer(modifier = Modifier.height(12.dp))
            RoleCard(
                icon = Icons.Default.Face,
                title = "Child",
                subtitle = "See and finish my tasks",
                selected = false,
                onClick = {}
            )
        }
    }
}
