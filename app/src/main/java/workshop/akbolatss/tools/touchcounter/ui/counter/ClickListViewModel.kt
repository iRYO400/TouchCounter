package workshop.akbolatss.tools.touchcounter.ui.counter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
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

    private val timer: Timer = Timer()
    private var timerTask: TimerTask? = null
    private var btnHoldingStartTime = Date()
    private var initialClickCount = 0

    val heldMillis = MutableLiveData<Long>()

    fun initArguments(counterId: Long, initialClickCount: Int) {
        if (this.counterId.value == counterId)
            return

        this.counterId.value = counterId
        this.initialClickCount = initialClickCount
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

    companion object {
        val COUNTER_ID = object : CreationExtras.Key<Long> {}
    }
}
