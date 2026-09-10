package com.mudassar.notes.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mudassar.notes.repository.SessionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val FETCH_INTERVAL_MINUTES = 60L

class ScheduleFetchImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionRepository: SessionRepository,
) : ScheduleFetch {

    override suspend fun schedule() {
        // don't fetch if not loggedIn
        if (sessionRepository.observeUser().first() == null) return

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<FetchNotesWorker>(FETCH_INTERVAL_MINUTES, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        // UPDATE refreshes constraints without resetting an already-running schedule - schedule()
        // gets called on every login, so this shouldn't restart the 60-minute cycle each time.
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "notes_fetch_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }
}
