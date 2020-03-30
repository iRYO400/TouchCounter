package workshop.akbolatss.tools.touchcounter.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository
import workshop.akbolatss.tools.touchcounter.utils.init
import java.util.*

class NavigationViewModelTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val counterRepository = mock(CounterRepository::class.java)
    private val clickRepository = mock(ClickRepository::class.java)

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
        `when`(counterRepository.findCounters()).thenReturn(expected)

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
        `when`(counterRepository.findCounters()).thenReturn(expected)

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
        `when`(counterRepository.findCounters()).thenReturn(expected)

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
////        viewModel.statsLiveData.test()
////            .assertNoValue()
////            .assertHistorySize(0)
//    }


    @Test
    fun `load stats, when result is expected, then return expected`() = runBlockingTest {
        // given
        val expectedCountersCount = 10
        val expectedClicks = 20
        val expectedLongestClick = 2500L
        val expectedMostClicks = 35
        `when`(counterRepository.getCountersCount()).thenReturn(expectedCountersCount)
        `when`(clickRepository.getAllClicks()).thenReturn(expectedClicks)
        `when`(clickRepository.getLongestClick()).thenReturn(expectedLongestClick)
        `when`(clickRepository.getMostClicksInCounter()).thenReturn(expectedMostClicks)
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
                stats.countersCount == expectedCountersCount
                        && stats.clicksCount == expectedClicks
                        && stats.longClick == expectedLongestClick
                        && stats.mostClicks == expectedMostClicks
            }
    }

    @Test
    fun `create counter`() = runBlockingTest {
        // given
        val counterName = "Test"
        val counter = CounterDto(createTime = Date(), editTime = Date(), name = counterName)
        `when`(counterRepository.getCountersCount()).thenReturn(0)
        `when`(counterRepository.createCounter(counter)).thenReturn(Unit)
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
        `when`(counterRepository.deleteCounter(counter)).thenReturn(Unit)
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
        `when`(counterRepository.updateCounter(any())).thenReturn(Unit)
        val viewModel = NavigationViewModel(counterRepository, clickRepository)

        // when
        viewModel.updateCounter(counter)

        // then
        verify(counterRepository, times(1)).updateCounter(counter)
    }

    private fun getFakeCounter(name: String = "Test"): CounterDto =
        CounterDto(createTime = Date(), editTime = Date(), name = name)

}