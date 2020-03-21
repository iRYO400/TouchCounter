package workshop.akbolatss.tools.touchcounter.domain.repository

import androidx.lifecycle.LiveData
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject

interface ClickRepository {

    suspend fun getCountersCount(): Int
    fun getCounters(): LiveData<List<CounterObject>>
    fun getClicks(counterId: Long): LiveData<List<ClickObject>>
    suspend fun saveCounter(counterObject: CounterObject)
    suspend fun addClick(clickObject: ClickObject)
    suspend fun updateCounter(counterObject: CounterObject)
    suspend fun deleteCounter(counter: CounterObject)
    suspend fun getAllClicks(): Int
    suspend fun getLongestClick(): Long
    suspend fun getMostClicksInCounter(): Int
}