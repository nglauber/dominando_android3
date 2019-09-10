package dominando.android.hotel.repository.http

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class HotelSyncWorker(context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams), KoinComponent {
    override fun doWork(): Result {
        val hotelHttp: HotelHttp by inject()
        return try {
            hotelHttp.synchronizeWithServer()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    companion object {
        fun start(): LiveData<WorkInfo> {
            val workManager = WorkManager.getInstance()
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequest.Builder(HotelSyncWorker::class.java)
                .setConstraints(constraints)
                .build()
            workManager.enqueue(request)
            return workManager.getWorkInfoByIdLiveData(request.id)
        }
    }
}
