package workshop.akbolatss.tools.touchcounter.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "counter"
)
data class CounterDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val createTime: Date,
    val editTime: Date,
    val name: String
) {
    var itemCount: Int = 0
}