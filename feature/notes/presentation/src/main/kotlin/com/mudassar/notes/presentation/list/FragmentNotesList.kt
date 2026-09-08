package com.mudassar.notes.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mudassar.notes.base.BaseFragment
import com.mudassar.notes.design.AppHeader
import com.mudassar.notes.design.FullErrorScreen
import com.mudassar.notes.design.FullScreenLoader
import com.mudassar.notes.design.StatusDot
import com.mudassar.notes.models.NoteId
import com.mudassar.notes.models.NoteStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentNotesList : BaseFragment() {
    private val viewModel: NotesListViewModel by viewModels()

    @Composable
    override fun UiContent() {
        val state by viewModel.state.collectAsStateWithLifecycle()
        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
        val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
        NotesListContent(
            state = state,
            searchQuery = searchQuery,
            isLoggedIn = isLoggedIn,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onNoteClicked = viewModel::onNoteClicked,
            onAddNoteClicked = viewModel::onAddNoteClicked,
            onLoginClicked = viewModel::onLoginClicked,
        )
    }
}

@Composable
private fun NotesListContent(
    state: NotesListUiState,
    searchQuery: String,
    isLoggedIn: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onNoteClicked: (NoteId) -> Unit,
    onAddNoteClicked: () -> Unit,
    onLoginClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .systemBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            AppHeader(
                title = null,
                trailingContent = if (isLoggedIn) null else {
                    { TextButton(onClick = onLoginClicked) { Text("Login") } }
                },
                content = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        placeholder = { Text(text = "Search notes") },
                        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
                        singleLine = true,
                    )
                },
            )
            when (state) {
                NotesListUiState.Loading -> FullScreenLoader()
                NotesListUiState.Empty -> FullErrorScreen(message = "No notes yet")
                NotesListUiState.NoResults -> FullErrorScreen(message = "No notes found")
                is NotesListUiState.Success -> NotesListScreen(state.notes, onNoteClicked)
            }
        }
        Button(
            onClick = onAddNoteClicked,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Text(text = "+ New")
        }
    }
}

@Composable
private fun NotesListScreen(notes: List<NoteUiModel>, onNoteClicked: (NoteId) -> Unit) {
    LazyColumn {
        items(notes.size) { index ->
            NoteItem(note = notes[index], onClick = onNoteClicked)
            if (index < notes.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun NoteItem(note: NoteUiModel, onClick: (NoteId) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = { onClick(note.id) })
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(text = note.title, fontWeight = FontWeight.W800)
            Text(text = note.updatedAt, fontSize = 15.sp)
        }
        StatusDot(
            color = note.status.toColor(),
            size = 20.dp,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

private fun NoteStatus.toColor(): Color = when (this) {
    NoteStatus.PENDING -> Color(0xFFFFC107)
    NoteStatus.SYNCED -> Color(0xFF4CAF50)
    NoteStatus.ERROR -> Color(0xFFF44336)
}
