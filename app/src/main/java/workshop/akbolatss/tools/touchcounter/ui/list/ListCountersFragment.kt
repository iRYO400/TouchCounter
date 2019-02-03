package workshop.akbolatss.tools.touchcounter.ui.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_counters.*
import workshop.akbolatss.tools.touchcounter.ApplicationMain
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.room.ClicksRepository
import workshop.akbolatss.tools.touchcounter.ui.NavigationActivity

class ListCountersFragment : Fragment() {

    companion object {
        fun newInstance() = ListCountersFragment()
    }

    private lateinit var callback: OnListCallback

    private lateinit var viewModel: ListCountersViewModel

    private lateinit var adapter: CounterAdapter


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = (context as NavigationActivity)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_counters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()

        initAdapter()
        setObservers()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ListCountersViewModel::class.java)
        viewModel.processRepository(ApplicationMain.instance.appDatabase.dataDao)

    }

    private fun initAdapter() {
        adapter = CounterAdapter { counter, position ->
            callback.onListItemClick(counter)
        }
        recyclerView.adapter = adapter
    }

    private fun setObservers() {
        viewModel.countersLiveData.observe(this, Observer { list ->
            adapter.submitList(list)
        })
    }
}
