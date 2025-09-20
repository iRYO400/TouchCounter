package workshop.akbolatss.tools.touchcounter.domain.repository

import androidx.lifecycle.LiveData
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import java.util.Date

interface CounterRepository {

    suspend fun createCounter(counterDto: CounterDto)
    suspend fun updateCounter(counterDto: CounterDto)
    suspend fun deleteCounter(counter: CounterDto)

    suspend fun deleteCounters(ids: List<Long>)
    suspend fun getCountersCount(): Int

    fun findCounters(): LiveData<List<CounterDto>>
    fun findCounter(id: Long): LiveData<CounterDto>

    suspend fun updateCounters(counterIds: List<Long>, editTime: Date)
}
