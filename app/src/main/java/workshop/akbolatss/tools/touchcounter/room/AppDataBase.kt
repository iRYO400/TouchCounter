package workshop.akbolatss.tools.touchcounter.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import workshop.akbolatss.tools.touchcounter.data.dao.ClickDao
import workshop.akbolatss.tools.touchcounter.data.dao.CounterDao
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto

@Database(
    entities = [(CounterDto::class), (ClickDto::class)],
    version = 2,
    exportSchema = true
)
@TypeConverters(
    RoomConverters::class
)
abstract class AppDataBase : RoomDatabase() {

    abstract val counterDao: CounterDao

    abstract val clickDao: ClickDao
}
