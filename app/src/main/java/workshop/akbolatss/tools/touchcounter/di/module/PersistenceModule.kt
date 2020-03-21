package workshop.akbolatss.tools.touchcounter.di.module

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import workshop.akbolatss.tools.touchcounter.room.AppDataBase
import workshop.akbolatss.tools.touchcounter.room.DataDao
import javax.inject.Singleton

@Module
class PersistenceModule {

    companion object {
        const val DATABASE_NAME = "YourCounters"
    }

    @Singleton
    @Provides
    fun provideAppDataBase(application: Application): AppDataBase {
        return Room.databaseBuilder(
                application.applicationContext,
                AppDataBase::class.java,
                DATABASE_NAME
            )
            .build()
    }


    @Singleton
    @Provides
    fun provideDao(appDataBase: AppDataBase): DataDao =
        appDataBase.dataDao
}

