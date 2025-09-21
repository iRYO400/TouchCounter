package workshop.akbolatss.tools.touchcounter.data.dao

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.data.dto.ClickStatsDto

@Dao
interface ClickDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(clickObject: ClickDto)

    @Query("DELETE FROM click WHERE id = :id")
    suspend fun removeBy(id: Long)

    @Query("DELETE FROM click WHERE counterId = :counterId")
    suspend fun removeAllBy(counterId: Long)

    @Query("DELETE FROM click WHERE counterId IN (:counterIds)")
    suspend fun removeAllBy(counterIds: List<Long>)

    @Query("SELECT * FROM click WHERE counterId = :counterId")
    fun findListBy(counterId: Long): LiveData<List<ClickDto>>

    @Query("SELECT COUNT(id) FROM click")
    suspend fun getCount(): Int?

    @Query("SELECT MAX(heldMillis) FROM click")
    suspend fun getLongest(): Long?

    @Query("SELECT MIN(heldMillis) FROM click")
    suspend fun getShortest(): Long?

    @Query(
        "WITH longest_click AS (SELECT * FROM click WHERE counterId = :counterId ORDER BY heldMillis DESC LIMIT 1) " +
                "SELECT heldMillis, (SELECT COUNT(*) FROM click WHERE counterId = :counterId AND id <= longest_click.id) as position " +
                "FROM longest_click"
    )
    suspend fun getLongestClick(counterId: Long): ClickStatsDto?

    @Query(
        "WITH shortest_click AS (SELECT * FROM click WHERE counterId = :counterId ORDER BY heldMillis ASC LIMIT 1) " +
                "SELECT heldMillis, (SELECT COUNT(*) FROM click WHERE counterId = :counterId AND id <= shortest_click.id) as position " +
                "FROM shortest_click"
    )
    suspend fun getShortestClick(counterId: Long): ClickStatsDto?

    @Query("SELECT COUNT(counterId) AS c FROM click GROUP BY counterId ORDER BY c DESC LIMIT 1")
    suspend fun getMostClicksInCounter(): Int?

    @VisibleForTesting
    @Query("SELECT * FROM click WHERE id = :id")
    fun findBy(id: Long): ClickDto?
}
