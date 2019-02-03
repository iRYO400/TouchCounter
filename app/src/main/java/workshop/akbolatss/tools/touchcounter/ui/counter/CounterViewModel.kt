package workshop.akbolatss.tools.touchcounter.ui.counter

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.INTENT_COUNTER_ID
import workshop.akbolatss.tools.touchcounter.logd
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
import workshop.akbolatss.tools.touchcounter.room.ClicksRepository
import workshop.akbolatss.tools.touchcounter.room.DataDao

class CounterViewModel : ViewModel() {

    private lateinit var repository: ClicksRepository

    lateinit var counterLiveData: LiveData<CounterObject>
    lateinit var clicksLiveData: LiveData<List<ClickObject>>

    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = Job()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     *
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var counterId: Long = -1L

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun processRepository(dataDao: DataDao) {
        repository = ClicksRepository(dataDao)
    }

    fun processIntent(intent: Intent) {
        counterId = intent.getLongExtra(INTENT_COUNTER_ID, -1)

        counterLiveData = repository.getCounter(counterId)
        clicksLiveData = repository.getClicks(counterId)
    }

    fun addClickObject(clickObject: ClickObject) {
        uiScope.launch {
            clickObject.counterId = counterId
            repository.addClick(clickObject)
        }
    }

    fun updateCounter() {
        uiScope.launch {
            val counterObject = counterLiveData.value!!
            counterObject.count = clicksLiveData.value!!.size.toLong()
            repository.updateCounter(counterObject)
        }
    }
}