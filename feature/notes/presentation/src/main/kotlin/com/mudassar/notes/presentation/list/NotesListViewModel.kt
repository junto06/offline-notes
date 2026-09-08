package com.mudassar.notes.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudassar.notes.navigation.Navigator
import com.mudassar.notes.navigation.direction
import com.mudassar.notes.navigation.openDeeplink
import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteId
import com.mudassar.notes.usecases.ObserveNotesUseCase
import com.mudassar.notes.usecases.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    observeNotesUseCase: ObserveNotesUseCase,
    observeSessionUseCase: ObserveSessionUseCase,
    private val navigator: Navigator,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = observeSessionUseCase()
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val state: StateFlow<NotesListUiState> = combine(
        observeNotesUseCase(),
        _searchQuery,
    ) { notes, query ->
        val filtered = filterNotes(notes, query)
        when {
            notes.isEmpty() -> NotesListUiState.Empty
            filtered.isEmpty() -> NotesListUiState.NoResults
            else -> NotesListUiState.Success(NoteUiModelMapper.map(filtered))
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotesListUiState.Loading,
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onNoteClicked(id: NoteId) {
        navigator.direction(FragmentNotesListDirections.actionNotesListToNoteEdit(id.value))
    }

    fun onAddNoteClicked() {
        navigator.direction(FragmentNotesListDirections.actionNotesListToNoteEdit(null))
    }

    fun onLoginClicked() {
        navigator.openDeeplink("notes://login")
    }

    private fun filterNotes(notes: List<Note>, query: String): List<Note> {
        if (query.isEmpty()) return notes
        return notes.filter {
            it.title.contains(query, ignoreCase = true)
                    || it.content.contains(query, ignoreCase = true)
        }
    }
}

sealed interface NotesListUiState {
    object Loading : NotesListUiState
    object Empty : NotesListUiState
    object NoResults : NotesListUiState
    data class Success(val notes: List<NoteUiModel>) : NotesListUiState
}
