package workshop.akbolatss.tools.touchcounter.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import workshop.akbolatss.tools.touchcounter.data.dao.ClickDao
import workshop.akbolatss.tools.touchcounter.data.dao.CounterDao
import workshop.akbolatss.tools.touchcounter.room.AppDataBase
import javax.inject.Singleton

@Module
class PersistenceModule {

    companion object {
        const val SHARED_PREFERENCES = "YourCountersPreferences"
        const val DATABASE_NAME = "YourCounters.db"
    }

    @Singleton
    @Provides
    fun provideAppDataBase(application: Application): AppDataBase =
        Room.databaseBuilder(
            application.applicationContext,
            AppDataBase::class.java,
            DATABASE_NAME
        )
            .createFromAsset("databases/your_counters.db")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideCounterDao(appDataBase: AppDataBase): CounterDao =
        appDataBase.counterDao

    @Singleton
    @Provides
    fun provideClickDao(appDataBase: AppDataBase): ClickDao =
        appDataBase.clickDao

    @Singleton
    @Provides
    fun provideSharedPreference(application: Application): SharedPreferences =
        application.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
}