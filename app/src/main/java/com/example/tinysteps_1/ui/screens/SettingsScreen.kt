package com.example.tinysteps_1.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinysteps_1.R
import com.example.tinysteps_1.ui.theme.TinyStepsTheme

enum class AppLanguage {
    EN,
    AR
}

data class SettingsUiState(
    val parentName: String = "Huda Alharbi",
    val notificationsEnabled: Boolean = true,
    val selectedLanguage: AppLanguage = AppLanguage.EN,
    val selectedTab: ParentTab = ParentTab.SETTINGS,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SettingsViewModel : ViewModel() {
    var uiState by mutableStateOf(SettingsUiState())
        private set

    fun onNotificationsChange(enabled: Boolean) {
        uiState = uiState.copy(notificationsEnabled = enabled)
    }

    fun onLanguageSelected(language: AppLanguage) {
        uiState = uiState.copy(selectedLanguage = language)
    }

    fun onTabSelected(tab: ParentTab) {
        uiState = uiState.copy(selectedTab = tab)
    }
}

@Composable
fun SettingsRoute(
    onProfileClick: () -> Unit,
    onChangePassword: () -> Unit,
    onManageChildren: () -> Unit,
    onLinkDevice: () -> Unit,
    onHelpSupport: () -> Unit,
    onSwitchAccount: () -> Unit,
    onSignOut: () -> Unit,
    onTabSelected: (ParentTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel()
) {
    SettingsScreen(
        uiState = viewModel.uiState,
        onNotificationsChange = viewModel::onNotificationsChange,
        onLanguageSelected = viewModel::onLanguageSelected,
        onProfileClick = onProfileClick,
        onChangePassword = onChangePassword,
        onManageChildren = onManageChildren,
        onLinkDevice = onLinkDevice,
        onHelpSupport = onHelpSupport,
        onSwitchAccount = onSwitchAccount,
        onSignOut = onSignOut,
        onTabSelected = { tab ->
            viewModel.onTabSelected(tab)
            onTabSelected(tab)
        },
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onNotificationsChange: (Boolean) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onProfileClick: () -> Unit,
    onChangePassword: () -> Unit,
    onManageChildren: () -> Unit,
    onLinkDevice: () -> Unit,
    onHelpSupport: () -> Unit,
    onSwitchAccount: () -> Unit,
    onSignOut: () -> Unit,
    onTabSelected: (ParentTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSignOutDialog by remember { mutableStateOf(false) }

    // Sign Out Confirmation Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text(text = stringResource(id = R.string.dialog_signout_title)) },
            text = { Text(text = stringResource(id = R.string.dialog_signout_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        onSignOut()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.label_sign_out),
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text(text = stringResource(id = R.string.btn_cancel))
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF7F9FB),
        bottomBar = {
            ParentBottomBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Title: Settings
                        Text(
                            text = stringResource(id = R.string.settings_title),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Section 1: Profile Card
                        SettingsProfileCard(
                            parentName = uiState.parentName,
                            onClick = onProfileClick
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Section 2: Account & Preferences Card (Notifications, Language, Change Password)
                        SettingsSection {
                            // Notifications Row
                            SettingsRow(
                                title = stringResource(id = R.string.label_notifications),
                                icon = Icons.Outlined.Notifications,
                                trailingContent = {
                                    Switch(
                                        checked = uiState.notificationsEnabled,
                                        onCheckedChange = onNotificationsChange,
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                                            uncheckedThumbColor = Color.White,
                                            uncheckedTrackColor = Color(0xFFCBD5E1)
                                        )
                                    )
                                }
                            )

                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                            // Language Row
                            SettingsRow(
                                title = stringResource(id = R.string.label_language),
                                icon = Icons.Outlined.Language,
                                trailingContent = {
                                    LanguageSelector(
                                        selectedLanguage = uiState.selectedLanguage,
                                        onLanguageSelected = onLanguageSelected
                                    )
                                }
                            )

                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                            // Change Password Row
                            SettingsRow(
                                title = stringResource(id = R.string.label_change_password),
                                icon = Icons.Outlined.Lock,
                                onClick = onChangePassword
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Section 3: Management & Support Card (Manage Children, Link Device, Help & Support)
                        SettingsSection {
                            // Manage Children Row
                            SettingsRow(
                                title = stringResource(id = R.string.label_manage_children),
                                icon = Icons.Outlined.People,
                                onClick = onManageChildren
                            )

                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                            // Link Device Row
                            SettingsRow(
                                title = stringResource(id = R.string.label_link_child_device),
                                icon = Icons.Outlined.Link,
                                onClick = onLinkDevice
                            )

                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                            // Help & Support Row
                            SettingsRow(
                                title = stringResource(id = R.string.label_help_support),
                                icon = Icons.AutoMirrored.Outlined.HelpOutline,
                                onClick = onHelpSupport
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Section 4: Account Actions Card (Switch Account, Sign Out)
                        SettingsSection {
                            // Switch Account Row
                            SettingsRow(
                                title = stringResource(id = R.string.label_switch_account),
                                icon = Icons.Outlined.People,
                                onClick = onSwitchAccount
                            )

                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                            // Sign Out Row (Red Text & Icon)
                            SettingsRow(
                                title = stringResource(id = R.string.label_sign_out),
                                icon = Icons.AutoMirrored.Outlined.Logout,
                                iconTint = Color(0xFFDC2626),
                                iconBg = Color(0xFFFEF2F2),
                                textColor = Color(0xFFDC2626),
                                onClick = { showSignOutDialog = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsProfileCard(
    parentName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initial = parentName.firstOrNull()?.toString() ?: "H"

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Initial Avatar Circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5EE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Parent Name & Subtitle
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = parentName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(id = R.string.label_view_profile),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Chevron
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    icon: ImageVector,
    iconTint: Color = Color(0xFF1B2430),
    iconBg: Color = Color(0xFFF8FAFC),
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val rowModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    } else {
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Container Box
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            color = textColor,
            modifier = Modifier.weight(1f)
        )

        // Trailing Content or Default Chevron
        if (trailingContent != null) {
            trailingContent()
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun LanguageSelector(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF1F5F9))
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // EN Option
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (selectedLanguage == AppLanguage.EN) Color.White else Color.Transparent)
                .clickable { onLanguageSelected(AppLanguage.EN) }
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.lang_en),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = if (selectedLanguage == AppLanguage.EN) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (selectedLanguage == AppLanguage.EN) MaterialTheme.colorScheme.primary else Color(0xFF5B6474)
            )
        }

        // Arabic Option
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (selectedLanguage == AppLanguage.AR) Color.White else Color.Transparent)
                .clickable { onLanguageSelected(AppLanguage.AR) }
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.lang_ar),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = if (selectedLanguage == AppLanguage.AR) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (selectedLanguage == AppLanguage.AR) MaterialTheme.colorScheme.primary else Color(0xFF5B6474)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsDefaultPreview() {
    TinyStepsTheme {
        SettingsScreen(
            uiState = SettingsUiState(
                parentName = "Huda Alharbi",
                notificationsEnabled = true,
                selectedLanguage = AppLanguage.EN
            ),
            onNotificationsChange = {},
            onLanguageSelected = {},
            onProfileClick = {},
            onChangePassword = {},
            onManageChildren = {},
            onLinkDevice = {},
            onHelpSupport = {},
            onSwitchAccount = {},
            onSignOut = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsArabicPreview() {
    TinyStepsTheme {
        SettingsScreen(
            uiState = SettingsUiState(
                parentName = "Huda Alharbi",
                notificationsEnabled = true,
                selectedLanguage = AppLanguage.AR
            ),
            onNotificationsChange = {},
            onLanguageSelected = {},
            onProfileClick = {},
            onChangePassword = {},
            onManageChildren = {},
            onLinkDevice = {},
            onHelpSupport = {},
            onSwitchAccount = {},
            onSignOut = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsNotificationsDisabledPreview() {
    TinyStepsTheme {
        SettingsScreen(
            uiState = SettingsUiState(
                parentName = "Huda Alharbi",
                notificationsEnabled = false,
                selectedLanguage = AppLanguage.EN
            ),
            onNotificationsChange = {},
            onLanguageSelected = {},
            onProfileClick = {},
            onChangePassword = {},
            onManageChildren = {},
            onLinkDevice = {},
            onHelpSupport = {},
            onSwitchAccount = {},
            onSignOut = {},
            onTabSelected = {}
        )
    }
}
