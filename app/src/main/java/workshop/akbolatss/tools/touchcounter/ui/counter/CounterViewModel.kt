package workshop.akbolatss.tools.touchcounter.ui.counter

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
import workshop.akbolatss.tools.touchcounter.room.ClicksRepository
import workshop.akbolatss.tools.touchcounter.room.DataDao
import workshop.akbolatss.tools.touchcounter.utils.INTENT_COUNTER_ID
import workshop.akbolatss.tools.touchcounter.utils.getCurrentTime

class CounterViewModel : ViewModel() {

    private lateinit var repository: ClicksRepository

    lateinit var counterLiveData: LiveData<CounterObject>
    lateinit var clicksLiveData: LiveData<List<ClickObject>>

    private var counterId: Long = -1L

    fun processRepository(dataDao: DataDao) {
        repository = ClicksRepository(dataDao)
    }

    fun processIntent(intent: Intent) {
        counterId = intent.getLongExtra(INTENT_COUNTER_ID, -1)

        counterLiveData = repository.getCounter(counterId)
        clicksLiveData = repository.getClicks(counterId)
    }

    fun addClickObject(clickObject: ClickObject) {
        viewModelScope.launch {
            clickObject.counterId = counterId
            repository.addClick(clickObject)
        }
    }

    fun updateCounter() {
        viewModelScope.launch {
            val counterObject = counterLiveData.value!!
            counterObject.timestampEditing =
                getCurrentTime()
            counterObject.count = clicksLiveData.value!!.size.toLong()
            repository.updateCounter(counterObject)
        }
    }

}