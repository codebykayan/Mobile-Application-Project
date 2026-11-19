package com.example.tinysteps_1.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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

data class ChildListItemUi(
    val id: String,
    val name: String,
    val age: Int,
    val avatar: String,
    val activeTasks: Int,
    val stars: Int
)

data class MyChildrenUiState(
    val children: List<ChildListItemUi> = emptyList(),
    val selectedTab: ParentTab = ParentTab.CHILDREN,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MyChildrenViewModel : ViewModel() {
    private val sampleChildren = listOf(
        ChildListItemUi(
            id = "1",
            name = "Sara",
            age = 8,
            avatar = "🐰",
            activeTasks = 2,
            stars = 45
        ),
        ChildListItemUi(
            id = "2",
            name = "Omar",
            age = 10,
            avatar = "🐻",
            activeTasks = 1,
            stars = 30
        )
    )

    var uiState by mutableStateOf(
        MyChildrenUiState(
            children = sampleChildren,
            selectedTab = ParentTab.CHILDREN
        )
    )
        private set

    fun onTabSelected(tab: ParentTab) {
        uiState = uiState.copy(selectedTab = tab)
    }
}

@Composable
fun MyChildrenRoute(
    onChildClick: (String) -> Unit,
    onAddChild: () -> Unit,
    onTabSelected: (ParentTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyChildrenViewModel = viewModel()
) {
    MyChildrenScreen(
        uiState = viewModel.uiState,
        onChildClick = onChildClick,
        onAddChild = onAddChild,
        onTabSelected = { tab ->
            viewModel.onTabSelected(tab)
            onTabSelected(tab)
        },
        modifier = modifier
    )
}

@Composable
fun MyChildrenScreen(
    uiState: MyChildrenUiState,
    onChildClick: (String) -> Unit,
    onAddChild: () -> Unit,
    onTabSelected: (ParentTab) -> Unit,
    modifier: Modifier = Modifier
) {
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

                        // Header: Title & Subtitle
                        Text(
                            text = stringResource(id = R.string.my_children_title),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(id = R.string.my_children_subtitle),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Children Cards List
                    if (uiState.children.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 36.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(id = R.string.empty_children_title),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = stringResource(id = R.string.empty_children_subtitle),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(uiState.children) { child ->
                            ChildListCard(
                                child = child,
                                onClick = { onChildClick(child.id) }
                            )
                        }
                    }

                    item {
                        // Dashed "+ Add a child" Button
                        val primaryColor = MaterialTheme.colorScheme.primary
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF1FAF4))
                                .drawWithContent {
                                    drawContent()
                                    drawRoundRect(
                                        color = primaryColor,
                                        style = Stroke(
                                            width = 1.5.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                                        ),
                                        cornerRadius = CornerRadius(20.dp.toPx())
                                    )
                                }
                                .clickable(onClick = onAddChild),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(id = R.string.btn_add_child),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = primaryColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Green Info Box (6-digit pairing code)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFE8F5EE))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Shield,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            val infoText = buildAnnotatedString {
                                append(stringResource(id = R.string.info_pairing_code_prefix))
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(stringResource(id = R.string.pairing_code_bold))
                                }
                                append(stringResource(id = R.string.info_pairing_code_suffix))
                            }

                            Text(
                                text = infoText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                ),
                                color = Color(0xFF1E7844)
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
fun ChildListCard(
    child: ChildListItemUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarBgColor = if (child.avatar.contains("🐰") || child.name == "Sara") {
        Color(0xFFFEF3C7) // Light yellow
    } else {
        Color(0xFFDBEAFE) // Light blue
    }

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
            // Animal Avatar Circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(avatarBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = child.avatar, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Child Info: Name, Age & Active tasks, Stars
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = child.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(
                        id = R.string.child_age_tasks_ratio,
                        child.age,
                        child.activeTasks
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFEAB308),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(id = R.string.child_stars_count, child.stars),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFFD97706)
                    )
                }
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

@Preview(showBackground = true)
@Composable
private fun MyChildrenDefaultPreview() {
    TinyStepsTheme {
        MyChildrenScreen(
            uiState = MyChildrenUiState(
                children = listOf(
                    ChildListItemUi("1", "Sara", 8, "🐰", 2, 45),
                    ChildListItemUi("2", "Omar", 10, "🐻", 1, 30)
                )
            ),
            onChildClick = {},
            onAddChild = {},
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyChildrenEmptyPreview() {
    TinyStepsTheme {
        MyChildrenScreen(
            uiState = MyChildrenUiState(children = emptyList()),
            onChildClick = {},
            onAddChild = {},
            onTabSelected = {}
        )
    }
}
