package workshop.akbolatss.tools.touchcounter.data.repository

import androidx.lifecycle.LiveData
import javax.inject.Inject
import workshop.akbolatss.tools.touchcounter.data.dao.ClickDao
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository

class ClickRepositoryImpl
@Inject
constructor(private val clickDao: ClickDao) : ClickRepository {

    override suspend fun createClick(clickObject: ClickDto) {
        clickDao.create(clickObject)
    }

    override suspend fun remove(id: Long) {
        clickDao.remove(id)
    }

    override suspend fun getAllClicks(): Int =
        clickDao.getCount() ?: 0

    override suspend fun getLongestClick(): Long =
        clickDao.getLongest() ?: 0

    override suspend fun getMostClicksInCounter(): Int =
        clickDao.getMostClicksInCounter() ?: 0

    override fun findClickList(counterId: Long): LiveData<List<ClickDto>> {
        return clickDao.findListBy(counterId)
    }

    override suspend fun removeAll(counterId: Long) {
        clickDao.removeAll(counterId)
    }
}
