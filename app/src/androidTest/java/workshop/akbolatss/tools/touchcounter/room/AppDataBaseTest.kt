package workshop.akbolatss.tools.touchcounter.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import workshop.akbolatss.tools.touchcounter.data.dao.CounterDao

@RunWith(AndroidJUnit4::class)
class AppDataBaseTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private lateinit var db: AppDataBase
    private lateinit var dataDao: CounterDao
//
//    @Before
//    fun setUp() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java).build()
//        dataDao = db.dataDao
//    }
//
//    @After
//    fun tearDown() {
//        db.close()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun saveCounter_withDefaultId_shouldFound() {
//        // given
//        val counter = getDefaultCounter(id = DEFAULT_ID)
//        // when
//        dataDao.create(counter)
//        // then
//        dataDao.findList().test()
//            .assertValue {
//                it.isNotEmpty()
//            }
//            .assertValue {
//                it[0] == counter
//            }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun saveCounter_withSameCounterId_shouldReplace() {
//        val counter = getDefaultCounter(id = COUNTER_ID_1)
//
//        dataDao.create(counter)
//        dataDao.create(counter)
//
//        dataDao.findList().test()
//            .assertValue {
//                it.size == 1
//            }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun saveCounter_withDefaultCounterId_shouldNotReplace() {
//        val counter = getDefaultCounter(id = DEFAULT_ID)
//
//        dataDao.create(counter)
//        dataDao.create(counter)
//
//        dataDao.findList().test()
//            .assertValue {
//                it.size == 2
//            }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun saveCounter_withDifferentCounterId_shouldNotReplace() {
//        val counter1 = getDefaultCounter(id = 1)
//        val counter2 = getDefaultCounter(id = 2)
//
//        dataDao.create(counter1)
//        dataDao.create(counter2)
//
//        dataDao.findList().test()
//            .assertValue {
//                it.size == 2
//            }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun saveClick_withDefaultCounterId_shouldBeNotEmpty() {
//        val click = getDefaultClick(id = DEFAULT_ID)
//
//        dataDao.saveClickObject(click)
//
//        dataDao.getClickObjects(DEFAULT_ID).test()
//            .assertHasValue()
//            .assertValue { list ->
//                list.isNotEmpty()
//            }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun saveClick_withSameCounterId_shouldBeNotEmpty() {
//        val click = getDefaultClick(counterId = COUNTER_ID_1, id = DEFAULT_ID)
//
//        dataDao.saveClickObject(click)
//
//        dataDao.getClickObjects(COUNTER_ID_1).test()
//            .assertHasValue()
//            .assertValue { list ->
//                list.isNotEmpty()
//            }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun saveClick_withDifferentCounterId() {
//        val click = getDefaultClick(counterId = DEFAULT_ID, id = DEFAULT_ID)
//
//        dataDao.saveClickObject(click)
//
//        dataDao.getClickObjects(COUNTER_ID_1).test()
//            .assertHasValue()
//            .assertValue { list ->
//                list.isEmpty()
//            }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun saveClicks_withEmptyList_shouldBeEmpty() {
//
//        dataDao.saveClickObjects(listOf())
//
//        dataDao.getClickObjects(DEFAULT_ID).test()
//            .assertValue {
//                it.isEmpty()
//            }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun saveClicks_withSingleItemInList_shouldBeNotEmpty() {
//        val click = getDefaultClick(id = DEFAULT_ID)
//        val list = listOf(click)
//
//        dataDao.saveClickObjects(list)
//
//        dataDao.getClickObjects(DEFAULT_ID).test()
//            .assertValue {
//                it.isEmpty()
//            }
//    }
//
}