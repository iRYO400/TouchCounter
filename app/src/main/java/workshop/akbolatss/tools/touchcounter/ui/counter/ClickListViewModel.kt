package workshop.akbolatss.tools.touchcounter.ui.counter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.data.dto.ClickStatsDto
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository
import workshop.akbolatss.tools.touchcounter.utils.android.AbsentLiveData
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.concurrent.scheduleAtFixedRate

@OptIn(ExperimentalCoroutinesApi::class)
class ClickListViewModel
@Inject
constructor(
    private val counterRepository: CounterRepository,
    private val clickRepository: ClickRepository,
) : ViewModel() {

    val counterId = MutableLiveData<Long>()
    val heldMillis = MutableLiveData<Long>()

    val counter: LiveData<CounterDto> = counterId.switchMap { counterId ->
        if (counterId == null) AbsentLiveData.create()
        else counterRepository.findCounter(counterId)
    }

    val clickList: LiveData<List<ClickDto>> = counterId.switchMap { counterId ->
        if (counterId == null) AbsentLiveData.create()
        else clickRepository.findClickList(counterId)
    }

    val longestClick: Flow<ClickStatsDto> = counterId
        .asFlow()
        .flatMapLatest { counterId -> clickRepository.getLongestClick(counterId) }
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, ClickStatsDto.empty())

    val shortestClick: Flow<ClickStatsDto> = counterId
        .asFlow()
        .flatMapLatest { counterId -> clickRepository.getShortestClick(counterId) }
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, ClickStatsDto.empty())

    val vibrateEffect: Flow<Unit> = heldMillis
        .asFlow()
        .map { currentHeldMillis ->
            if (currentHeldMillis >= lastVibrationTriggerMillis + VIBRATION_INTERVAL_MS) {
                lastVibrationTriggerMillis = (currentHeldMillis / VIBRATION_INTERVAL_MS) * VIBRATION_INTERVAL_MS
            } else {
                null
            }
        }
        .filterNotNull()
        .shareIn(viewModelScope, SharingStarted.Lazily)

    private val timer: Timer = Timer()
    private var timerTask: TimerTask? = null
    private var btnHoldingStartTime = Date()
    private var initialClickCount = 0
    private var lastVibrationTriggerMillis: Long = 0

    fun initArguments(counterId: Long, initialClickCount: Int) {
        if (this.counterId.value == counterId)
            return

        this.counterId.value = counterId
        this.initialClickCount = initialClickCount
    }

    fun executeTask() {
        btnHoldingStartTime = Date()
        lastVibrationTriggerMillis = 0
        timerTask = timer.scheduleAtFixedRate(time = btnHoldingStartTime, period = 1) {
            val currentHeldMillis = System.currentTimeMillis().minus(btnHoldingStartTime.time)
            heldMillis.postValue(currentHeldMillis)
        }
    }

    fun cancelTask() {
        timerTask?.cancel()
        timerTask = null
        lastVibrationTriggerMillis = 0
    }

    fun createClick(isForce: Boolean) {
        if (isForce)
            return
        counterId.value?.let { counterId ->
            val click = ClickDto(
                createTime = btnHoldingStartTime,
                heldMillis = heldMillis.value ?: System.currentTimeMillis(),
                counterId = counterId,
            )
            createClick(click)
        }
    }

    private fun createClick(click: ClickDto) {
        viewModelScope.launch { clickRepository.createClick(click) }
    }

    fun updateCounter() {
        counter.value?.let { counter ->
            viewModelScope.launch {
                if (clickList.value?.size == initialClickCount) return@launch

                val updatedCounter = counter.copy(editTime = Date())
                counterRepository.updateCounter(updatedCounter)
            }
        }
    }

    fun removeClick(clickDto: ClickDto) {
        viewModelScope.launch { clickRepository.removeBy(clickDto) }
    }

    fun clearAllClick() {
        counterId.value?.let { counterId ->
            viewModelScope.launch {
                clickRepository.removeBy(counterId)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        timer.purge()
    }

    private companion object {
        const val VIBRATION_INTERVAL_MS = 10_000L
    }
}
