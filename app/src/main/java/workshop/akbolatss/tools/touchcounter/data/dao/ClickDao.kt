package workshop.akbolatss.tools.touchcounter.data.dao

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.room.*
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto

@Dao
interface ClickDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(clickObject: ClickDto)

    @Query("DELETE FROM click WHERE id = :id")
    suspend fun remove(id: Long)

    @Query("SELECT * FROM click WHERE counterId = :counterId")
    fun findListBy(counterId: Long): LiveData<List<ClickDto>>

    @Query("SELECT COUNT(id) FROM click")
    suspend fun getCount(): Int?

    @Query("SELECT MAX(heldMillis) FROM click")
    suspend fun getLongest(): Long?

    @Query("SELECT COUNT(counterId) AS c FROM click GROUP BY counterId ORDER BY c DESC LIMIT 1")
    suspend fun getMostClicksInCounter(): Int?

    @VisibleForTesting
    @Query("SELECT * FROM click WHERE id = :id")
    fun findBy(id: Long): ClickDto?

    @Query("DELETE FROM click WHERE counterId = :counterId")
    suspend fun removeAll(counterId: Long)
}
