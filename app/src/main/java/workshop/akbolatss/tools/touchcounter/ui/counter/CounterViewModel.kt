package workshop.akbolatss.tools.touchcounter.ui.counter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository
import workshop.akbolatss.tools.touchcounter.utils.AbsentLiveData
import workshop.akbolatss.tools.touchcounter.utils.getCurrentTime
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.concurrent.scheduleAtFixedRate

class CounterViewModel
@Inject
constructor(
    private val counterRepository: CounterRepository,
    private val clickRepository: ClickRepository
) : ViewModel() {

    val counterId = MutableLiveData<Long>()

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

    private val timer: Timer = Timer()
    private var timerTask: TimerTask? = null
    private var btnHoldingStartTime = Date()

    val heldMillis = MutableLiveData<Long>()

    fun setCounterId(counterId: Long) {
        if (this.counterId.value == counterId)
            return

        this.counterId.value = counterId
    }

    fun executeTask() {
        btnHoldingStartTime = Date()
        timerTask = timer.scheduleAtFixedRate(time = btnHoldingStartTime, period = 1) {
            heldMillis.postValue(System.currentTimeMillis().minus(btnHoldingStartTime.time))
        }
    }

    fun cancelTask() {
        timerTask?.cancel()
        timerTask = null
    }

    fun createClick(isForce: Boolean) {
        if (isForce)
            return
        counterId.value?.let { counterId ->
            val click = ClickDto(
                createTime = btnHoldingStartTime,
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

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        timer.purge()
    }
}