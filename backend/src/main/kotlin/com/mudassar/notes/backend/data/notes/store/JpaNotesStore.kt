package com.mudassar.notes.backend.data.notes.store

import com.mudassar.notes.backend.data.notes.NotesStore
import com.mudassar.notes.backend.domain.model.NoteSync
import com.mudassar.notes.backend.domain.model.SyncOperation
import com.mudassar.notes.backend.domain.model.UserId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class JpaNotesStore(
    private val noteJpaRepository: NoteJpaRepository,
) : NotesStore {

    @Transactional
    override fun <R> update(userId: String, id: String, update: (NoteSync?) -> NotesStore.Update<R>): R {
        val existing = noteJpaRepository.findForUpdate(userId, id)
        val (newValue, result) = update(existing?.toDomain())
        when {
            newValue == null -> existing?.let(noteJpaRepository::delete)
            else -> noteJpaRepository.save(newValue.toEntity())
        }
        return result
    }

    override fun getAll(userId: String): List<NoteSync> =
        noteJpaRepository.findAllByUserId(userId).map { it.toDomain() }

    override fun get(userId: String, id: String): NoteSync? =
        noteJpaRepository.findByUserIdAndId(userId, id)?.toDomain()
}

private fun NoteEntity.toDomain(): NoteSync = NoteSync(
    id = id,
    userId = UserId(userId),
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    operation = SyncOperation.UPDATE,
    version = version,
)

private fun NoteSync.toEntity(): NoteEntity = NoteEntity(
    id = id,
    userId = userId.value,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    version = version,
)
