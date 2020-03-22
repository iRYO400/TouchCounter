package workshop.akbolatss.tools.touchcounter.ui.counter

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
import workshop.akbolatss.tools.touchcounter.utils.AbsentLiveData
import workshop.akbolatss.tools.touchcounter.utils.getCurrentTime
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

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

    private var timer: Timer? = null
    private var startTime = Date()

    val heldMillis = MutableLiveData<Long>()

    fun setCounterId(counterId: Long) {
        if (this.counterId.value == counterId)
            return

        this.counterId.value = counterId
    }

    fun startTimer() {
        startTime = Date()
        timer = fixedRateTimer(startAt = startTime, period = 1) {
            heldMillis.postValue(System.currentTimeMillis().minus(startTime.time))
        }
    }

    fun stopTimer() {
        timer?.cancel()
    }

    fun createClick(isForce: Boolean) {
        if (isForce)
            return
        clickList.value?.let { clickList ->
            val index = clickList.size + 1
            val click = ClickObject(
                timestamp = startTime.time,
                holdTiming = heldMillis.value ?: System.currentTimeMillis(),
                index = index
            )
            addClick(click)
        }
    }

    private fun addClick(clickObject: ClickObject) {
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