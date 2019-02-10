package workshop.akbolatss.tools.touchcounter.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.utils.defaultName
import workshop.akbolatss.tools.touchcounter.utils.getCurrentTime
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
import workshop.akbolatss.tools.touchcounter.pojo.StatsObject
import workshop.akbolatss.tools.touchcounter.room.ClicksRepository
import workshop.akbolatss.tools.touchcounter.room.DataDao

class NavigationViewModel : ViewModel() {

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

    var currentTabTag: MutableLiveData<String> = MutableLiveData()

    var statsLiveData: MutableLiveData<StatsObject> = MutableLiveData()

    private lateinit var repository: ClicksRepository

    fun processRepository(dataDao: DataDao) {
        repository = ClicksRepository(dataDao)
    }

    fun loadStats() {
        uiScope.launch {
            val countersCount = repository.getCountersCount()
            val clicksCount = repository.getAllClicks()
            val longestClick = repository.getLongestClick()
            val mostClicksInCounter = repository.getMostClicksInCounter()

            val stats = StatsObject(
                countersCount = countersCount,
                clicksCount = clicksCount,
                longClick = longestClick,
                mostClicks = mostClicksInCounter
            )
            statsLiveData.value = stats
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun createCounter(context: Context) {
        uiScope.launch {
            val countersCount = repository.getCountersCount()
            repository.saveCounter(
                CounterObject(
                    getCurrentTime(),
                    getCurrentTime(),
                    count = 0,
                    name = context.defaultName((countersCount + 1))
                )
            )
        }
    }
}