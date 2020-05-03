package workshop.akbolatss.tools.touchcounter.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository
import workshop.akbolatss.tools.touchcounter.pojo.Stats
import java.util.Date
import javax.inject.Inject

class CounterListViewModel
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
                    createTime = Date(),
                    editTime = Date(),
                    name = "$newName $countersCount"
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