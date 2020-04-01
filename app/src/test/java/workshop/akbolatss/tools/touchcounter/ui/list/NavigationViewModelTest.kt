package workshop.akbolatss.tools.touchcounter.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository
import workshop.akbolatss.tools.touchcounter.utils.init
import java.util.Date

@ExperimentalCoroutinesApi
class NavigationViewModelTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val counterRepository: CounterRepository = mock()
    private val clickRepository: ClickRepository = mock()

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
        Dispatchers.resetMain()
    }

    @Test
    fun getStatsLiveData() {
    }

    @Test
    fun `get counters at init, when result is null, return empty`() {
        // given
        val expected = MutableLiveData<List<CounterDto>>().init(null)
        whenever(counterRepository.findCounters()).thenReturn(expected)

        // when
        val viewModel = NavigationViewModel(counterRepository, clickRepository)

        // then
        viewModel.counterList.test()
            .assertNullValue()

        verify(counterRepository, times(1)).findCounters()
        verifyZeroInteractions(clickRepository)
    }

    @Test
    fun `get counters at init, when result is empty, return empty`() {
        // given
        val expected = MutableLiveData<List<CounterDto>>().init(emptyList())
        whenever(counterRepository.findCounters()).thenReturn(expected)

        // when
        val viewModel = NavigationViewModel(counterRepository, clickRepository)

        // then
        viewModel.counterList.test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue {
                it.isEmpty()
            }

        verify(counterRepository, times(1)).findCounters()
        verifyZeroInteractions(clickRepository)
    }

    @Test
    fun `get counters at init, when result has value , return expected`() {
        // given
        val expected = MutableLiveData<List<CounterDto>>().init(listOf(getFakeCounter()))
        whenever(counterRepository.findCounters()).thenReturn(expected)

        // when
        val viewModel = NavigationViewModel(counterRepository, clickRepository)

        // then
        viewModel.counterList.test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue {
                it.isNotEmpty()
            }

        verify(counterRepository, times(1)).findCounters()
        verifyZeroInteractions(clickRepository)
    }

//    @Test(expected = NullPointerException::class) TODO wait for https://github.com/Kotlin/kotlinx.coroutines/issues/1205
//    fun `load stats, when result is null, throw exception`() = runBlockingTest {
//        // given
//        `when`(counterRepository.getCountersCount()).thenReturn(null)
//        `when`(clickRepository.getAllClicks()).thenReturn(null)
//        `when`(clickRepository.getLongestClick()).thenReturn(null)
//        `when`(clickRepository.getMostClicksInCounter()).thenReturn(null)
//
//        // when
//        viewModel.loadStats()
//
//        // then
// //        viewModel.statsLiveData.test()
// //            .assertNoValue()
// //            .assertHistorySize(0)
//    }

    @Test
    fun `load stats, when result is expected, then return expected`() = runBlockingTest {
        // given
        val expectedCountersCount = 10
        val expectedClicks = 20
        val expectedLongestClick = 2500L
        val expectedMostClicks = 35
        whenever(counterRepository.getCountersCount()).thenReturn(expectedCountersCount)
        whenever(clickRepository.getAllClicks()).thenReturn(expectedClicks)
        whenever(clickRepository.getLongestClick()).thenReturn(expectedLongestClick)
        whenever(clickRepository.getMostClicksInCounter()).thenReturn(expectedMostClicks)
        val viewModel = NavigationViewModel(counterRepository, clickRepository)

        // when
        viewModel.loadStats()

        // then
        verify(counterRepository, times(1)).getCountersCount()
        verify(clickRepository, times(1)).getAllClicks()
        verify(clickRepository, times(1)).getLongestClick()
        verify(clickRepository, times(1)).getMostClicksInCounter()
        viewModel.statsLiveData.test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue { stats ->
                stats.countersCount == expectedCountersCount &&
                    stats.clicksCount == expectedClicks &&
                    stats.longClick == expectedLongestClick &&
                    stats.mostClicks == expectedMostClicks
            }
    }

    @Test
    fun `create counter`() = runBlockingTest {
        // given
        val counterName = "Test"
        val counter = CounterDto(createTime = Date(), editTime = Date(), name = counterName)
        whenever(counterRepository.getCountersCount()).thenReturn(0)
        whenever(counterRepository.createCounter(counter)).thenReturn(Unit)
        val viewModel = NavigationViewModel(counterRepository, clickRepository)

        // when
        viewModel.createCounter(counterName)

        // then
        verify(counterRepository, times(1)).getCountersCount()
        verify(counterRepository, times(1)).createCounter(any())
    }

    @Test
    fun deleteCounter() = runBlockingTest {
        // given
        val counter = getFakeCounter()
        whenever(counterRepository.deleteCounter(counter)).thenReturn(Unit)
        val viewModel = NavigationViewModel(counterRepository, clickRepository)

        // when
        viewModel.deleteCounter(counter)

        // then
        verify(counterRepository, times(1)).deleteCounter(counter)
    }

    @Test
    fun `update counter`() = runBlockingTest {
        // given
        val counter = getFakeCounter()
        whenever(counterRepository.updateCounter(any())).thenReturn(Unit)
        val viewModel = NavigationViewModel(counterRepository, clickRepository)

        // when
        viewModel.updateCounter(counter)

        // then
        verify(counterRepository, times(1)).updateCounter(counter)
    }

    private fun getFakeCounter(name: String = "Test"): CounterDto =
        CounterDto(createTime = Date(), editTime = Date(), name = name)
}