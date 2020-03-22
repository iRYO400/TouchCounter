package workshop.akbolatss.tools.touchcounter.ui.counter

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository
import workshop.akbolatss.tools.touchcounter.utils.AbsentLiveData
import workshop.akbolatss.tools.touchcounter.utils.getCurrentTime
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

class CounterViewModel
@Inject
constructor(
    private val counterRepository: CounterRepository,
    private val clickRepository: ClickRepository
) : ViewModel() {

    private var counterId = MutableLiveData<Long>()

    val counter: LiveData<CounterDto> =
        Transformations.switchMap(counterId) { counterId ->
            if (counterId == null)
                AbsentLiveData.create()
            else
                counterRepository.findCounter(counterId)
        }

    val clickList: LiveData<List<ClickDto>> =
        Transformations.switchMap(counterId) { counterId ->
            if (counterId == null)
                AbsentLiveData.create()
            else
                clickRepository.findClickList(counterId)
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
        counterId.value?.let { counterId ->
            val click = ClickDto(
                createTime = startTime,
                heldMillis = heldMillis.value ?: System.currentTimeMillis(),
                counterId = counterId
            )
            createClickAsync(click)
        }
    }

    private fun createClickAsync(click: ClickDto) {
        viewModelScope.launch {
            clickRepository.createClick(click)
        }
    }

    fun updateCounter() {
        viewModelScope.launch {
            counter.value?.let { counter ->
                val updatedCounter = counter.copy(editTime = getCurrentTime())
                counterRepository.updateCounter(updatedCounter)
            }
        }
    }
}