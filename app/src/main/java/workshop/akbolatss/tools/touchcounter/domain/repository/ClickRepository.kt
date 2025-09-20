package workshop.akbolatss.tools.touchcounter.domain.repository

import androidx.lifecycle.LiveData
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto

interface ClickRepository {

    suspend fun createClick(clickObject: ClickDto)
    suspend fun remove(id: Long)

    suspend fun getAllClicks(): Int
    suspend fun getLongestClick(): Long
    suspend fun getShortestClick(): Long
    suspend fun getMostClicksInCounter(): Int

    fun findClickList(counterId: Long): LiveData<List<ClickDto>>
    suspend fun removeAll(counterId: Long)
    suspend fun deleteClicksByCounterIds(counterIds: List<Long>)
}
