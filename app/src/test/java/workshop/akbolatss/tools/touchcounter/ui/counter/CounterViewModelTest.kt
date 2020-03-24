package workshop.akbolatss.tools.touchcounter.ui.counter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository
import workshop.akbolatss.tools.touchcounter.utils.init
import java.util.*

class CounterViewModelTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val counterRepository = Mockito.mock(CounterRepository::class.java)
    private val clickRepository = Mockito.mock(ClickRepository::class.java)

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: CounterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CounterViewModel(counterRepository, clickRepository)
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
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


//    @Test TODO don't know
//    fun executeTask() {
//        // given
//        val testObserver = viewModel.heldMillis.test()
//            .assertNoValue()
//        assertThat(viewModel.timerTask).isNull()
//        assertThat(viewModel.timer).isNotNull()
//
//        // when
//        viewModel.executeTask()
//        testObserver.assertHasValue()
//            .assertHistorySize(1)
//
//        assertThat(viewModel.timerTask).isNotNull()
//        assertThat(viewModel.timer).isNotNull()
//
//        viewModel.cancelTask()
//        assertThat(viewModel.timerTask).isNull()
//        assertThat(viewModel.timer).isNotNull()
//    }

//    @Test
//    fun cancelTask() {
//    }

    @Test
    fun `create click, when force, then nothing`() = runBlockingTest {
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
    fun `create click, when not force and counterId is null, then nothing`() = runBlockingTest {
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
    fun `create click, when not force and has counterId, then expected`() = runBlockingTest {
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
    fun `update counter, when counter is null, then nothing`() = runBlockingTest {
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
    fun `update counter, when counter is Not null, then expected`() = runBlockingTest {
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