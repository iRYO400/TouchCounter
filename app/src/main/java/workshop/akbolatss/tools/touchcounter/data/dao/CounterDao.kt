package workshop.akbolatss.tools.touchcounter.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto

@Dao
interface CounterDao {

    @Insert(onConflict = REPLACE)
    suspend fun create(counterObject: CounterDto): Long

    @Update
    suspend fun update(counterObject: CounterDto)

    @Query("SELECT * FROM counter WHERE id = :id")
    fun findBy(id: Long): LiveData<CounterDto>

    @Query("SELECT c.*, (SELECT COUNT(id) FROM click WHERE counterId = c.id) AS itemCount FROM counter AS c ORDER BY editTime DESC")
    fun findList(): LiveData<List<CounterDto>>

    @Query("SELECT COUNT(id) FROM counter")
    suspend fun getCount(): Int?

    @Delete
    suspend fun delete(counterObject: CounterDto)

}