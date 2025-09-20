package workshop.akbolatss.tools.touchcounter.data.repository

import androidx.lifecycle.LiveData
import javax.inject.Inject
import workshop.akbolatss.tools.touchcounter.data.dao.CounterDao
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository

class CounterRepositoryImpl
@Inject
constructor(private val counterDao: CounterDao) : CounterRepository {

    override suspend fun createCounter(counterDto: CounterDto) {
        counterDao.create(counterDto)
    }

    override suspend fun updateCounter(counterDto: CounterDto) {
        counterDao.update(counterDto)
    }

    override suspend fun deleteCounter(counter: CounterDto) {
        counterDao.delete(counter)
    }

    override suspend fun getCountersCount(): Int =
        counterDao.getCount() ?: 0

    override fun findCounters(): LiveData<List<CounterDto>> {
        return counterDao.findList()
    }

    override fun findCounter(id: Long): LiveData<CounterDto> {
        return counterDao.findBy(id)
    }

    override suspend fun deleteCounters(ids: List<Long>) {
        counterDao.deleteCounters(ids)
    }
}
