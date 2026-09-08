package com.mudassar.notes.presentation.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mudassar.notes.base.BaseFragment
import com.mudassar.notes.design.AppHeader
import com.mudassar.notes.design.FullScreenLoader
import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentNoteEdit : BaseFragment() {
    private val viewModel: NoteEditViewModel by viewModels()

    @Composable
    override fun UiContent() {
        val state by viewModel.state.collectAsStateWithLifecycle()
        when (val current = state) {
            NoteEditUiState.Loading -> FullScreenLoader()
            is NoteEditUiState.Editing -> NoteEditContent(
                note = current.note,
                isSaved = current.isSaved,
                onTitleChanged = viewModel::onTitleChanged,
                onContentChanged = viewModel::onContentChanged,
                onSaveClicked = viewModel::onSaveClicked,
                onDeleteClicked = viewModel::onDeleteClicked,
                onBackClicked = viewModel::onBackClicked,
            )
        }
    }
}

@Composable
private fun NoteEditContent(
    note: Note,
    isSaved: Boolean,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        AppHeader("Edit Note", onBack = onBackClicked)

        if (note.status == NoteStatus.ERROR && !note.failureReason.isNullOrBlank()) {
            Text(
                text = "Sync failed: ${note.failureReason}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        OutlinedTextField(
            value = note.title,
            onValueChange = onTitleChanged,
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = note.content,
            onValueChange = onContentChanged,
            label = { Text("Content") },
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(top = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onSaveClicked, modifier = Modifier.fillMaxWidth()) {
                Text("Save")
            }
            Button(onClick = onDeleteClicked, enabled = isSaved, modifier = Modifier.fillMaxWidth()) {
                Text("Delete")
            }
        }
    }
}
