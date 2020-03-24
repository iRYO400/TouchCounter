package workshop.akbolatss.tools.touchcounter.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.jraska.livedata.test
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import workshop.akbolatss.tools.touchcounter.data.dao.CounterDao
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository
import workshop.akbolatss.tools.touchcounter.utils.init
import java.util.*

class CounterRepositoryImplTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val counterDao: CounterDao = mock(CounterDao::class.java)

    private lateinit var repository: CounterRepository

    @Before
    fun setUp() {
        repository = CounterRepositoryImpl(counterDao)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `create counter`() = runBlockingTest {
        // given
        val counter = getFakeCounterA()
        val repository = CounterRepositoryImpl(counterDao)

        // when
        `when`(counterDao.create(counter)).thenReturn(0)
        repository.createCounter(counter)

        // then
        verify(counterDao, times(1)).create(counter)
    }

    @Test
    fun `create counter which is not modified`() = runBlockingTest {
        // given
        val counterA = getFakeCounterA()
        val counterB = getFakeCounterB()
        val repository = CounterRepositoryImpl(counterDao)

        // when
        `when`(counterDao.create(counterA)).thenReturn(0)
        repository.createCounter(counterA)

        // then
        verify(counterDao, times(1)).create(counterA)
        verify(counterDao, never()).create(counterB)
    }

    @Test
    fun `update counter`() = runBlockingTest {
        // given
        val counter = getFakeCounterA()
        val repository = CounterRepositoryImpl(counterDao)

        // when
        `when`(counterDao.update(counter)).thenReturn(Unit)
        repository.updateCounter(counter)

        // then
        verify(counterDao, times(1)).update(counter)
    }

    @Test
    fun `update counter which is not modified`() = runBlockingTest {
        // given
        val counterA = getFakeCounterA()
        val counterB = getFakeCounterB()

        // when
        `when`(counterDao.update(counterA)).thenReturn(Unit)
        repository.updateCounter(counterA)

        // then
        verify(counterDao, times(1)).update(counterA)
        verify(counterDao, never()).update(counterB)
    }

    @Test
    fun `delete counter exact`() = runBlockingTest {
        // given
        val counter = getFakeCounterA()

        // when
        `when`(counterDao.delete(counter)).thenReturn(Unit)
        repository.deleteCounter(counter)

        // then
        verify(counterDao, times(1)).delete(counter)
    }

    @Test
    fun `delete counter not different`() = runBlockingTest {
        // given
        val counterA = getFakeCounterA()
        val counterB = getFakeCounterB()

        // when
        `when`(counterDao.delete(counterA)).thenReturn(Unit)
        repository.deleteCounter(counterA)

        // then
        verify(counterDao, times(1)).delete(counterA)
        verify(counterDao, never()).delete(counterB)
    }

    @Test
    fun `get counters when not null return expected`() = runBlockingTest {
        // when
        `when`(counterDao.getCount()).thenReturn(10)
        val actual = repository.getCountersCount()

        // then
        verify(counterDao, times(1)).getCount()
        assertThat(actual).isNotNull()
        assertThat(actual).isEqualTo(10)
    }

    @Test
    fun `get counters when null return default value`() = runBlockingTest {
        // when
        `when`(counterDao.getCount()).thenReturn(null)
        val actual = repository.getCountersCount()

        // then
        verify(counterDao, times(1)).getCount()
        assertThat(actual).isNotNull()
        assertThat(actual).isEqualTo(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `find counters, when data source gives null, throws exception`() {
        // when
        `when`(counterDao.findList()).thenReturn(null)

        // then
        repository.findCounters().test()
        verify(counterDao.findList())
    }

    @Test
    fun `find counters, when data source gives null value, return null`() {
        // given
        val expectedResponse = MutableLiveData<List<CounterDto>>().init(null)

        // when
        `when`(counterDao.findList()).thenReturn(expectedResponse)

        // then
        repository.findCounters().test()
            .assertNullValue()
            .assertHistorySize(1)
        verify(counterDao).findList()
    }

    @Test
    fun `find counters, when data source is empty, return empty list`() {
        // given
        val expectedResponse = MutableLiveData<List<CounterDto>>().init(emptyList())

        // when
        `when`(counterDao.findList()).thenReturn(expectedResponse)

        // then
        repository.findCounters().test()
            .assertHasValue()
            .assertValue { it.isEmpty() }
            .assertHistorySize(1)
        verify(counterDao).findList()
    }

    @Test
    fun `find counters, when data source has 1 item, return list with one element`() {
        // given
        val expectedResponse = MutableLiveData<List<CounterDto>>().init(listOf(getFakeCounterA()))

        // when
        `when`(counterDao.findList()).thenReturn(expectedResponse)

        // then
        repository.findCounters().test()
            .assertHasValue()
            .assertValue { it.isNotEmpty() }
            .assertValue { it.size == 1 }
            .assertHistorySize(1)
        verify(counterDao).findList()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `find counter, when data source gives null, throw exception `() {
        // when
        `when`(counterDao.findBy(0)).thenReturn(null)

        //then
        repository.findCounter(0).test()
    }

    @Test
    fun `find counter, when data source gives null value, return null `() {
        // given
        val expectedResponse = MutableLiveData<CounterDto>().init(null)

        // when
        `when`(counterDao.findBy(0)).thenReturn(expectedResponse)

        //then
        repository.findCounter(0).test()
            .assertNullValue()
            .assertHistorySize(1)
        verify(counterDao, times(1)).findBy(0)
        verify(counterDao, never()).findBy(-1)
    }

    @Test
    fun `find counter, when data source gives item, return that item`() {
        // given
        val counter = getFakeCounterA()
        val expectedResponse = MutableLiveData<CounterDto>().init(counter)

        // when
        `when`(counterDao.findBy(0)).thenReturn(expectedResponse)

        //then
        repository.findCounter(0).test()
            .assertHasValue()
            .assertValue {
                it == counter
            }
            .assertHistorySize(1)
        verify(counterDao, times(1)).findBy(0)
        verify(counterDao, never()).findBy(-1)
    }

    private fun getFakeCounterA(): CounterDto =
        CounterDto(createTime = Date(), editTime = Date(), name = "Test1")

    private fun getFakeCounterB(): CounterDto =
        CounterDto(createTime = Date(), editTime = Date(), name = "Test2")
}