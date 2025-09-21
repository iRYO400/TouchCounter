package workshop.akbolatss.tools.touchcounter.domain.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.data.dto.ClickStatsDto

interface ClickRepository {

    suspend fun createClick(clickObject: ClickDto)

    suspend fun getAllClicks(): Int
    suspend fun getMostClicksInCounter(): Int

    suspend fun getLongestClick(): Long
    suspend fun getShortestClick(): Long

    fun getLongestClick(counterId: Long): Flow<ClickStatsDto?>
    fun getShortestClick(counterId: Long): Flow<ClickStatsDto?>

    fun findClickList(counterId: Long): LiveData<List<ClickDto>>

    suspend fun removeBy(clickDto: ClickDto)
    suspend fun removeBy(counterId: Long)
    suspend fun removeBy(counterIds: List<Long>)
}
