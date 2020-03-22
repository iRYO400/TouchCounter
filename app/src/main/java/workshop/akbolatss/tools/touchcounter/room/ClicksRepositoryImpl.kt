package workshop.akbolatss.tools.touchcounter.room

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
import javax.inject.Inject


class ClicksRepositoryImpl
@Inject
constructor(private val dataDao: DataDao) : ClickRepository {

    override fun getCounter(id: Long): LiveData<CounterObject> {
        return dataDao.getCounterObject(id)
    }

    override suspend fun getCountersCount(): Int {
        return withContext(Dispatchers.IO) {
            dataDao.getCountersRowCount()
        }
    }

    override fun getCounters(): LiveData<List<CounterObject>> {
        return dataDao.getCounterObjects()
    }

    override fun getClicks(counterId: Long): LiveData<List<ClickObject>> {
        return dataDao.getClickObjects(counterId)
    }

    override suspend fun saveCounter(counterObject: CounterObject) {
        withContext(Dispatchers.IO) {
            val counterId = dataDao.saveCounter(counterObject)
            saveClickObjects(counterObject, counterId)
        }
    }

    private suspend fun saveClickObjects(
        counterObject: CounterObject,
        counterId: Long
    ) {
        withContext(Dispatchers.IO) {
            for (click in counterObject.clickObjects) {
                click.counterId = counterId
            }
            dataDao.saveClickObjects(counterObject.clickObjects)
        }
    }

    override suspend fun addClick(clickObject: ClickObject) {
        withContext(Dispatchers.IO) {
            dataDao.saveClickObject(clickObject)
        }
    }

    override suspend fun updateCounter(counterObject: CounterObject) {
        withContext(Dispatchers.IO) {
            dataDao.updateCounter(counterObject)
        }
    }

    override suspend fun deleteCounter(counter: CounterObject) {
        withContext(Dispatchers.IO) {
            dataDao.deleteCounter(counter)
            dataDao.deleteClicks(counter.id)
        }
    }

    override suspend fun getAllClicks(): Int {
        return withContext(Dispatchers.IO) {
            dataDao.getAllClicksCount()
        }
    }

    override suspend fun getLongestClick(): Long {
        return withContext(Dispatchers.IO) {
            dataDao.getLongestClick()
        }
    }

    override suspend fun getMostClicksInCounter(): Int {
        return withContext(Dispatchers.IO) {
            dataDao.getMostClicksInCounter()
        }
    }
}