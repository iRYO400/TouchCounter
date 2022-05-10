package workshop.akbolatss.tools.touchcounter.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val oldTable = "click"
                val newTable = "${oldTable}_new"
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `$newTable` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`createTime` INTEGER NOT NULL, " +
                        "`heldMillis` INTEGER NOT NULL, " +
                        "`counterId` INTEGER NOT NULL, " +
                        "FOREIGN KEY(`counterId`) REFERENCES `counter`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                database.execSQL(
                    "INSERT INTO `$newTable` (id, createTime, heldMillis, counterId) " +
                        "SELECT id, createTime, heldMillis, counterId " +
                        "FROM $oldTable"
                )
                database.execSQL("DROP TABLE $oldTable")
                database.execSQL("ALTER TABLE $newTable RENAME to $oldTable")
            }
        }
    }

    @Singleton
    @Provides
    fun provideAppDataBase(application: Application): AppDataBase =
        Room.databaseBuilder(
            application.applicationContext,
            AppDataBase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .addMigrations(MIGRATION_1_2)
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
