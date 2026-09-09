package com.mudassar.notes.backend.data.notes

import com.mudassar.notes.backend.domain.model.NoteSync

interface NotesStore {
    // Atomically applies [update] to the current value for [id] belonging to [userId]
    // (null if absent), replacing it with [Update.newValue] and returning [Update.result]
    // to the caller.
    fun <R> update(userId: String, id: String, update: (NoteSync?) -> Update<R>): R

    fun getAll(userId: String): List<NoteSync>

    fun get(userId: String, id: String): NoteSync?

    // What an [update] callback hands back: [newValue] (null removes)
    // replaces the stored value, and [result] is returned to the [update] function's caller
    data class Update<R>(
        val newValue: NoteSync?,
        val result: R
    )
}
