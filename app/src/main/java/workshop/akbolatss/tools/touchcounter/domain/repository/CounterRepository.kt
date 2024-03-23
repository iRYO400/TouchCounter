package workshop.akbolatss.tools.touchcounter.domain.repository

import androidx.lifecycle.LiveData
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto

interface CounterRepository {

    suspend fun createCounter(counterDto: CounterDto)
    suspend fun updateCounter(counterDto: CounterDto)
    suspend fun deleteCounter(counter: CounterDto)

    suspend fun getCountersCount(): Int

    fun findCounters(): LiveData<List<CounterDto>>
    fun findCounter(id: Long): LiveData<CounterDto>
}
