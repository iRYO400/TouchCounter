package workshop.akbolatss.tools.touchcounter.ui.counter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import workshop.akbolatss.tools.touchcounter.data.dto.ClickStatsDto
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository
import workshop.akbolatss.tools.touchcounter.utils.exts.init
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class ClickListViewModelTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `set counterId, when called once, then changed once`() {
        // given
        val viewModel = getViewModel()
        val testObserver = viewModel.counterId.test()
            .assertNoValue()
        val counterId = 0L

        // when
        viewModel.initArguments(counterId, CLICK_COUNT)

        // then
        testObserver.assertHasValue()
            .assertHistorySize(1)
            .assertValue {
                it == counterId
            }
    }

    @Test
    fun `set counterId, when called twice with same counterId, then changed once`() {
        // given
        val viewModel = getViewModel()
        val testObserver = viewModel.counterId.test()
            .assertNoValue()
        val counterId = 0L

        // when
        viewModel.initArguments(counterId, CLICK_COUNT)

        testObserver.assertHasValue()
            .assertHistorySize(1)
            .assertValueHistory(counterId)

        viewModel.initArguments(counterId, CLICK_COUNT)

        // then
        viewModel.counterId.test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValueHistory(counterId)
    }

    @Test
    fun `set counterId, when called twice with diff counterId, then changed twice`() {
        // given
        val counterIdA = 0L
        val counterIdB = 101L
        val viewModel = getViewModel()
        val testObserver = viewModel.counterId.test()
            .assertNoValue()

        // when
        viewModel.initArguments(counterIdA, CLICK_COUNT)

        testObserver.assertHasValue()
            .assertHistorySize(1)
            .assertValueHistory(counterIdA)
            .assertValue {
                it == counterIdA
            }

        viewModel.initArguments(counterIdB, CLICK_COUNT)

        testObserver.assertHistorySize(2)
            .assertValueHistory(counterIdA, counterIdB)
            .assertValue {
                it == counterIdB
            }
    }

    @Test
    fun `create click, when force, then nothing`() = runTest {
        // given
        val counterRepository: CounterRepository = mock()
        val clickRepository: ClickRepository = mock()
        val viewModel = getViewModel(
            counterRepository = counterRepository,
            clickRepository = clickRepository,
        )
        val testObserver = viewModel.counterId.test()
            .assertNoValue()

        // when
        viewModel.createClick(true)

        // then
        verify(clickRepository, never()).createClick(any())
        verifyZeroInteractions(clickRepository)
        verifyZeroInteractions(counterRepository)
        testObserver.assertNoValue()
    }

    @Test
    fun `create click, when not force and counterId is null, then nothing`() = runTest {
        // given
        val counterRepository: CounterRepository = mock()
        val clickRepository: ClickRepository = mock()
        val viewModel = getViewModel(
            counterRepository = counterRepository,
            clickRepository = clickRepository,
        )
        val testObserver = viewModel.counterId.test()
            .assertNoValue()

        // when
        viewModel.createClick(false)

        // then
        verify(clickRepository, never()).createClick(any())
        verifyZeroInteractions(clickRepository)
        verifyZeroInteractions(counterRepository)
        testObserver.assertNoValue()
    }

    @Test
    fun `create click, when not force and has counterId, then expected`() = runTest {
        // given
        val counterRepository: CounterRepository = mock()
        val clickRepository: ClickRepository = mock()
        val viewModel = getViewModel(
            counterRepository = counterRepository,
            clickRepository = clickRepository,
        )
        val testObserver = viewModel.counterId.test()
            .assertNoValue()
        viewModel.counterId.postValue(1L)

        // when
        viewModel.createClick(false)

        // then
        verify(clickRepository, times(1)).createClick(any())
        verifyZeroInteractions(counterRepository)
        testObserver.assertHasValue()
            .assertHistorySize(1)
    }

    @Test
    fun `update counter, when counter is null, then nothing`() = runTest {
        // given
        val counterRepository: CounterRepository = mock()
        val clickRepository: ClickRepository = mock()
        val viewModel = getViewModel(
            counterRepository = counterRepository,
            clickRepository = clickRepository,
        )
        val testObserver = viewModel.counter.test()
            .assertNoValue()

        // when
        viewModel.updateCounter()

        // then
        verifyZeroInteractions(counterRepository)
        verifyZeroInteractions(clickRepository)
        testObserver.assertNoValue()
    }

    @Test
    fun `update counter, when counter is Not null, then expected`() = runTest {
        // given
        val counterRepository: CounterRepository = mock()
        val clickRepository: ClickRepository = mock()
        val viewModel = getViewModel(
            counterRepository = counterRepository,
            clickRepository = clickRepository,
        )
        val testObserverId = viewModel.counterId.test()
            .assertNoValue()
        val testObserverCounter = viewModel.counter.test()
            .assertNoValue()
        val counter = CounterDto(createTime = Date(), editTime = Date(), name = "Test", itemCount = CLICK_COUNT)
        val counterLD = MutableLiveData<CounterDto>().init(counter)
        whenever(counterRepository.findCounter(any())).thenReturn(counterLD)
        viewModel.counterId.postValue(1L)

        // when
        viewModel.updateCounter()

        // then
        verify(counterRepository, times(1)).findCounter(any())
        verify(counterRepository, times(1)).updateCounter(any())
        verifyNoMoreInteractions(counterRepository)
        verifyNoMoreInteractions(clickRepository)
        testObserverId.assertHasValue()
        testObserverCounter.assertHasValue()
    }

    @Test
    fun `longestClick initially emits empty dto`() = runTest {
        val clickRepository: ClickRepository = mock()
        val viewModel = getViewModel(clickRepository = clickRepository)

        viewModel.longestClick.test {
            assertThat(awaitItem()).isEqualTo(ClickStatsDto.empty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `longestClick emits data from repository when counterId is set`() = runTest {
        val clickRepository: ClickRepository = mock()
        val viewModel = getViewModel(clickRepository = clickRepository)
        val expectedStats = ClickStatsDto(heldMillis = 100L, position = 1L)

        whenever(clickRepository.getLongestClick(DEFAULT_COUNTER_ID)).thenReturn(flowOf(expectedStats))

        viewModel.longestClick.test {
            assertThat(awaitItem()).isEqualTo(ClickStatsDto.empty())

            viewModel.initArguments(DEFAULT_COUNTER_ID, CLICK_COUNT)

            assertThat(awaitItem()).isEqualTo(expectedStats)
            cancelAndConsumeRemainingEvents()
        }
        @Suppress("UnusedFlow")
        verify(clickRepository).getLongestClick(DEFAULT_COUNTER_ID)
    }

    @Test
    fun `longestClick remains empty dto if repository returns null after counterId is set`() = runTest {
        val clickRepository: ClickRepository = mock()
        val viewModel = getViewModel(clickRepository = clickRepository)

        whenever(clickRepository.getLongestClick(DEFAULT_COUNTER_ID)).thenReturn(flowOf(null))

        viewModel.longestClick.test {
            assertThat(awaitItem()).isEqualTo(ClickStatsDto.empty())
            viewModel.initArguments(DEFAULT_COUNTER_ID, CLICK_COUNT)
            cancelAndIgnoreRemainingEvents()
        }
        @Suppress("UnusedFlow")
        verify(clickRepository).getLongestClick(DEFAULT_COUNTER_ID)
    }

    @Test
    fun `shortestClick initially emits empty dto`() = runTest {
        val clickRepository: ClickRepository = mock()
        val viewModel = getViewModel(clickRepository = clickRepository)

        viewModel.shortestClick.test {
            assertThat(awaitItem()).isEqualTo(ClickStatsDto.empty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `shortestClick emits data from repository when counterId is set`() = runTest {
        val clickRepository: ClickRepository = mock()
        val viewModel = getViewModel(clickRepository = clickRepository)
        val expectedStats = ClickStatsDto(heldMillis = 10L, position = 1L)

        whenever(clickRepository.getShortestClick(DEFAULT_COUNTER_ID)).thenReturn(flowOf(expectedStats))

        viewModel.shortestClick.test {
            assertThat(awaitItem()).isEqualTo(ClickStatsDto.empty())
            viewModel.initArguments(DEFAULT_COUNTER_ID, CLICK_COUNT)
            assertThat(awaitItem()).isEqualTo(expectedStats)
            cancelAndConsumeRemainingEvents()
        }
        @Suppress("UnusedFlow")
        verify(clickRepository).getShortestClick(DEFAULT_COUNTER_ID)
    }

    @Test
    fun `shortestClick remains empty dto if repository returns null after counterId is set`() = runTest {
        val clickRepository: ClickRepository = mock()
        val viewModel = getViewModel(clickRepository = clickRepository)

        whenever(clickRepository.getShortestClick(DEFAULT_COUNTER_ID)).thenReturn(flowOf(null))

        viewModel.shortestClick.test {
            assertThat(awaitItem()).isEqualTo(ClickStatsDto.empty()) // Initial from stateIn
            viewModel.initArguments(DEFAULT_COUNTER_ID, CLICK_COUNT) // Set counterId
            cancelAndIgnoreRemainingEvents()
        }
        @Suppress("UnusedFlow")
        verify(clickRepository).getShortestClick(DEFAULT_COUNTER_ID)
    }

    private fun getViewModel(
        counterRepository: CounterRepository = mock(),
        clickRepository: ClickRepository = mock(),
    ): ClickListViewModel {
        return ClickListViewModel(
            counterRepository = counterRepository,
            clickRepository = clickRepository,
        )
    }

    private companion object {
        const val CLICK_COUNT = 0
        const val DEFAULT_COUNTER_ID = 1L // Added for new tests
    }
}
