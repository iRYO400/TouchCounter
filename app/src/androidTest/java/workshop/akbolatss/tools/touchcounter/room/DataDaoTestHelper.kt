package workshop.akbolatss.tools.touchcounter.room

import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
import java.util.*

val DEFAULT_DATE_TIMESTAMP = Date(0).time
const val DEFAULT_ID = 0L

const val COUNTER_ID_1 = 1L
const val CLICK_ID_1 = 1L

fun getDefaultCounter(
    creatingTimestamp: Long = DEFAULT_DATE_TIMESTAMP,
    editingTimestamp: Long = DEFAULT_DATE_TIMESTAMP,
    count: Long = 0,
    name: String = "TestCounter",
    id: Long
): CounterObject {
    val counter = CounterObject(
        timestampCreating = creatingTimestamp,
        timestampEditing = editingTimestamp,
        count = count,
        name = name
    )
    counter.id = id
    return counter
}

fun getDefaultClick(
    timestamp: Long = DEFAULT_DATE_TIMESTAMP,
    holdTiming: Long = 1000,
    index: Int = 0,
    id: Long,
    counterId: Long = DEFAULT_ID
): ClickObject {
    val click = ClickObject(
        timestamp = timestamp,
        holdTiming = holdTiming,
        index = index
    )
    click.id = id
    click.counterId = counterId
    return click
}