package workshop.akbolatss.tools.touchcounter.room

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject

@Dao
interface DataDao {

    @Query("SELECT COUNT(name) FROM CounterObject")
    fun getCountersRowCount(): Int

    @Query("SELECT COUNT(counterId) FROM ClickObject WHERE counterId =:counterId")
    fun getClicksRowInCounterObject(counterId: Long): Int

    @Query("SELECT * FROM CounterObject WHERE id = :id")
    fun counterObject(id: Long): LiveData<CounterObject>

    @Query("SELECT * FROM CounterObject ORDER BY timestampEditing DESC")
    fun getCounterObjects(): LiveData<List<CounterObject>>

    @Query("SELECT * FROM CounterObject")
    fun getCounterObjectsSS(): List<CounterObject>

    @Query("SELECT * FROM ClickObject WHERE counterId = :counterId")
    fun getClickObjects(counterId: Long): LiveData<List<ClickObject>>

    @Query("SELECT * FROM ClickObject WHERE counterId = :counterId")
    fun getClickObjectsLD(counterId: Long): LiveData<List<ClickObject>>

    @Insert(onConflict = REPLACE)
    fun saveCounter(counterObject: CounterObject): Long

    @Insert(onConflict = REPLACE)
    fun saveCounters(counterObjects: List<CounterObject>)

    @Insert(onConflict = REPLACE)
    fun saveClickObjects(clickObjects: List<ClickObject>)

    @Insert(onConflict = REPLACE)
    fun saveClickObject(clickObject: ClickObject)

    @Update
    fun updateCounter(counterObject: CounterObject)

    @Delete
    fun deleteCounter(counterObject: CounterObject)

    @Query("SELECT SUM(count) FROM CounterObject")
    fun getAllClicksCount(): Int

    @Query("SELECT MAX(holdTiming) FROM ClickObject")
    fun getLongestClick(): Long

    @Query("SELECT MAX(count) FROM CounterObject")
    fun getMostClicksInCounter(): Int

    @Query("DELETE FROM ClickObject WHERE counterId = :id")
    fun deleteClicks(id: Long)

}