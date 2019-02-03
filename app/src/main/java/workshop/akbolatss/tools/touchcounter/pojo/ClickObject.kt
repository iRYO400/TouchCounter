package workshop.akbolatss.tools.touchcounter.pojo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class ClickObject(
    val timestamp: Long,
    val holdTiming: Long,
    val index: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ForeignKey(entity = CounterObject::class, parentColumns = ["id"], childColumns = ["counterId"])
    var counterId: Long = 0
}