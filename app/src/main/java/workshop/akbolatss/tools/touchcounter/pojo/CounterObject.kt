package workshop.akbolatss.tools.touchcounter.pojo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class CounterObject(
    var timestampCreating: Long,
    var timestampEditing: Long,
    var count: Long,
    var name: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Ignore
    var clickObjects: ArrayList<ClickObject> = ArrayList()
}