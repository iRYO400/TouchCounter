package workshop.akbolatss.tools.touchcounter.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import workshop.akbolatss.tools.touchcounter.data.dao.ClickDao
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.data.dto.ClickStatsDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.utils.INITIAL
import javax.inject.Inject

class ClickRepositoryImpl @Inject constructor(
    private val clickDao: ClickDao
) : ClickRepository {

    private val _longestStatsCache = MutableSharedFlow<CachedClickStats>(replay = 1)
    private val _shortestStatsCache = MutableSharedFlow<CachedClickStats>(replay = 1)

    override suspend fun createClick(clickObject: ClickDto) {
        clickDao.create(clickObject)

        invalidateCache(clickObject)
    }

    private suspend fun invalidateCache(clickObject: ClickDto) {
        _longestStatsCache.replayCache.firstOrNull().let { cachedStats ->
            if (cachedStats == null ||
                cachedStats.counterId != clickObject.counterId ||
                clickObject.heldMillis > cachedStats.stats.heldMillis
            ) {
                emitLongestStats(clickObject.counterId)
            }
        }

        _shortestStatsCache.replayCache.firstOrNull().let { cachedStats ->
            if (cachedStats == null ||
                cachedStats.counterId != clickObject.counterId ||
                cachedStats.stats.heldMillis == INITIAL ||
                clickObject.heldMillis < cachedStats.stats.heldMillis
            ) {
                emitShortestStats(clickObject.counterId)
            }
        }
    }

    override suspend fun getAllClicks(): Int =
        clickDao.getCount() ?: 0

    override suspend fun getMostClicksInCounter(): Int =
        clickDao.getMostClicksInCounter() ?: 0

    override suspend fun getLongestClick(): Long =
        clickDao.getLongest() ?: 0

    override suspend fun getShortestClick(): Long =
        clickDao.getShortest() ?: 0

    override fun getLongestClick(counterId: Long): Flow<ClickStatsDto?> {
        return _longestStatsCache
            .filter { cached -> cached.counterId == counterId }
            .map { cached -> cached.stats }
            .onStart { emitLongestStats(counterId) }
    }

    private suspend fun emitLongestStats(counterId: Long) {
        val stats = clickDao.getLongestClick(counterId) ?: ClickStatsDto.empty()
        _longestStatsCache.emit(CachedClickStats(counterId, stats))
    }

    override fun getShortestClick(counterId: Long): Flow<ClickStatsDto?> {
        return _shortestStatsCache
            .filter { cached -> cached.counterId == counterId }
            .map { cached -> cached.stats }
            .onStart { emitShortestStats(counterId) }
    }

    private suspend fun emitShortestStats(counterId: Long) {
        val stats = clickDao.getShortestClick(counterId) ?: ClickStatsDto.empty()
        _shortestStatsCache.emit(CachedClickStats(counterId, stats))
    }

    override fun findClickList(counterId: Long): LiveData<List<ClickDto>> {
        return clickDao.findListBy(counterId)
    }

    override suspend fun removeBy(clickDto: ClickDto) {
        clickDao.removeBy(clickDto.id)

        emitShortestStats(clickDto.counterId)
        emitLongestStats(clickDto.counterId)
    }

    override suspend fun removeBy(counterId: Long) {
        clickDao.removeAllBy(counterId)

        emitShortestStats(counterId)
        emitLongestStats(counterId)
    }

    override suspend fun removeBy(counterIds: List<Long>) {
        clickDao.removeAllBy(counterIds)
    }
}
