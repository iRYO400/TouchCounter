package workshop.akbolatss.tools.touchcounter.room

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject


class ClicksRepository(private val dataDao: DataDao) {

    fun getCounter(id: Long): LiveData<CounterObject> {
        return dataDao.counterObject(id)
    }

    suspend fun getCountersCount(): Int {
        return withContext(Dispatchers.IO) {
            dataDao.getCountersRowCount()
        }
    }

    fun getCounters(): LiveData<List<CounterObject>> {
        return dataDao.getCounterObjects()
    }

    fun getClicks(counterId: Long): LiveData<List<ClickObject>> {
        return dataDao.getClickObjects(counterId)
    }

    suspend fun saveCounter(counterObject: CounterObject) {
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

    suspend fun addClick(clickObject: ClickObject) {
        withContext(Dispatchers.IO) {
            dataDao.saveClickObject(clickObject)
        }
    }

    suspend fun updateCounter(counterObject: CounterObject) {
        withContext(Dispatchers.IO) {
            dataDao.updateCounter(counterObject)
        }
    }
}