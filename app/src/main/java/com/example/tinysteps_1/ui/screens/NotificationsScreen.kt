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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinysteps_1.R
import com.example.tinysteps_1.ui.theme.TinyStepsTheme

enum class NotificationType {
    REWARD_REQUEST,
    STEP_COMPLETED,
    TASK_STARTED,
    STARS_EARNED,
    TASK_COMPLETED
}

data class NotificationUi(
    val id: String,
    val type: NotificationType,
    val title: String,
    val description: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val relatedId: String? = null,
    val section: String = "TODAY"
)

data class NotificationsUiState(
    val todayNotifications: List<NotificationUi> = emptyList(),
    val yesterdayNotifications: List<NotificationUi> = emptyList(),
    val unreadCount: Int = 3,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class NotificationsViewModel : ViewModel() {
    private val initialToday = listOf(
        NotificationUi(
            id = "n1",
            type = NotificationType.REWARD_REQUEST,
            title = "Omar asked for 30 min extra game time",
            description = "Tap to approve · 1h",
            timestamp = "1h",
            isRead = false,
            relatedId = "req2",
            section = "TODAY"
        ),
        NotificationUi(
            id = "n2",
            type = NotificationType.REWARD_REQUEST,
            title = "Sara asked for Ice cream trip",
            description = "Tap to approve · 5m",
            timestamp = "5m",
            isRead = false,
            relatedId = "req1",
            section = "TODAY"
        ),
        NotificationUi(
            id = "n3",
            type = NotificationType.STEP_COMPLETED,
            title = "Sara finished step 5 of Get Ready for School",
            description = "Progress 83% · 10m",
            timestamp = "10m",
            isRead = false,
            relatedId = "t1",
            section = "TODAY"
        ),
        NotificationUi(
            id = "n4",
            type = NotificationType.TASK_STARTED,
            title = "Omar started Clean My Room",
            description = "2 of 4 steps · 2h",
            timestamp = "2h",
            isRead = true,
            relatedId = "t2",
            section = "TODAY"
        )
    )

    private val initialYesterday = listOf(
        NotificationUi(
            id = "n5",
            type = NotificationType.STARS_EARNED,
            title = "Sara earned 10 stars for Water the Plants",
            description = "Total 45 stars · 6:10 PM",
            timestamp = "6:10 PM",
            isRead = true,
            relatedId = "1",
            section = "YESTERDAY"
        ),
        NotificationUi(
            id = "n6",
            type = NotificationType.TASK_COMPLETED,
            title = "Omar completed Set the Table",
            description = "All 3 steps done · 5:30 PM",
            timestamp = "5:30 PM",
            isRead = true,
            relatedId = "t4",
            section = "YESTERDAY"
        )
    )

    var uiState by mutableStateOf(
        NotificationsUiState(
            todayNotifications = initialToday,
            yesterdayNotifications = initialYesterday,
            unreadCount = initialToday.count { !it.isRead } + initialYesterday.count { !it.isRead }
        )
    )
        private set

    fun markAllRead() {
        val updatedToday = uiState.todayNotifications.map { it.copy(isRead = true) }
        val updatedYesterday = uiState.yesterdayNotifications.map { it.copy(isRead = true) }
        uiState = uiState.copy(
            todayNotifications = updatedToday,
            yesterdayNotifications = updatedYesterday,
            unreadCount = 0
        )
    }

    fun markRead(id: String): NotificationUi? {
        var foundNotification: NotificationUi? = null
        val updatedToday = uiState.todayNotifications.map {
            if (it.id == id) {
                foundNotification = it
                it.copy(isRead = true)
            } else it
        }
        val updatedYesterday = uiState.yesterdayNotifications.map {
            if (it.id == id) {
                foundNotification = it
                it.copy(isRead = true)
            } else it
        }

        uiState = uiState.copy(
            todayNotifications = updatedToday,
            yesterdayNotifications = updatedYesterday,
            unreadCount = updatedToday.count { !it.isRead } + updatedYesterday.count { !it.isRead }
        )
        return foundNotification
    }
}

@Composable
fun NotificationsRoute(
    onNotificationClick: (String, NotificationType, String?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = viewModel()
) {
    NotificationsScreen(
        uiState = viewModel.uiState,
        onNotificationClick = { id ->
            val notification = viewModel.markRead(id)
            if (notification != null) {
                onNotificationClick(notification.id, notification.type, notification.relatedId)
            }
        },
        onMarkAllRead = viewModel::markAllRead,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun NotificationsScreen(
    uiState: NotificationsUiState,
    onNotificationClick: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Header Bar: Back Arrow, Title, Mark All Read
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(id = R.string.back_content_description),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(id = R.string.notifications_title),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = stringResource(id = R.string.btn_mark_all_read),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onMarkAllRead)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Empty State
                if (uiState.todayNotifications.isEmpty() && uiState.yesterdayNotifications.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp, vertical = 64.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.empty_notifications_title),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(id = R.string.empty_notifications_subtitle),
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // TODAY Section
                    if (uiState.todayNotifications.isNotEmpty()) {
                        item {
                            NotificationSectionHeader(title = stringResource(id = R.string.section_today))
                        }

                        itemsIndexed(uiState.todayNotifications) { index, notification ->
                            NotificationRow(
                                notification = notification,
                                onClick = { onNotificationClick(notification.id) }
                            )
                            if (index < uiState.todayNotifications.size - 1) {
                                HorizontalDivider(
                                    color = Color(0xFFF1F5F9),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }

                    // YESTERDAY Section
                    if (uiState.yesterdayNotifications.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            NotificationSectionHeader(title = stringResource(id = R.string.section_yesterday))
                        }

                        itemsIndexed(uiState.yesterdayNotifications) { index, notification ->
                            NotificationRow(
                                notification = notification,
                                onClick = { onNotificationClick(notification.id) }
                            )
                            if (index < uiState.yesterdayNotifications.size - 1) {
                                HorizontalDivider(
                                    color = Color(0xFFF1F5F9),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun NotificationSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = Color(0xFF64748B)
        )
    }
}

@Composable
fun NotificationRow(
    notification: NotificationUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Colored Icon Box
        val (icon, iconTint, bgTint) = when (notification.type) {
            NotificationType.REWARD_REQUEST -> Triple(
                Icons.Outlined.CardGiftcard,
                Color(0xFFD97706), // Gold/Amber
                Color(0xFFFEF3C7)  // Light yellow
            )
            NotificationType.STEP_COMPLETED, NotificationType.TASK_COMPLETED -> Triple(
                Icons.Outlined.Check,
                MaterialTheme.colorScheme.primary, // Green
                Color(0xFFE8F5EE)                  // Light mint
            )
            NotificationType.TASK_STARTED -> Triple(
                Icons.Outlined.Schedule,
                MaterialTheme.colorScheme.tertiary, // Purple
                Color(0xFFEDE9FE)                   // Light purple
            )
            NotificationType.STARS_EARNED -> Triple(
                Icons.Default.Star,
                Color(0xFFEAB308), // Gold
                Color(0xFFFEF3C7)  // Light yellow
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bgTint),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title & Subtitle Column
        Column(
            modifier = Modifier.weight(1f)
        ) {
            val formattedTitle = buildAnnotatedTitle(notification)
            Text(
                text = formattedTitle,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = notification.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Right Unread Green Dot
        if (!notification.isRead) {
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun buildAnnotatedTitle(notification: NotificationUi) = buildAnnotatedString {
    val titleText = notification.title
    // Format bold keywords in notification titles
    when (notification.type) {
        NotificationType.REWARD_REQUEST -> {
            // e.g. "Omar asked for 30 min extra game time"
            val parts = titleText.split(" asked for ")
            if (parts.size == 2) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(parts[0]) }
                append(" asked for ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(parts[1]) }
            } else {
                append(titleText)
            }
        }
        NotificationType.STEP_COMPLETED -> {
            // e.g. "Sara finished step 5 of Get Ready for School"
            val parts = titleText.split(" finished step 5 of ")
            if (parts.size == 2) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(parts[0]) }
                append(" finished step 5 of ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(parts[1]) }
            } else {
                append(titleText)
            }
        }
        NotificationType.TASK_STARTED -> {
            // e.g. "Omar started Clean My Room"
            val parts = titleText.split(" started ")
            if (parts.size == 2) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(parts[0]) }
                append(" started ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(parts[1]) }
            } else {
                append(titleText)
            }
        }
        NotificationType.STARS_EARNED -> {
            // e.g. "Sara earned 10 stars for Water the Plants"
            val parts = titleText.split(" earned ")
            if (parts.size == 2) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(parts[0]) }
                append(" earned ")
                val subParts = parts[1].split(" for ")
                if (subParts.size == 2) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(subParts[0]) }
                    append(" for ")
                    append(subParts[1])
                } else {
                    append(parts[1])
                }
            } else {
                append(titleText)
            }
        }
        NotificationType.TASK_COMPLETED -> {
            // e.g. "Omar completed Set the Table"
            val parts = titleText.split(" completed ")
            if (parts.size == 2) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(parts[0]) }
                append(" completed ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(parts[1]) }
            } else {
                append(titleText)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsDefaultPreview() {
    TinyStepsTheme {
        NotificationsScreen(
            uiState = NotificationsUiState(
                todayNotifications = listOf(
                    NotificationUi("n1", NotificationType.REWARD_REQUEST, "Omar asked for 30 min extra game time", "Tap to approve · 1h", "1h", false),
                    NotificationUi("n2", NotificationType.REWARD_REQUEST, "Sara asked for Ice cream trip", "Tap to approve · 5m", "5m", false),
                    NotificationUi("n3", NotificationType.STEP_COMPLETED, "Sara finished step 5 of Get Ready for School", "Progress 83% · 10m", "10m", false),
                    NotificationUi("n4", NotificationType.TASK_STARTED, "Omar started Clean My Room", "2 of 4 steps · 2h", "2h", true)
                ),
                yesterdayNotifications = listOf(
                    NotificationUi("n5", NotificationType.STARS_EARNED, "Sara earned 10 stars for Water the Plants", "Total 45 stars · 6:10 PM", "6:10 PM", true),
                    NotificationUi("n6", NotificationType.TASK_COMPLETED, "Omar completed Set the Table", "All 3 steps done · 5:30 PM", "5:30 PM", true)
                ),
                unreadCount = 3
            ),
            onNotificationClick = {},
            onMarkAllRead = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsAllReadPreview() {
    TinyStepsTheme {
        NotificationsScreen(
            uiState = NotificationsUiState(
                todayNotifications = listOf(
                    NotificationUi("n1", NotificationType.REWARD_REQUEST, "Omar asked for 30 min extra game time", "Tap to approve · 1h", "1h", true),
                    NotificationUi("n2", NotificationType.REWARD_REQUEST, "Sara asked for Ice cream trip", "Tap to approve · 5m", "5m", true)
                ),
                yesterdayNotifications = listOf(
                    NotificationUi("n5", NotificationType.STARS_EARNED, "Sara earned 10 stars for Water the Plants", "Total 45 stars · 6:10 PM", "6:10 PM", true)
                ),
                unreadCount = 0
            ),
            onNotificationClick = {},
            onMarkAllRead = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsEmptyPreview() {
    TinyStepsTheme {
        NotificationsScreen(
            uiState = NotificationsUiState(
                todayNotifications = emptyList(),
                yesterdayNotifications = emptyList(),
                unreadCount = 0
            ),
            onNotificationClick = {},
            onMarkAllRead = {},
            onBack = {}
        )
    }
}
