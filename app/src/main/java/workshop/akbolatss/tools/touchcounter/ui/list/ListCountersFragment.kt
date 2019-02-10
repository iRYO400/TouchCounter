package workshop.akbolatss.tools.touchcounter.ui.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_counters.*
import workshop.akbolatss.tools.touchcounter.ApplicationMain
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.utils.dp
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
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

        initRefresher()
        initAdapter()
        setObservers()
    }

    private fun initRefresher() {
//        refresher.setOnRefreshListener {
//            viewModel.loadData()
//        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ListCountersViewModel::class.java)
        viewModel.processRepository(ApplicationMain.instance.appDatabase.dataDao)

    }

    private fun initAdapter() {
        adapter = CounterAdapter { counter, position, clickType ->
            when (clickType) {
                ClickType.ITEM_CLICK -> {
                    callback.onListItemClick(counter)
                }
                ClickType.OPTIONS_CLICK -> {
                    showPopupOptions(counter)
                }
            }
        }
        recyclerView.adapter = adapter
    }

    private fun setObservers() {
        viewModel.countersLiveData.observe(this, Observer { list ->
            adapter.submitList(list)
        })
    }

    /**
     * Show Options dialog
     */
    private fun showPopupOptions(counter: CounterObject) {
        val layoutInflater = LayoutInflater.from(activity)
        val view = layoutInflater.inflate(R.layout.dialog_options, null)

        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.options_title, counter.name))
        builder.setView(view)

        val input = view.findViewById<TextInputEditText>(R.id.input_name)
        input.setText(counter.name)

        builder.setPositiveButton(R.string.options_positive) { dialog, which ->
            counter.name = input.text.toString()
            viewModel.updateCounter(counter)
        }

        builder.setNegativeButton(R.string.options_negative) { dialog, which ->
            dialog.cancel()
        }

        builder.setNeutralButton(R.string.options_neutral) { dialog, which ->
            showDeleteDialog(counter)
        }

        val alertDialog = builder.show()

        val buttons = intArrayOf(
            AlertDialog.BUTTON_POSITIVE,
            AlertDialog.BUTTON_NEGATIVE,
            AlertDialog.BUTTON_NEUTRAL
        )
        for (i in buttons) {
            var b: Button? = null
            try {
                b = alertDialog.getButton(i)
                b!!.setPadding(8.dp, 0, 8.dp, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Show Delete Dialog confirmation
     */
    private fun showDeleteDialog(counter: CounterObject) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.confirmation_delete_title))
        builder.setMessage(getString(R.string.confirmation_delete_message))
        builder.setPositiveButton(R.string.confirmation_delete_positive) { dialog, which ->
            viewModel.deleteCounter(counter)
        }

        builder.setNegativeButton(R.string.confirmation_delete_negative) { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }
}
