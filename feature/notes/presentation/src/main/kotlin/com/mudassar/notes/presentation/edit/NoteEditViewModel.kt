package com.mudassar.notes.presentation.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudassar.notes.navigation.Navigator
import com.mudassar.notes.navigation.popBackStack
import com.mudassar.notes.models.ConflictResolution
import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteId
import com.mudassar.notes.models.NoteStatus
import com.mudassar.notes.models.ResolveConflictResult
import com.mudassar.notes.presentation.edit.NoteEditUiState.Conflict
import com.mudassar.notes.presentation.edit.NoteEditUiState.Editing
import com.mudassar.notes.usecases.DeleteNoteUseCase
import com.mudassar.notes.usecases.LoadNoteForEditUseCase
import com.mudassar.notes.usecases.RefreshNoteUseCase
import com.mudassar.notes.usecases.ResolveConflictUseCase
import com.mudassar.notes.usecases.SaveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    loadNoteForEditUseCase: LoadNoteForEditUseCase,
    refreshNoteUseCase: RefreshNoteUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val resolveConflictUseCase: ResolveConflictUseCase,
    private val navigator: Navigator,
) : ViewModel() {

    private val noteId = getNoteId(savedStateHandle)

    private val _state = MutableStateFlow<NoteEditUiState>(NoteEditUiState.Loading)
    val state: StateFlow<NoteEditUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<NoteEditEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<NoteEditEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            loadNoteForEditUseCase(noteId)
                .collect { note ->
                    if (note == null) {
                        // deleted on another device, show error message and navigate back
                        _events.emit(NoteEditEvent.NoteDeletedRemotely)
                        navigator.popBackStack()
                    } else {
                        _state.update { current -> nextState(current, note) }
                    }
                }
        }
        // Show local content immediately & refreshes it in the background
        // if another device changed the note since it was last synced here
        noteId?.let { id ->
            viewModelScope.launch {
                refreshNoteUseCase(id)
            }
        }
    }

    private fun nextState(current: NoteEditUiState, note: Note): NoteEditUiState {
        if (note.status == NoteStatus.CONFLICT) return Conflict(note = note)

        // restore state only from SavedStateHandle if current state isn't already Editing
        val newNote = when (current) {
            is Editing -> note.copy(title = current.note.title, content = current.note.content)
            NoteEditUiState.Loading, is Conflict -> restoreDraft(note)
        }
        return Editing(note = newNote, isSaved = noteId != null)
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

    fun onKeepMineClicked() = resolveConflict(ConflictResolution.KEEP_MINE)

    fun onKeepRemoteClicked() = resolveConflict(ConflictResolution.KEEP_REMOTE)

    private fun resolveConflict(resolution: ConflictResolution) {
        val conflict = _state.value as? Conflict ?: return
        if (conflict.isResolving) return
        _state.value = conflict.copy(isResolving = true)

        viewModelScope.launch {
            when (val result = resolveConflictUseCase(conflict.note, resolution)) {
                is ResolveConflictResult.Resolved -> navigator.popBackStack()

                is ResolveConflictResult.StillConflicting -> _state.value = Conflict(
                    note = conflict.note.copy(
                        failureReason = result.reason,
                        conflictServerVersion = result.serverVersion,
                    ),
                    isResolving = false,
                )

                ResolveConflictResult.Failed -> _state.value = conflict.copy(isResolving = false)
            }
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

sealed interface NoteEditEvent {
    data object NoteDeletedRemotely : NoteEditEvent
}

sealed interface NoteEditUiState {
    data object Loading : NoteEditUiState
    data class Editing(
        val note: Note,
        val isSaved: Boolean
    ) : NoteEditUiState
    data class Conflict(
        val note: Note,
        val isResolving: Boolean = false,
    ) : NoteEditUiState
}
