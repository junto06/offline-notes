package com.mudassar.notes.backend.data.notes.store

import com.mudassar.notes.backend.data.notes.NotesStore
import com.mudassar.notes.backend.domain.model.NoteSync
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryNotesStore : NotesStore {
    // One bucket per user
    private val notesByUser =
        ConcurrentHashMap<String, ConcurrentHashMap<String, NoteSync>>()

    override fun <R> update(userId: String, id: String, update: (NoteSync?) -> NotesStore.Update<R>): R {
        val bucket = notesFor(userId)
        synchronized(bucket) {
            val (newValue, result) = update(bucket[id])
            if (newValue == null) bucket.remove(id) else bucket[id] = newValue
            return result
        }
    }

    override fun getAll(userId: String): List<NoteSync> = notesFor(userId).values.toList()

    override fun get(userId: String, id: String): NoteSync? = notesFor(userId)[id]

    private fun notesFor(userId: String): ConcurrentHashMap<String, NoteSync> =
        notesByUser.computeIfAbsent(userId) { ConcurrentHashMap() }
}
