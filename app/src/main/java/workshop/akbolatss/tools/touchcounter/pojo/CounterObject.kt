package workshop.akbolatss.tools.touchcounter.pojo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class CounterObject(
    val timestampCreating: Long,
    var count: Long,
    val name: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Ignore
    var clickObjects: ArrayList<ClickObject> = ArrayList()
}