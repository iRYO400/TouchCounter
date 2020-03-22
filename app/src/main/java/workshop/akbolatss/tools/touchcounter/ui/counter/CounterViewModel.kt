package workshop.akbolatss.tools.touchcounter.ui.counter

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
import workshop.akbolatss.tools.touchcounter.utils.AbsentLiveData
import workshop.akbolatss.tools.touchcounter.utils.getCurrentTime
import javax.inject.Inject

class CounterViewModel
@Inject
constructor(private val repository: ClickRepository) : ViewModel() {

    private var counterId = MutableLiveData<Long>()

    val counter: LiveData<CounterObject> =
        Transformations.switchMap(counterId) { counterId ->
            if (counterId == null)
                AbsentLiveData.create()
            else
                repository.getCounter(counterId)
        }

    val clickList: LiveData<List<ClickObject>> =
        Transformations.switchMap(counterId) { counterId ->
            if (counterId == null)
                AbsentLiveData.create()
            else
                repository.getClicks(counterId)
        }

    fun setCounterId(counterId: Long) {
        if (this.counterId.value == counterId)
            return

        this.counterId.value = counterId
    }

    fun addClickObject(clickObject: ClickObject) {
        viewModelScope.launch {
            counterId.value?.let { counterId ->
                clickObject.counterId = counterId
                repository.addClick(clickObject)
            }
        }
    }

    fun updateCounter() {
        viewModelScope.launch {
            counter.value?.let { counter ->
                counter.timestampEditing =
                    getCurrentTime()
                counter.count = clickList.value?.size?.toLong() ?: 0L
                repository.updateCounter(counter)
            }
        }
    }

}