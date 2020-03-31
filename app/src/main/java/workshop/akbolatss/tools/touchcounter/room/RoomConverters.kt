package workshop.akbolatss.tools.touchcounter.room

import androidx.room.TypeConverter
import java.util.Date

class RoomConverters {

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        if (timestamp == null || timestamp == -1L)
            return null
        return Date(timestamp)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long {
        if (date == null)
            return -1
        return date.time
    }
}