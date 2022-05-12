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
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.room.AppDataBase
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], manifest = Config.NONE)
class CounterDaoTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private lateinit var appDataBase: AppDataBase
    private lateinit var counterDao: CounterDao

    @Before
    fun setUp() {
        appDataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()
        counterDao = appDataBase.counterDao
    }

    @After
    fun tearDown() {
        appDataBase.close()
    }

    @Test
    fun `create one counter when autoincrement primary key then expected primary key`() =
        runTest {
            val expectedId = 1L
            assertThat(counterDao.getCount()).isEqualTo(0)

            // when
            counterDao.create(getFakeCounter())

            // then
            assertThat(counterDao.find(expectedId)).isNotNull()
            assertThat(counterDao.find(expectedId)?.id).isEqualTo(expectedId)

            assertThat(counterDao.getCount()).isEqualTo(1)
        }

    @Test
    fun `create two counters when autoincrement primary key then expected primary key`() =
        runTest {
            val expectedIdA = 1L
            val expectedIdB = 2L
            assertThat(counterDao.getCount()).isEqualTo(0)

            // when
            counterDao.create(getFakeCounter())
            counterDao.create(getFakeCounter())

            // then
            assertThat(counterDao.find(expectedIdA)).isNotNull()
            assertThat(counterDao.find(expectedIdA)?.id).isEqualTo(expectedIdA)

            assertThat(counterDao.find(expectedIdB)).isNotNull()
            assertThat(counterDao.find(expectedIdB)?.id).isEqualTo(expectedIdB)

            assertThat(counterDao.getCount()).isEqualTo(2)
        }

    @Test
    fun `create two counters when same autoincrement primary key then expected primary key`() =
        runTest {
            val expectedId = 1L
            val notExpectedId = 2L
            assertThat(counterDao.getCount()).isEqualTo(0)

            // when
            counterDao.create(getFakeCounter(id = 1L))
            counterDao.create(getFakeCounter(id = 1L))

            // then
            assertThat(counterDao.find(expectedId)).isNotNull()
            assertThat(counterDao.find(expectedId)?.id).isEqualTo(expectedId)

            assertThat(counterDao.find(notExpectedId)).isNull()

            assertThat(counterDao.getCount()).isEqualTo(1)
        }

    @Test
    fun `update counter name by id then entity get name`() = runTest {
        val counterId = 1L
        val oldName = "Mega Name"
        val newName = "Ultra Name"
        counterDao.create(getFakeCounter(id = counterId, name = oldName))

        assertThat(counterDao.find(counterId)).isNotNull()
        assertThat(counterDao.find(counterId)?.name).isEqualTo(oldName)
        // when
        counterDao.update(getFakeCounter(id = counterId, name = newName))

        assertThat(counterDao.find(counterId)).isNotNull()
        assertThat(counterDao.find(counterId)?.name).isEqualTo(newName)
    }

    @Test
    fun `update counter name by wrong id then nothing changes`() = runTest {
        val counterId = 1L
        val wrongCounterId = 2L
        val oldName = "Mega Name"
        val newName = "Ultra Name"
        counterDao.create(getFakeCounter(id = counterId, name = oldName))

        assertThat(counterDao.find(counterId)).isNotNull()
        assertThat(counterDao.find(counterId)?.name).isEqualTo(oldName)
        // when
        counterDao.update(getFakeCounter(id = wrongCounterId, name = newName))

        assertThat(counterDao.find(counterId)).isNotNull()
        assertThat(counterDao.find(counterId)?.name).isEqualTo(oldName)

        assertThat(counterDao.find(wrongCounterId)).isNull()
    }

    @Test
    fun `update counter createTime by id then entity get new createTime`() = runTest {
        val counterId = 1L
        val oldCreateTime = Date(1000)
        val newCreateTime = Date(5000)
        counterDao.create(getFakeCounter(id = counterId, createTime = oldCreateTime))

        assertThat(counterDao.find(counterId)).isNotNull()
        assertThat(counterDao.find(counterId)?.createTime).isEqualTo(oldCreateTime)
        // when
        counterDao.update(getFakeCounter(id = counterId, createTime = newCreateTime))

        assertThat(counterDao.find(counterId)).isNotNull()
        assertThat(counterDao.find(counterId)?.createTime).isEqualTo(newCreateTime)
    }

    @Test
    fun `update counter createTime by wrong id then nothing changes`() = runTest {
        val counterId = 1L
        val wrongCounterId = 5L
        val oldCreateTime = Date(1000)
        val newCreateTime = Date(5000)
        counterDao.create(getFakeCounter(id = counterId, createTime = oldCreateTime))

        assertThat(counterDao.find(counterId)).isNotNull()
        assertThat(counterDao.find(counterId)?.createTime).isEqualTo(oldCreateTime)
        // when
        counterDao.update(getFakeCounter(id = wrongCounterId, createTime = newCreateTime))

        assertThat(counterDao.find(counterId)).isNotNull()
        assertThat(counterDao.find(counterId)?.createTime).isEqualTo(oldCreateTime)

        assertThat(counterDao.find(wrongCounterId)).isNull()
    }

    @Test
    fun `update counter editTime by id then entity get new editTime`() = runTest {
        val counterId = 1L
        val oldEditTime = Date(2000)
        val newEditTime = Date(7000)
        counterDao.create(getFakeCounter(id = counterId, createTime = oldEditTime))

        assertThat(counterDao.find(counterId)).isNotNull()
        assertThat(counterDao.find(counterId)?.createTime).isEqualTo(oldEditTime)
        // when
        counterDao.update(getFakeCounter(id = counterId, createTime = newEditTime))

        assertThat(counterDao.find(counterId)).isNotNull()
        assertThat(counterDao.find(counterId)?.createTime).isEqualTo(newEditTime)
    }

    @Test
    fun `update counter editTime by wrong id then nothing changes`() = runTest {
        val counterId = 2L
        val wrongCounterId = 7L
        val oldEditTime = Date(2000)
        val newEditTime = Date(7000)
        counterDao.create(getFakeCounter(id = counterId, createTime = oldEditTime))

        assertThat(counterDao.find(counterId)).isNotNull()
        assertThat(counterDao.find(counterId)?.createTime).isEqualTo(oldEditTime)
        // when
        counterDao.update(getFakeCounter(id = wrongCounterId, createTime = newEditTime))

        assertThat(counterDao.find(counterId)).isNotNull()
        assertThat(counterDao.find(counterId)?.createTime).isEqualTo(oldEditTime)

        assertThat(counterDao.find(wrongCounterId)).isNull()
    }

    @Test
    fun `delete counter by id then result empty`() = runTest {
        val expectedId = 1L
        counterDao.create(getFakeCounter(id = expectedId))

        assertThat(counterDao.find(expectedId)).isNotNull()
        assertThat(counterDao.find(expectedId)?.id).isEqualTo(expectedId)
        assertThat(counterDao.getCount()).isEqualTo(1)

        // when
        counterDao.delete(getFakeCounter(id = expectedId))

        assertThat(counterDao.find(expectedId)).isNull()
        assertThat(counterDao.getCount()).isEqualTo(0)
    }

    @Test
    fun `delete counter by wrong id then nothing changes`() = runTest {
        val expectedId = 1L
        val wrongCounterId = 5L
        counterDao.create(getFakeCounter(id = expectedId))

        assertThat(counterDao.find(expectedId)).isNotNull()
        assertThat(counterDao.find(expectedId)?.id).isEqualTo(expectedId)
        assertThat(counterDao.getCount()).isEqualTo(1)

        // when
        counterDao.delete(getFakeCounter(id = wrongCounterId))

        assertThat(counterDao.find(expectedId)).isNotNull()
        assertThat(counterDao.getCount()).isEqualTo(1)
    }

    @Test
    fun `observe list changes when create then expected results`() = runTest {
        val testObserver = counterDao.findList().test()
            .assertHistorySize(1)
            .assertHasValue()
            .assertValue {
                it.isEmpty()
            }

        // when
        counterDao.create(getFakeCounter())

        testObserver.assertHistorySize(2)
            .assertValue {
                it.size == 1
            }

        counterDao.create(getFakeCounter())

        testObserver.assertHistorySize(3)
            .assertValue {
                it.size == 2
            }

        for (i in 0 until 10) {
            counterDao.create(getFakeCounter())
        }

        testObserver.assertHistorySize(13)
            .assertValue {
                it.size == 12
            }
    }

    @Test
    fun `observe list changes when update then expected results`() = runTest {
        val testObserver = counterDao.findList().test()
            .assertHistorySize(1)
            .assertHasValue()
            .assertValue {
                it.isEmpty()
            }

        // when
        val counterId = 1L
        counterDao.create(getFakeCounter(id = counterId))

        testObserver.assertHistorySize(2)
            .assertValue {
                it.size == 1
            }

        counterDao.update(getFakeCounter(id = counterId))

        testObserver.assertHistorySize(3)
            .assertValue {
                it.size == 1
            }

        for (i in 0 until 10) {
            counterDao.update(getFakeCounter(id = counterId))
        }

        testObserver.assertHistorySize(13)
            .assertValue {
                it.size == 1
            }
    }

    @Test
    fun `observe by id counter when create then expected results`() = runTest {
        val counterId = 1L
        val testObserver = counterDao.findBy(counterId).test()
            .assertNullValue()
            .assertHistorySize(1)

        // when
        val oldName = "Brush"
        val oldDate = Date(3000)
        counterDao.create(
            getFakeCounter(
                id = counterId,
                name = oldName,
                createTime = oldDate,
                editTime = oldDate
            )
        )
        testObserver.assertHistorySize(2)
            .assertValue {
                it!!.name == oldName && it.createTime == oldDate && it.editTime == oldDate
            }

        val newName = "Melancholy"
        val newDate = Date(8000)
        counterDao.create(
            getFakeCounter(
                id = counterId,
                name = newName,
                createTime = newDate,
                editTime = newDate
            )
        )
        testObserver.assertHistorySize(3)
            .assertValue {
                it!!.name == newName && it.createTime == newDate && it.editTime == newDate
            }
    }

    @Test
    fun `observe by id counter when update then expected results`() = runTest {
        val counterId = 1L
        val testObserver = counterDao.findBy(counterId).test()
            .assertNullValue()
            .assertHistorySize(1)

        // when
        counterDao.update(getFakeCounter(id = counterId))
        testObserver.assertHistorySize(1)
            .assertNullValue()
    }

    @Test
    fun `observe by id counter when create, update and delete then expected results`() =
        runTest {
            val counterId = 1L
            val testObserver = counterDao.findBy(counterId).test()
                .assertNullValue()
                .assertHistorySize(1)

            // when
            val oldName = "Brush"
            counterDao.create(getFakeCounter(id = counterId, name = oldName))
            testObserver.assertHistorySize(2)
                .assertHasValue()
                .assertValue {
                    it!!.name == oldName
                }

            val newName = "Melancholy"
            counterDao.update(getFakeCounter(id = counterId, name = newName))
            testObserver.assertHistorySize(3)
                .assertHasValue()
                .assertValue {
                    it!!.name == newName
                }

            counterDao.delete(getFakeCounter(id = counterId))
            testObserver.assertHistorySize(4)
                .assertNullValue()
        }

    private fun getFakeCounter(
        id: Long = 0L,
        createTime: Date = Date(),
        editTime: Date = Date(),
        name: String = "Test"
    ) =
        CounterDto(id = id, createTime = createTime, editTime = editTime, name = name)
}