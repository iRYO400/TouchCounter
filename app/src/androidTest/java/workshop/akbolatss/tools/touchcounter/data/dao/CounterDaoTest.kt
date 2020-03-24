package workshop.akbolatss.tools.touchcounter.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jraska.livedata.test
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.room.AppDataBase
import java.util.*

@RunWith(AndroidJUnit4::class)
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
        ).build()
        counterDao = appDataBase.counterDao
    }

    @After
    fun tearDown() {
        appDataBase.close()
    }


    @Test
    fun create() = runBlockingTest {
        // given

        // when
        counterDao.create(CounterDto(createTime = Date(), editTime = Date(), name = "Test"))

        // then
        counterDao.findBy(1L).test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue {
                it != null
            }
    }

    @Test
    fun update() = runBlockingTest {
        // given

        // when
        counterDao.update(CounterDto(createTime = Date(), editTime = Date(), name = "Test"))

        // then
        counterDao.findBy(1L).test()
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue {
                it != null
            }
    }

}