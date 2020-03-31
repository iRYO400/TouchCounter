package workshop.akbolatss.tools.touchcounter.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.room.AppDataBase
import java.util.Date

@RunWith(AndroidJUnit4::class)
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
        ).build()
        clickDao = database.clickDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    companion object {
        const val PRIMARY_ID_WITH_AUTOINCREMENT = 0L
        const val PRIMARY_ID_WITHOUT_AUTOINCREMENT = 1L
    }

    @Test
    fun create_click_when_autoincrement_id_then_find_it() = runBlockingTest {
        // given
        val givenId = 0L
        val expectedId = 1L
        val clickA = getFakeClick(foreignKeyId = givenId)

        // when
        clickDao.create(clickA)

        // then
        val click = clickDao.findListBy(givenId)
        assertThat(click).isNotNull()
    }

    @Test
    fun create_click_when_not_autoincrement_id_then_find_it() = runBlockingTest {
        // given
        val givenId = 1L
        val expectedId = 1L
        val clickA = getFakeClick(id = givenId)

        // when
        clickDao.create(clickA)

        // then
        val click = clickDao.findBy(expectedId)
        assertThat(click).isNotNull()
        assertThat(click!!.id).isEqualTo(expectedId)
    }

    @Test
    fun create_three_clicks_when_autoincrement_id_then_find_three() = runBlockingTest {
        // given
        val givenId = 0L
        val expectedId = 1L
        val clickA = getFakeClick(id = givenId)
        val clickB = getFakeClick(id = givenId)

        // when
        clickDao.create(clickA)
        clickDao.create(clickB)

        // then
        val click = clickDao.findBy(expectedId)
        assertThat(click).isNotNull()
        assertThat(click!!.id).isEqualTo(expectedId)
    }

    private fun getFakeClick(
        id: Long = 0L,
        createTime: Date = Date(),
        heldMillis: Long = 0L,
        foreignKeyId: Long = 0L
    ): ClickDto =
        ClickDto(
            id = id,
            createTime = createTime,
            heldMillis = heldMillis,
            counterId = foreignKeyId
        )
}