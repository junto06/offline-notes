package com.mudassar.notes.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mudassar.notes.usecases.FetchUserNotesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FetchNotesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val fetchUserNotesUseCase: FetchUserNotesUseCase,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return if (fetchUserNotesUseCase()) {
            Result.success()
        } else {
            Result.failure()
        }
    }
}
