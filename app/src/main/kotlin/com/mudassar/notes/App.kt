package com.mudassar.notes

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.mudassar.notes.sync.ScheduleSync
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var scheduleSync: ScheduleSync

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        WorkManager.initialize(this, workManagerConfiguration)
        // schedule any pending notes
        applicationScope.launch { scheduleSync.schedule() }
    }

}
