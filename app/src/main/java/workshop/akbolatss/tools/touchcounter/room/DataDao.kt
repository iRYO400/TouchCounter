package workshop.akbolatss.tools.touchcounter.room

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject

@Dao
interface DataDao {

    // Insert
    @Insert(onConflict = REPLACE)
    fun saveCounter(counterObject: CounterObject): Long

    @Insert(onConflict = REPLACE)
    fun saveClickObjects(clickObjects: List<ClickObject>)

    @Insert(onConflict = REPLACE)
    fun saveClickObject(clickObject: ClickObject)

    // Update
    @Update
    fun updateCounter(counterObject: CounterObject)

    // Query
    @Query("SELECT COUNT(name) FROM CounterObject")
    fun getCountersRowCount(): Int

    @Query("SELECT * FROM CounterObject WHERE id = :id")
    fun getCounterObject(id: Long): LiveData<CounterObject>

    @Query("SELECT * FROM CounterObject ORDER BY timestampEditing DESC")
    fun getCounterObjects(): LiveData<List<CounterObject>>

    @Query("SELECT * FROM ClickObject WHERE counterId = :counterId")
    fun getClickObjects(counterId: Long): LiveData<List<ClickObject>>

    @Query("SELECT SUM(count) FROM CounterObject")
    fun getAllClicksCount(): Int

    @Query("SELECT MAX(holdTiming) FROM ClickObject")
    fun getLongestClick(): Long

    @Query("SELECT MAX(count) FROM CounterObject")
    fun getMostClicksInCounter(): Int

    // Delete
    @Delete
    fun deleteCounter(counterObject: CounterObject)

    @Query("DELETE FROM ClickObject WHERE counterId = :id")
    fun deleteClicks(id: Long)

}