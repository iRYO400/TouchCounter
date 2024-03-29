package workshop.akbolatss.tools.touchcounter.ui.counter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlin.concurrent.scheduleAtFixedRate
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository
import workshop.akbolatss.tools.touchcounter.utils.android.AbsentLiveData
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class ClickListViewModel
@Inject
constructor(
    private val counterRepository: CounterRepository,
    private val clickRepository: ClickRepository
) : ViewModel() {

    val counterId = MutableLiveData<Long>()

    val counter: LiveData<CounterDto> = counterId.switchMap { counterId ->
        if (counterId == null)
            AbsentLiveData.create()
        else
            counterRepository.findCounter(counterId)
    }

    val clickList: LiveData<List<ClickDto>> = counterId.switchMap { counterId ->
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
                val updatedCounter = counter.copy(editTime = Date())
                counterRepository.updateCounter(updatedCounter)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        timer.purge()
    }

    fun removeClick(clickDto: ClickDto) {
        viewModelScope.launch {
            clickRepository.remove(clickDto.id)
        }
    }

    fun clearAllClick() {
        counterId.value?.let { counterId ->
            viewModelScope.launch {
                clickRepository.removeAll(counterId)
            }
        }
    }
}
