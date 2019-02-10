package workshop.akbolatss.tools.touchcounter

import android.app.Application
import androidx.room.Room
import workshop.akbolatss.tools.touchcounter.room.AppDataBase

class ApplicationMain : Application() {

    lateinit var appDatabase: AppDataBase

    companion object {
        const val DATABASE_ID = "YourCounters"

        lateinit var instance: ApplicationMain
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        appDatabase = Room.databaseBuilder(instance, AppDataBase::class.java, DATABASE_ID)
            .build()
    }
}