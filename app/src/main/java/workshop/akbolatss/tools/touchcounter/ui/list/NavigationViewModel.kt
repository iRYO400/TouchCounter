package workshop.akbolatss.tools.touchcounter.ui.list

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
import workshop.akbolatss.tools.touchcounter.pojo.StatsObject
import workshop.akbolatss.tools.touchcounter.utils.appendIndex
import workshop.akbolatss.tools.touchcounter.utils.getCurrentTime
import javax.inject.Inject

class NavigationViewModel
@Inject
constructor(
    private val repository: ClickRepository
) : ViewModel() {

    val statsLiveData = MutableLiveData<StatsObject>()

    val counterList: LiveData<List<CounterObject>> = Transformations.map(repository.getCounters()) {
        it
    }

    fun loadStats() {
        viewModelScope.launch {
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

    fun createCounter(newName: String) {
        viewModelScope.launch {
            val countersCount = repository.getCountersCount()
            repository.saveCounter(
                CounterObject(
                    getCurrentTime(),
                    getCurrentTime(),
                    count = 0,
                    name = newName.appendIndex(countersCount)
                )
            )
        }
    }

    fun deleteCounter(counter: CounterObject) {
        viewModelScope.launch {
            repository.deleteCounter(counter)
        }
    }

    fun updateCounter(counter: CounterObject) {
        viewModelScope.launch {
            repository.updateCounter(counter)
        }
    }
}