package workshop.akbolatss.tools.touchcounter.data.dao

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.jraska.livedata.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.room.AppDataBase
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], manifest = Config.NONE)
class ClickDaoTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private lateinit var database: AppDataBase
    private lateinit var clickDao: ClickDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()
        clickDao = database.clickDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun create_single_click_autoincrement() = runTest {
        val counterId = 1L
        val click = getFakeClick(counterId = counterId)

        // when
        clickDao.create(click)

        // then
        assertThat(clickDao.getCount()).isNotNull()
        assertThat(clickDao.getCount()).isEqualTo(1)

        assertThat(clickDao.findBy(1)).isNotNull()
        assertThat(clickDao.findBy(1)?.counterId).isEqualTo(counterId)
    }

    @Test
    fun create_double_click_autoincrement() = runTest {
        val counterId = 1L
        val click = getFakeClick(counterId = counterId)

        // when
        clickDao.create(click)
        clickDao.create(click)

        // then
        assertThat(clickDao.getCount()).isNotNull()
        assertThat(clickDao.getCount()).isEqualTo(2)

        assertThat(clickDao.findBy(1)).isNotNull()
        assertThat(clickDao.findBy(1)?.counterId).isEqualTo(counterId)

        assertThat(clickDao.findBy(2)).isNotNull()
        assertThat(clickDao.findBy(2)?.counterId).isEqualTo(counterId)
    }

    @Test
    fun create_triple_click_autoincrement() = runTest {
        val counterId = 1L
        val click = getFakeClick(counterId = counterId)

        // when
        clickDao.create(click)
        clickDao.create(click)
        clickDao.create(click)

        // then
        assertThat(clickDao.getCount()).isNotNull()
        assertThat(clickDao.getCount()).isEqualTo(3)

        assertThat(clickDao.findBy(1)).isNotNull()
        assertThat(clickDao.findBy(1)?.counterId).isEqualTo(counterId)

        assertThat(clickDao.findBy(2)).isNotNull()
        assertThat(clickDao.findBy(2)?.counterId).isEqualTo(counterId)

        assertThat(clickDao.findBy(3)).isNotNull()
        assertThat(clickDao.findBy(3)?.counterId).isEqualTo(counterId)
    }

    @Test
    fun findLongestClick_when_single_click_then_expected() = runTest {
        val counterId = 1L
        val expectedMillis = 1000
        val click = getFakeClick(counterId = counterId, heldMillis = 1000)

        clickDao.create(click)

        assertThat(clickDao.getLongest()).isEqualTo(expectedMillis)
    }

    @Test
    fun findLongestClick_when_double_click_then_expected() = runTest {
        val counterId = 1L
        val expectedMillis = 2000
        val clickA = getFakeClick(counterId = counterId, heldMillis = 1000)
        val clickB = getFakeClick(counterId = counterId, heldMillis = 2000)

        clickDao.create(clickA)
        clickDao.create(clickB)

        assertThat(clickDao.getLongest()).isEqualTo(expectedMillis)
    }

    @Test
    fun findLongestClick_when_triple_click_then_expected() = runTest {
        val counterId = 1L
        val expectedMillis = 9000
        val clickA = getFakeClick(counterId = counterId, heldMillis = 8999)
        val clickB = getFakeClick(counterId = counterId, heldMillis = 9000)
        val clickC = getFakeClick(counterId = counterId, heldMillis = 9000)

        clickDao.create(clickA)
        clickDao.create(clickB)
        clickDao.create(clickC)

        assertThat(clickDao.getLongest()).isEqualTo(expectedMillis)
    }

    @Test
    fun `find most click in counter when two items with different counter id `() = runTest {
        val expectedCount = 1

        clickDao.create(getFakeClick(counterId = 1))
        clickDao.create(getFakeClick(counterId = 2))

        assertThat(clickDao.getMostClicksInCounter()).isEqualTo(expectedCount)
    }

    @Test
    fun `find most click in counter when two items with same counter id `() = runTest {
        val expectedCount = 2L

        clickDao.create(getFakeClick(counterId = 1L))
        clickDao.create(getFakeClick(counterId = 1L))

        assertThat(clickDao.getMostClicksInCounter()).isEqualTo(expectedCount)
    }

    @Test
    fun `find most click in counter when many items with different counter id `() =
        runTest {
            val expectedCount = 5

            for (i in 0 until 3) {
                clickDao.create(getFakeClick(counterId = 3))
            }

            for (i in 0 until 4) {
                clickDao.create(getFakeClick(counterId = 4))
            }

            for (i in 0 until 5) {
                clickDao.create(getFakeClick(counterId = 5))
            }

            assertThat(clickDao.getMostClicksInCounter()).isEqualTo(expectedCount)
        }

    @Test
    fun `observe list live data by counter id when changed once`() = runTest {
        val counterId = 1L
        val testObserver = clickDao.findListBy(counterId).test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue {
                it.isEmpty()
            }

        clickDao.create(getFakeClick(counterId = counterId))

        testObserver.assertHistorySize(2)
        testObserver.assertValue {
            it.size == 1
        }
    }

    @Test
    fun `observe list live data by counter id when changed two times`() = runTest {
        val counterId = 1L
        val testObserver = clickDao.findListBy(counterId).test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue {
                it.isEmpty()
            }

        clickDao.create(getFakeClick(counterId = counterId))

        testObserver.assertHistorySize(2)

        clickDao.create(getFakeClick(counterId = counterId))

        testObserver.assertHistorySize(3)

        testObserver.assertValue {
            it.size == 2
        }

        testObserver.assertValue {
            it.size == 2
        }
    }

    @Test
    fun `observe list live data by counter id when changed three times`() = runTest {
        val counterId = 1L
        val testObserver = clickDao.findListBy(counterId).test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue {
                it.isEmpty()
            }

        clickDao.create(getFakeClick(counterId = counterId))

        testObserver.assertHistorySize(2)

        clickDao.create(getFakeClick(counterId = counterId))

        testObserver.assertHistorySize(3)

        clickDao.create(getFakeClick(counterId = counterId))

        testObserver.assertHistorySize(4)

        testObserver.assertValue {
            it.size == 3
        }
    }

    private fun getFakeClick(
        id: Long = 0L,
        createTime: Date = Date(),
        heldMillis: Long = 0L,
        counterId: Long = 0L
    ): ClickDto =
        ClickDto(
            id = id,
            createTime = createTime,
            heldMillis = heldMillis,
            counterId = counterId
        )
}
