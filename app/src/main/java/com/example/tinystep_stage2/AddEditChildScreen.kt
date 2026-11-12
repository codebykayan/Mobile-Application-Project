package com.example.tinystep_stage2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tinystep_stage2.ui.theme.ErrorRed
import com.example.tinystep_stage2.ui.theme.PrimaryGreen
import com.example.tinystep_stage2.ui.theme.SecondaryAccent
import com.example.tinystep_stage2.ui.theme.TinyStepsTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

data class AddEditChildUiState(
    val children: List<ChildProfile> = emptyList(),
    val isFormOpen: Boolean = false,
    val isEditMode: Boolean = false,
    val childId: String? = null,
    val name: String = "",
    val age: String = "",
    val childToDelete: ChildProfile? = null,
    val isLoading: Boolean = false,
)

class AddEditChildViewModel : ViewModel() {
    var uiState by mutableStateOf(AddEditChildUiState())
        private set

    private var initialized = false

    fun initChild(initialChildId: String?) {
        if (initialized) return
        initialized = true

        loadChildrenAndInit(initialChildId)
    }

    private fun loadChildrenAndInit(initialChildId: String?) {
        val currentChildren = AppRepository.children
        uiState = uiState.copy(children = currentChildren)

        if (initialChildId != null) {
            val child = AppRepository.getChildById(initialChildId)
            if (child != null) {
                openEditForm(child)
            } else {
                openAddForm()
            }
        }

        try {
            val auth = FirebaseAuth.getInstance()
            val uid = auth.currentUser?.uid
            if (uid != null) {
                FirebaseFirestore.getInstance()
                    .collection("parents")
                    .document(uid)
                    .collection("children")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot != null && !snapshot.isEmpty) {
                            val firestoreChildren = snapshot.documents.mapNotNull { doc ->
                                val id = doc.id
                                val name = doc.getString("name") ?: return@mapNotNull null
                                val age = doc.getLong("age")?.toInt()
                                    ?: doc.getString("age")?.toIntOrNull()
                                    ?: 0
                                val avatarColor = doc.getLong("avatarColor") ?: 0xFFFFE3A3
                                ChildProfile(id = id, name = name, age = age, avatarColor = avatarColor)
                            }
                            AppRepository.updateChildrenList(firestoreChildren)
                            uiState = uiState.copy(children = firestoreChildren)
                            if (initialChildId != null && !uiState.isFormOpen) {
                                val child = AppRepository.getChildById(initialChildId)
                                if (child != null) openEditForm(child)
                            }
                        }
                    }
            }
        } catch (_: Exception) {
            // Handled gracefully in offline or uninitialized mode
        }
    }

    fun openAddForm() {
        uiState = uiState.copy(
            isFormOpen = true,
            isEditMode = false,
            childId = null,
            name = "",
            age = "",
        )
    }

    fun openEditForm(child: ChildProfile) {
        uiState = uiState.copy(
            isFormOpen = true,
            isEditMode = true,
            childId = child.id,
            name = child.name,
            age = child.age.toString(),
        )
    }

    fun closeForm() {
        uiState = uiState.copy(
            isFormOpen = false,
            isEditMode = false,
            childId = null,
            name = "",
            age = "",
        )
    }

    fun onNameChange(name: String) {
        uiState = uiState.copy(name = name)
    }

    fun onAgeChange(age: String) {
        uiState = uiState.copy(age = age)
    }

    fun onRequestDelete(child: ChildProfile) {
        uiState = uiState.copy(childToDelete = child)
    }

    fun onDismissDelete() {
        uiState = uiState.copy(childToDelete = null)
    }

    fun onConfirmDelete() {
        val child = uiState.childToDelete ?: return
        val childId = child.id

        AppRepository.deleteChild(childId)
        uiState = uiState.copy(
            children = AppRepository.children,
            childToDelete = null,
        )

        try {
            val auth = FirebaseAuth.getInstance()
            val uid = auth.currentUser?.uid
            if (uid != null) {
                FirebaseFirestore.getInstance()
                    .collection("parents")
                    .document(uid)
                    .collection("children")
                    .document(childId)
                    .delete()
            }
        } catch (_: Exception) {
            // Handled gracefully
        }
    }

    fun saveChild(onSuccess: () -> Unit = {}) {
        val nameTrimmed = uiState.name.trim()
        val ageInt = uiState.age.trim().toIntOrNull() ?: 0
        val currentChildId = uiState.childId

        if (nameTrimmed.isNotBlank()) {
            val savedChild: ChildProfile
            if (uiState.isEditMode && currentChildId != null) {
                AppRepository.updateChild(currentChildId, nameTrimmed, ageInt)
                savedChild = AppRepository.getChildById(currentChildId)
                    ?: ChildProfile(id = currentChildId, name = nameTrimmed, age = ageInt)
            } else {
                savedChild = AppRepository.addChild(nameTrimmed, ageInt)
            }

            uiState = uiState.copy(
                children = AppRepository.children,
                isFormOpen = false,
                isEditMode = false,
                childId = null,
                name = "",
                age = "",
            )

            try {
                val auth = FirebaseAuth.getInstance()
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    val childMap = mapOf(
                        "id" to savedChild.id,
                        "name" to savedChild.name,
                        "age" to savedChild.age,
                        "avatarColor" to savedChild.avatarColor
                    )
                    FirebaseFirestore.getInstance()
                        .collection("parents")
                        .document(uid)
                        .collection("children")
                        .document(savedChild.id)
                        .set(childMap, SetOptions.merge())
                }
            } catch (_: Exception) {
                // Handled gracefully
            }

            onSuccess()
        }
    }
}

