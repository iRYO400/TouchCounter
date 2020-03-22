package workshop.akbolatss.tools.touchcounter.ui.list

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository
import workshop.akbolatss.tools.touchcounter.pojo.Stats
import workshop.akbolatss.tools.touchcounter.utils.appendIndex
import workshop.akbolatss.tools.touchcounter.utils.getCurrentTime
import javax.inject.Inject

class NavigationViewModel
@Inject
constructor(
    private val counterRepository: CounterRepository,
    private val clickRepository: ClickRepository
) : ViewModel() {

    val statsLiveData = MutableLiveData<Stats>()

    val counterList: LiveData<List<CounterDto>> =
        Transformations.map(counterRepository.findCounters()) {
            it
        }

    fun loadStats() {
        viewModelScope.launch {
            val countersCount = counterRepository.getCountersCount()
            val clicksCount = clickRepository.getAllClicks()
            val longestClick = clickRepository.getLongestClick()
            val mostClicksInCounter = clickRepository.getMostClicksInCounter()

            val stats = Stats(
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
            val countersCount = counterRepository.getCountersCount()
            counterRepository.createCounter(
                CounterDto(
                    createTime = getCurrentTime(),
                    editTime = getCurrentTime(),
                    name = newName.appendIndex(countersCount)
                )
            )
        }
    }

    fun deleteCounter(counter: CounterDto) {
        viewModelScope.launch {
            counterRepository.deleteCounter(counter)
        }
    }

    fun updateCounter(counter: CounterDto) {
        viewModelScope.launch {
            counterRepository.updateCounter(counter)
        }
    }
}