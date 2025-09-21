package workshop.akbolatss.tools.touchcounter.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.jraska.livedata.test
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import workshop.akbolatss.tools.touchcounter.data.dao.ClickDao
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.data.dto.ClickStatsDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.utils.exts.init
import java.util.Date
import kotlin.test.assertEquals

class ClickRepositoryImplTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val clickDao = mock(ClickDao::class.java)

    private lateinit var repository: ClickRepository

    @Before
    fun setUp() {
        repository = ClickRepositoryImpl(clickDao)
    }

    @Test
    fun `create click `() = runTest {
        // given
        val clickA = getFakeClickA()
        val clickB = getFakeClickB()

        // when
        `when`(clickDao.create(clickA)).thenReturn(Unit)
        repository.createClick(clickA)

        // then
        verify(clickDao, times(1)).create(clickA)
        verify(clickDao, never()).create(clickB)
    }

    @Test
    fun `get clicks, when result is null, return default`() = runTest {
        // when
        `when`(clickDao.getCount()).thenReturn(null)
        val actual = repository.getAllClicks()

        // then
        verify(clickDao, times(1)).getCount()
        assertThat(actual).isNotNull()
        assertThat(actual).isEqualTo(0)
    }

    @Test
    fun `get clicks, when result is NOT null, return expected`() = runTest {
        // given
        val expected = 101

        // when
        `when`(clickDao.getCount()).thenReturn(expected)
        val actual = repository.getAllClicks()

        // then
        verify(clickDao, times(1)).getCount()
        assertThat(actual).isNotNull()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `get longest, when result is null, return default`() = runTest {
        // when
        `when`(clickDao.getLongest()).thenReturn(null)
        val actual = repository.getLongestClick()

        // then
        verify(clickDao, times(1)).getLongest()
        assertThat(actual).isNotNull()
        assertThat(actual).isEqualTo(0)
    }

    @Test
    fun `get longest, when result is NOT null, return expected`() = runTest {
        // given
        val expected = 101L

        // when
        `when`(clickDao.getLongest()).thenReturn(expected)
        val actual = repository.getLongestClick()

        // then
        verify(clickDao, times(1)).getLongest()
        assertThat(actual).isNotNull()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `get most clicks in counter, when result is null, return default`() = runTest {
        // when
        `when`(clickDao.getMostClicksInCounter()).thenReturn(null)
        val actual = repository.getMostClicksInCounter()

        // then
        verify(clickDao, times(1)).getMostClicksInCounter()
        assertThat(actual).isNotNull()
        assertThat(actual).isEqualTo(0)
    }

    @Test
    fun `get most clicks in counter, when result is NOT null, return expected`() = runTest {
        // given
        val expected = 101

        // when
        `when`(clickDao.getMostClicksInCounter()).thenReturn(expected)
        val actual = repository.getMostClicksInCounter()

        // then
        verify(clickDao, times(1)).getMostClicksInCounter()
        assertThat(actual).isNotNull()
        assertThat(actual).isEqualTo(expected)
    }

    @Test(expected = NullPointerException::class)
    fun `find clicks, when result is null, throw exception `() {
        // given
        val counterId = 0L

        // when
        `when`(clickDao.findListBy(counterId)).thenReturn(null)

        // then
        repository.findClickList(counterId).test()
        verify(clickDao, never()).findListBy(counterId)
    }

    @Test
    fun `find clicks, when result gives null, return null `() {
        // given
        val counterId = 0L
        val expectedResponse = MutableLiveData<List<ClickDto>>().init(null)

        // when
        `when`(clickDao.findListBy(counterId)).thenReturn(expectedResponse)

        // then
        repository.findClickList(counterId).test()
            .assertNullValue()
            .assertHistorySize(1)
        verify(clickDao, times(1)).findListBy(counterId)
    }

    @Test
    fun `find clicks, when result gives empty, return expected `() {
        // given
        val counterId = 0L
        val expectedResponse = MutableLiveData<List<ClickDto>>().init(emptyList())

        // when
        `when`(clickDao.findListBy(counterId)).thenReturn(expectedResponse)

        // then
        repository.findClickList(counterId).test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue {
                it.isEmpty()
            }
        verify(clickDao, times(1)).findListBy(counterId)
    }

    @Test
    fun `find clicks, when result gives expected, return expected `() {
        // given
        val counterId = 0L
        val expectedResponse =
            MutableLiveData<List<ClickDto>>().init(listOf(getFakeClickA(), getFakeClickB()))

        // when
        `when`(clickDao.findListBy(counterId)).thenReturn(expectedResponse)

        // then
        repository.findClickList(counterId).test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue {
                it.isNotEmpty() && it.size == 2
            }
        verify(clickDao, times(1)).findListBy(counterId)
    }

    @Test
    fun `get longest click by counterId, when dao returns null, flow emits empty dto`() = runTest {
        // given
        val counterId = 0L
        `when`(clickDao.getLongestClick(counterId)).thenReturn(null)

        // when
        val actualFlow = repository.getLongestClick(counterId)

        // then
        actualFlow.test {
            assertEquals(ClickStatsDto.empty(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(clickDao, times(1)).getLongestClick(counterId)
    }

    @Test
    fun `get longest click by counterId, when dao returns data, flow emits data`() = runTest {
        // given
        val counterId = 0L
        val expectedDto = ClickStatsDto(heldMillis = 101L, position = 1L)
        `when`(clickDao.getLongestClick(counterId)).thenReturn(expectedDto)

        // when
        val actualFlow = repository.getLongestClick(counterId)

        // then
        actualFlow.test {
            assertEquals(expectedDto, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(clickDao, times(1)).getLongestClick(counterId)
    }

    @Test
    fun `get shortest click by counterId, when dao returns null, flow emits empty dto`() = runTest {
        // given
        val counterId = 0L
        `when`(clickDao.getShortestClick(counterId)).thenReturn(null)

        // when
        val actualFlow = repository.getShortestClick(counterId)

        // then
        actualFlow.test {
            assertEquals(ClickStatsDto.empty(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(clickDao, times(1)).getShortestClick(counterId)
    }

    @Test
    fun `get shortest click by counterId, when dao returns data, flow emits data`() = runTest {
        // given
        val counterId = 0L
        val expectedDto = ClickStatsDto(heldMillis = 10L, position = 1L)
        `when`(clickDao.getShortestClick(counterId)).thenReturn(expectedDto)

        // when
        val actualFlow = repository.getShortestClick(counterId)

        // then
        actualFlow.test {
            assertEquals(expectedDto, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(clickDao, times(1)).getShortestClick(counterId)
    }

    private fun getFakeClickA(): ClickDto =
        ClickDto(createTime = Date(), heldMillis = 0L, counterId = 0L)

    private fun getFakeClickB(): ClickDto =
        ClickDto(createTime = Date(), heldMillis = 0L, counterId = 1L)
}
