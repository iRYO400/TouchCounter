package workshop.akbolatss.tools.touchcounter.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject

@Database(
    entities = [(CounterObject::class), (ClickObject::class)],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract val dataDao: DataDao

    companion object {

        private const val DB_NAME = "YourCounters"

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        AppDataBase::class.java,
                        DB_NAME
                    )
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}