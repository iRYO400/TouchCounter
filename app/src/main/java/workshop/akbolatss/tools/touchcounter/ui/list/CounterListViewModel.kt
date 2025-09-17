package workshop.akbolatss.tools.touchcounter.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository
import workshop.akbolatss.tools.touchcounter.pojo.Stats

class CounterListViewModel
@Inject
constructor(
    private val counterRepository: CounterRepository,
    private val clickRepository: ClickRepository
) : ViewModel() {

    val statsLiveData = MutableLiveData<Stats>()

    val counterList: LiveData<List<CounterDto>> = counterRepository.findCounters()

    fun loadStats() {
        viewModelScope.launch {
            val countersCount = counterRepository.getCountersCount()
            val clicksCount = clickRepository.getAllClicks()
            val longestClick = clickRepository.getLongestClick()
            val shortClick = clickRepository.getShortestClick()
            val mostClicksInCounter = clickRepository.getMostClicksInCounter()

            val stats = Stats(
                countersCount = countersCount,
                clicksCount = clicksCount,
                longClick = longestClick,
                shortClick = shortClick,
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
                    createTime = Date(),
                    editTime = Date(),
                    name = "$newName $countersCount"
                )
            )
        }
    }

    fun deleteCounter(counter: CounterDto) {
        viewModelScope.launch { counterRepository.deleteCounter(counter)
        }
    }

    fun updateCounter(counter: CounterDto) {
        viewModelScope.launch {
            counterRepository.updateCounter(counter)
        }
    }
}
