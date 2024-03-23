package workshop.akbolatss.tools.touchcounter.data.dao

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.jraska.livedata.test
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.room.AppDataBase
import java.util.Date

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU], manifest = Config.NONE)
class ClickDaoTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private lateinit var database: AppDataBase
    private lateinit var clickDao: ClickDao
    private lateinit var counterDao: CounterDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()
        counterDao = database.counterDao
        clickDao = database.clickDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun create_single_click_autoincrement() = runTest {
        // given
        val counterId = 1L
        val click = createClickDto(counterId = counterId)
        counterDao.create(createCounterDto(id = counterId))

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
        // given
        val counterId = 1L
        val click = createClickDto(counterId = counterId)
        counterDao.create(createCounterDto(id = counterId))

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
        // given
        val counterId = 1L
        val click = createClickDto(counterId = counterId)
        counterDao.create(createCounterDto(id = counterId))

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
        // given
        val expectedMillis = 1000
        val counterId = 1L
        val click = createClickDto(counterId = counterId, heldMillis = 1000)
        counterDao.create(createCounterDto(id = counterId))

        // when
        clickDao.create(click)

        // then
        assertThat(clickDao.getLongest()).isEqualTo(expectedMillis)
    }

    @Test
    fun findLongestClick_when_double_click_then_expected() = runTest {
        // given
        val expectedMillis = 2000
        val counterId = 1L
        val clickA = createClickDto(counterId = counterId, heldMillis = 1000)
        val clickB = createClickDto(counterId = counterId, heldMillis = 2000)
        counterDao.create(createCounterDto(id = counterId))

        // when
        clickDao.create(clickA)
        clickDao.create(clickB)

        // then
        assertThat(clickDao.getLongest()).isEqualTo(expectedMillis)
    }

    @Test
    fun findLongestClick_when_triple_click_then_expected() = runTest {
        // given
        val expectedMillis = 9000
        val counterId = 1L
        val clickA = createClickDto(counterId = counterId, heldMillis = 8999)
        val clickB = createClickDto(counterId = counterId, heldMillis = 9000)
        val clickC = createClickDto(counterId = counterId, heldMillis = 9000)
        counterDao.create(createCounterDto(id = counterId))

        // when
        clickDao.create(clickA)
        clickDao.create(clickB)
        clickDao.create(clickC)

        // then
        assertThat(clickDao.getLongest()).isEqualTo(expectedMillis)
    }

    @Test
    fun `find most click in counter when two items with different counter id `() = runTest {
        // given
        val expectedCount = 1
        val counterIdA = 1L
        val counterIdB = 2L
        counterDao.create(createCounterDto(id = counterIdA))
        counterDao.create(createCounterDto(id = counterIdB))

        // when
        clickDao.create(createClickDto(counterId = counterIdA))
        clickDao.create(createClickDto(counterId = counterIdB))

        // then
        assertThat(clickDao.getMostClicksInCounter()).isEqualTo(expectedCount)
    }

    @Test
    fun `find most click in counter when two items with same counter id `() = runTest {
        // given
        val counterId = 1L
        val expectedCount = 2L
        counterDao.create(createCounterDto(id = counterId))

        // when
        clickDao.create(createClickDto(counterId = counterId))
        clickDao.create(createClickDto(counterId = counterId))

        // then
        assertThat(clickDao.getMostClicksInCounter()).isEqualTo(expectedCount)
    }

    @Test
    fun `find most click in counter when many items with different counter id `() =
        runTest {
            val expectedCount = 5
            val counterIdA = 101L
            val counterIdB = 102L
            val counterIdC = 103L
            counterDao.create(createCounterDto(id = counterIdA))
            counterDao.create(createCounterDto(id = counterIdB))
            counterDao.create(createCounterDto(id = counterIdC))

            for (i in 0 until 3) {
                clickDao.create(createClickDto(counterId = counterIdA))
            }

            for (i in 0 until 4) {
                clickDao.create(createClickDto(counterId = counterIdB))
            }

            for (i in 0 until 5) {
                clickDao.create(createClickDto(counterId = counterIdC))
            }

            assertThat(clickDao.getMostClicksInCounter()).isEqualTo(expectedCount)
        }

    @Test
    fun `observe list live data by counter id when changed once`() = runTest {
        val counterId = 1L
        counterDao.create(createCounterDto(id = counterId))

        val testObserver = clickDao.findListBy(counterId).test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue {
                it.isEmpty()
            }

        clickDao.create(createClickDto(counterId = counterId))

        testObserver.assertHistorySize(2)
        testObserver.assertValue {
            it.size == 1
        }
    }

    @Test
    fun `observe list live data by counter id when changed two times`() = runTest {
        val counterId = 1L
        counterDao.create(createCounterDto(id = counterId))

        val testObserver = clickDao.findListBy(counterId).test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue {
                it.isEmpty()
            }

        clickDao.create(createClickDto(counterId = counterId))

        testObserver.assertHistorySize(2)

        clickDao.create(createClickDto(counterId = counterId))

        testObserver.assertHistorySize(3)

        testObserver.assertValue {
            it.size == 2
        }
    }

    @Test
    fun `observe list live data by counter id when changed three times`() = runTest {
        val counterId = 1L
        counterDao.create(createCounterDto(id = counterId))

        val testObserver = clickDao.findListBy(counterId).test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue {
                it.isEmpty()
            }

        clickDao.create(createClickDto(counterId = counterId))

        testObserver.assertHistorySize(2)

        clickDao.create(createClickDto(counterId = counterId))

        testObserver.assertHistorySize(3)

        clickDao.create(createClickDto(counterId = counterId))

        testObserver.assertHistorySize(4)

        testObserver.assertValue {
            it.size == 3
        }
    }

    private fun createCounterDto(
        id: Long = 0L,
        createTime: Date = Date(),
        editTime: Date = Date(),
        name: String = "Name"
    ) = CounterDto(
        id = id,
        createTime = createTime,
        editTime = editTime,
        name = name
    )

    private fun createClickDto(
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
