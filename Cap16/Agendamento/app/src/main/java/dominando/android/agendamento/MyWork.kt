package dominando.android.agendamento

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWork (context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {
    override fun doWork(): Result {
        val firstName = inputData.getString(PARAM_FIRST_NAME)
        val outputData = Data.Builder()
            .putString(PARAM_NAME, "$firstName Glauber")
            .putInt(PARAM_AGE, 35)
            .putLong(PARAM_TIME, System.currentTimeMillis())
            .build()
        return Result.success(outputData)
    }
    companion object {
        const val PARAM_FIRST_NAME = "first_name"
        const val PARAM_NAME = "name"
        const val PARAM_AGE = "age"
        const val PARAM_TIME = "time"
    }
}
