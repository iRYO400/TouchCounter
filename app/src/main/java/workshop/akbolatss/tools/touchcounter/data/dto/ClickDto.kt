package workshop.akbolatss.tools.touchcounter.data.dto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "click",
    foreignKeys = [ForeignKey(
        entity = CounterDto::class,
        parentColumns = ["id"],
        childColumns = ["counterId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ClickDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val createTime: Date,
    val heldMillis: Long,
    val counterId: Long
)
