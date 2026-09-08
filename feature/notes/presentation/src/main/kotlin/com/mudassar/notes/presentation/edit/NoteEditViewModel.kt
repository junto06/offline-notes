package com.mudassar.notes.presentation.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudassar.notes.navigation.Navigator
import com.mudassar.notes.navigation.popBackStack
import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteId
import com.mudassar.notes.presentation.edit.NoteEditUiState.Editing
import com.mudassar.notes.usecases.DeleteNoteUseCase
import com.mudassar.notes.usecases.LoadNoteForEditUseCase
import com.mudassar.notes.usecases.SaveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    loadNoteForEditUseCase: LoadNoteForEditUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val navigator: Navigator,
) : ViewModel() {

    private val noteId = getNoteId(savedStateHandle)

    private val _state = MutableStateFlow<NoteEditUiState>(NoteEditUiState.Loading)
    val state: StateFlow<NoteEditUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadNoteForEditUseCase(noteId)
                .collect { note ->
                    _state.update { current ->
                        // restore state only from SavedStateHandle if current state is Loading
                        val newNote = when (current) {
                            is NoteEditUiState.Loading -> {
                                restoreDraft(note)
                            }

                            is Editing -> {
                                note.copy(
                                    title = current.note.title,
                                    content = current.note.content
                                )
                            }
                        }
                        Editing(note = newNote, isSaved = noteId != null)
                    }
                }
        }
    }

    fun onTitleChanged(title: String) = updateNote { it.copy(title = title) }

    fun onContentChanged(content: String) = updateNote { it.copy(content = content) }

    private fun updateNote(transform: (Note) -> Note) {
        _state.update { current ->
            if (current is Editing) {
                val updated = transform(current.note)
                saveDraft(updated)
                current.copy(note = updated)
            } else {
                current
            }
        }
    }

    // process death mid-edit
    private fun restoreDraft(note: Note): Note = note.copy(
        title = savedStateHandle[KEY_DRAFT_TITLE] ?: note.title,
        content = savedStateHandle[KEY_DRAFT_CONTENT] ?: note.content,
    )

    private fun saveDraft(note: Note) {
        savedStateHandle[KEY_DRAFT_TITLE] = note.title
        savedStateHandle[KEY_DRAFT_CONTENT] = note.content
    }

    fun onSaveClicked() {
        val editing = _state.value as? Editing ?: return
        viewModelScope.launch {
            saveNoteUseCase(editing.note)
            _state.value = editing.copy(isSaved = true)
            navigator.popBackStack()
        }
    }

    fun onDeleteClicked() {
        val editing = _state.value as? Editing ?: return
        if (!editing.isSaved) return
        viewModelScope.launch {
            deleteNoteUseCase(editing.note)
            navigator.popBackStack()
        }
    }

    fun onBackClicked() {
        navigator.popBackStack()
    }

    private fun getNoteId(savedStateHandle: SavedStateHandle): NoteId? =
        FragmentNoteEditArgs.fromSavedStateHandle(savedStateHandle).noteId?.let(::NoteId)

    private companion object {
        const val KEY_DRAFT_TITLE = "draft_title"
        const val KEY_DRAFT_CONTENT = "draft_content"
    }
}

sealed interface NoteEditUiState {
    data object Loading : NoteEditUiState
    data class Editing(
        val note: Note,
        val isSaved: Boolean
    ) : NoteEditUiState
}
