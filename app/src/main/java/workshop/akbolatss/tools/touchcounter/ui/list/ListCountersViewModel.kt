package workshop.akbolatss.tools.touchcounter.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
import workshop.akbolatss.tools.touchcounter.room.ClicksRepository
import workshop.akbolatss.tools.touchcounter.room.DataDao

class ListCountersViewModel : ViewModel() {
    private lateinit var repository: ClicksRepository

    lateinit var countersLiveData: LiveData<List<CounterObject>>

    fun processRepository(dataDao: DataDao) {
        repository = ClicksRepository(dataDao)

        countersLiveData = repository.getCounters()
    }

}