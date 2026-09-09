package com.mudassar.notes.auth.repository

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.mudassar.notes.base.ErrorLogger
import com.mudassar.notes.models.User
import com.mudassar.notes.models.UserId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserSessionSerializer @Inject constructor(
    private val errorLogger: ErrorLogger,
) : Serializer<User?> {

    override val defaultValue: User? = null

    override suspend fun readFrom(input: InputStream): User? {
        val bytes = withContext(Dispatchers.IO) { input.readBytes() }
        if (bytes.isEmpty()) return null
        return try {
            Json.decodeFromString<UserSnapshot>(bytes.decodeToString()).toUser()
        } catch (e: SerializationException) {
            errorLogger.logError(e, "Corrupted user session data")
            throw CorruptionException("Unable to read User session", e)
        }
    }

    override suspend fun writeTo(t: User?, output: OutputStream) {
        val snapshot = t?.toSnapshot() ?: return
        withContext(Dispatchers.IO) {
            val bytes = Json.encodeToString(UserSnapshot.serializer(), snapshot).encodeToByteArray()
            output.write(bytes)
        }
    }
}

@Serializable
private data class UserSnapshot(
    val id: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String,
)

private fun User.toSnapshot() = UserSnapshot(
    id = id.value,
    name = name,
    accessToken = accessToken,
    refreshToken = refreshToken,
)

private fun UserSnapshot.toUser() = User(
    id = UserId(id),
    name = name,
    accessToken = accessToken,
    refreshToken = refreshToken,
)
