package workshop.akbolatss.tools.touchcounter.room

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import java.util.*

@RunWith(AndroidJUnit4::class)
class AppDataBaseTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private lateinit var db: AppDataBase
    private lateinit var dataDao: DataDao

    @Mock
    private lateinit var observer: Observer<List<ClickObject>>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java).build()
        dataDao = db.dataDao
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeClickAndReadIt() {
        // given
        val click = ClickObject(Date(0).time, 10, 0)
        click.counterId = 2
        dataDao.getClickObjects(2).observeForever(observer)

        // when
        dataDao.saveClickObject(click)

        // then
        verify(observer).onChanged(Collections.singletonList(click))
    }
}