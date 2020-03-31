package workshop.akbolatss.tools.touchcounter.data.dto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "click"
)
data class ClickDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val createTime: Date,
    val heldMillis: Long,
    @ForeignKey(
        entity = CounterDto::class,
        parentColumns = ["id"],
        childColumns = ["counterId"],
        onDelete = CASCADE
    )
    val counterId: Long
)