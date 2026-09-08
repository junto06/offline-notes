package com.mudassar.notes.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.mudassar.notes.repository.SessionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleSyncImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionRepository: SessionRepository,
) : ScheduleSync {

    override suspend fun schedule() {
        // don't trigger sync if not loggedIn
        if (sessionRepository.observeUser().first() == null) return

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncNotesWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS,
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "notes_sync_work",
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }
}
