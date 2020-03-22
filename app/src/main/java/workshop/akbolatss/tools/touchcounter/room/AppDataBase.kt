package workshop.akbolatss.tools.touchcounter.room

import androidx.room.Database
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

}