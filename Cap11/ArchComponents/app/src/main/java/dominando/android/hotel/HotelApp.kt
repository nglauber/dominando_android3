package dominando.android.hotel

import android.app.Application
import dominando.android.hotel.di.androidModule
import org.koin.android.ext.android.startKoin
import org.koin.standalone.StandAloneContext.stopKoin

class HotelApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(androidModule))
    }
    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}