@Composable
fun AddEditChildRoute(
    modifier: Modifier = Modifier,
    childId: String? = null,
    viewModel: AddEditChildViewModel = viewModel(),
    onSaveSuccess: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    LaunchedEffect(childId) {
        viewModel.initChild(childId)
    }

    val uiState = viewModel.uiState

    AddEditChildScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onAgeChange = viewModel::onAgeChange,
        onOpenAddForm = viewModel::openAddForm,
        onOpenEditForm = viewModel::openEditForm,
        onCloseForm = viewModel::closeForm,
        onRequestDelete = viewModel::onRequestDelete,
        onConfirmDelete = viewModel::onConfirmDelete,
        onDismissDelete = viewModel::onDismissDelete,
        onSaveClick = {
            viewModel.saveChild(onSuccess = {
                if (childId != null) {
                    onSaveSuccess()
                }
            })
        },
        onBack = {
            if (uiState.isFormOpen && childId == null) {
                viewModel.closeForm()
            } else {
                onBack()
            }
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditChildScreen(
    uiState: AddEditChildUiState,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onOpenAddForm: () -> Unit,
    onOpenEditForm: (ChildProfile) -> Unit,
    onCloseForm: () -> Unit,
    onRequestDelete: (ChildProfile) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onSaveClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFormOpen = uiState.isFormOpen
    val isEditMode = uiState.isEditMode

    val screenTitle = if (isFormOpen) {
        if (isEditMode) stringResource(R.string.title_edit_child) else stringResource(R.string.title_add_child)
    } else {
        stringResource(R.string.title_manage_children)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (isFormOpen) {
                ChildFormContent(
                    name = uiState.name,
                    age = uiState.age,
                    isEditMode = isEditMode,
                    onNameChange = onNameChange,
                    onAgeChange = onAgeChange,
                    onSaveClick = onSaveClick,
                    onCancelClick = onCloseForm,
                )
            } else {
                ChildrenListContent(
                    children = uiState.children,
                    onAddChildClick = onOpenAddForm,
                    onEditClick = onOpenEditForm,
                    onDeleteClick = onRequestDelete,
                )
            }
        }
    }

    if (uiState.childToDelete != null) {
        AlertDialog(
            onDismissRequest = onDismissDelete,
            title = {
                Text(
                    text = stringResource(R.string.delete_child_title),
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.delete_child_message,
                        uiState.childToDelete.name,
                    ),
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                ) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = Color.White,
                    )
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismissDelete) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun ChildrenListContent(
    children: List<ChildProfile>,
    onAddChildClick: () -> Unit,
    onEditClick: (ChildProfile) -> Unit,
    onDeleteClick: (ChildProfile) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Button(
            onClick = onAddChildClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen,
                contentColor = Color.White,
            ),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(
                text = stringResource(R.string.add_child_button),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (children.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.no_children_added),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                items(children, key = { it.id }) { child ->
                    ChildItemCard(
                        child = child,
                        onEditClick = { onEditClick(child) },
                        onDeleteClick = { onDeleteClick(child) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ChildItemCard(
    child: ChildProfile,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val avatarLetter = child.name.trim().take(1).uppercase().ifBlank { "?" }
    val avatarColor = if (child.avatarColor != 0L) Color(child.avatarColor) else SecondaryAccent

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = avatarColor,
                modifier = Modifier.size(52.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = avatarLetter,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = child.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.years_old, child.age),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.title_edit_child),
                        tint = PrimaryGreen,
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.action_delete),
                        tint = ErrorRed,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChildFormContent(
    name: String,
    age: String,
    isEditMode: Boolean,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonText = if (isEditMode) stringResource(R.string.save_changes) else stringResource(R.string.title_add_child)
    val avatarLetter = name.trim().take(1).uppercase().ifBlank { "?" }
    val isSaveEnabled = name.isNotBlank() && age.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = CircleShape,
                color = SecondaryAccent,
                modifier = Modifier.size(96.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = avatarLetter,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.child_name_label),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = { Text(stringResource(R.string.enter_name_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.age_label),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = age,
                onValueChange = onAgeChange,
                placeholder = { Text(stringResource(R.string.enter_age_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onSaveClick,
                enabled = isSaveEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color.White,
                    disabledContainerColor = PrimaryGreen.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f),
                ),
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }

            OutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Text(
                    text = stringResource(R.string.action_cancel),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ManageChildrenListPreview() {
    TinyStepsTheme {
        AddEditChildScreen(
            uiState = AddEditChildUiState(
                children = listOf(
                    ChildProfile(id = "1", name = "Sara", age = 8, avatarColor = 0xFFFFE3A3),
                    ChildProfile(id = "2", name = "Omar", age = 10, avatarColor = 0xFFCDE4FA),
                ),
                isFormOpen = false,
            ),
            onNameChange = {},
            onAgeChange = {},
            onOpenAddForm = {},
            onOpenEditForm = {},
            onCloseForm = {},
            onRequestDelete = {},
            onConfirmDelete = {},
            onDismissDelete = {},
            onSaveClick = {},
            onBack = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditChildFormPreview() {
    TinyStepsTheme {
        AddEditChildScreen(
            uiState = AddEditChildUiState(
                isFormOpen = true,
                isEditMode = true,
                name = "Sara",
                age = "8",
            ),
            onNameChange = {},
            onAgeChange = {},
            onOpenAddForm = {},
            onOpenEditForm = {},
            onCloseForm = {},
            onRequestDelete = {},
            onConfirmDelete = {},
            onDismissDelete = {},
            onSaveClick = {},
            onBack = {},
        )
    }
}
