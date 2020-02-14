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
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_counters.*
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
import workshop.akbolatss.tools.touchcounter.room.AppDataBase
import workshop.akbolatss.tools.touchcounter.ui.NavigationActivity
import workshop.akbolatss.tools.touchcounter.utils.dp


class ListCountersFragment : Fragment() {

    companion object {
        fun newInstance() = ListCountersFragment()
    }

    private lateinit var callback: OnListCallback

    private lateinit var viewModel: ListCountersViewModel

    private lateinit var adapter: CounterAdapter

    override fun onAttach(context: Context) {
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
        viewModel = ViewModelProvider(this).get(ListCountersViewModel::class.java)
        context?.let {
            viewModel.processRepository(AppDataBase.getInstance(it).dataDao)
        }
    }

    private fun initAdapter() {
        adapter = CounterAdapter { counter, _, clickType ->
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
        viewModel.countersLiveData.observe(viewLifecycleOwner, Observer { list ->
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

        builder.setPositiveButton(R.string.options_positive) { _, _ ->
            counter.name = input.text.toString()
            viewModel.updateCounter(counter)
        }

        builder.setNegativeButton(R.string.options_negative) { dialog, _ ->
            dialog.cancel()
        }

        builder.setNeutralButton(R.string.options_neutral) { _, _ ->
            showDeleteDialog(counter)
        }

        val alertDialog = builder.show()

        val buttons = intArrayOf(
            AlertDialog.BUTTON_POSITIVE,
            AlertDialog.BUTTON_NEGATIVE,
            AlertDialog.BUTTON_NEUTRAL
        )
        for (i in buttons) {
            var b: Button?
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
        builder.setPositiveButton(R.string.confirmation_delete_positive) { _, _ ->
            viewModel.deleteCounter(counter)
        }

        builder.setNegativeButton(R.string.confirmation_delete_negative) { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }
}
