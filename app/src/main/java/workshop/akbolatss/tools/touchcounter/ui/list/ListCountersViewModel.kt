package workshop.akbolatss.tools.touchcounter.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
import workshop.akbolatss.tools.touchcounter.room.ClicksRepository
import workshop.akbolatss.tools.touchcounter.room.DataDao

class ListCountersViewModel : ViewModel() {
    private lateinit var repository: ClicksRepository

    lateinit var countersLiveData: LiveData<List<CounterObject>>

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

    fun processRepository(dataDao: DataDao) {
        repository = ClicksRepository(dataDao)

        loadData()
    }

    fun loadData() {
        countersLiveData = repository.getCounters()
    }

    fun deleteCounter(counter: CounterObject) {
        uiScope.launch {
            repository.deleteCounter(counter)
        }
    }

    fun updateCounter(counter: CounterObject) {
        uiScope.launch {
            repository.updateCounter(counter)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}