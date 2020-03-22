package workshop.akbolatss.tools.touchcounter.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto

@Dao
interface ClickDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createList(clickObjects: List<ClickDto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(clickObject: ClickDto)

    @Query("SELECT * FROM click WHERE counterId = :counterId")
    fun findListBy(counterId: Long): LiveData<List<ClickDto>>

    @Query("SELECT COUNT(id) FROM click")
    suspend fun getCount(): Int?

    @Query("SELECT MAX(heldMillis) FROM click")
    suspend fun getLongest(): Long?

    @Query("SELECT COUNT(counterId) AS c FROM click GROUP BY counterId ORDER BY c DESC LIMIT 1")
    suspend fun getMostClicksInCounter(): Int?

}