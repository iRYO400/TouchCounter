package workshop.akbolatss.tools.touchcounter.ui.counter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository
import workshop.akbolatss.tools.touchcounter.utils.exts.init
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class CounterViewModelTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val counterRepository: CounterRepository = mock()
    private val clickRepository: ClickRepository = mock()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: ClickListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ClickListViewModel(counterRepository, clickRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `set counterId, when called once, then changed once`() {
        // given
        val testObserver = viewModel.counterId.test()
            .assertNoValue()
        val counterId = 0L

        // when
        viewModel.setCounterId(counterId)

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
        val testObserver = viewModel.counterId.test()
            .assertNoValue()
        val counterId = 0L

        // when
        viewModel.setCounterId(counterId)

        testObserver.assertHasValue()
            .assertHistorySize(1)
            .assertValueHistory(counterId)

        viewModel.setCounterId(counterId)

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
        val testObserver = viewModel.counterId.test()
            .assertNoValue()

        // when
        viewModel.setCounterId(counterIdA)

        testObserver.assertHasValue()
            .assertHistorySize(1)
            .assertValueHistory(counterIdA)
            .assertValue {
                it == counterIdA
            }

        viewModel.setCounterId(counterIdB)

        testObserver.assertHistorySize(2)
            .assertValueHistory(counterIdA, counterIdB)
            .assertValue {
                it == counterIdB
            }
    }

    @Test
    fun `create click, when force, then nothing`() = runTest {
        // given
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
        val testObserverId = viewModel.counterId.test()
            .assertNoValue()
        val testObserverCounter = viewModel.counter.test()
            .assertNoValue()
        val counter = CounterDto(createTime = Date(), editTime = Date(), name = "Test")
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
}
