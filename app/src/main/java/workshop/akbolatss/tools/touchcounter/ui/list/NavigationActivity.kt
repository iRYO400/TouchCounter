package workshop.akbolatss.tools.touchcounter.ui.list

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.nav_header.view.*
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.ui.ViewModelFactory
import workshop.akbolatss.tools.touchcounter.ui.counter.CounterActivity
import workshop.akbolatss.tools.touchcounter.utils.INTENT_COUNTER_ID
import workshop.akbolatss.tools.touchcounter.utils.SUPPORT_EMAIL
import workshop.akbolatss.tools.touchcounter.utils.defaultName
import workshop.akbolatss.tools.touchcounter.utils.dp
import javax.inject.Inject

class NavigationActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: NavigationViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(NavigationViewModel::class.java)
    }

    private lateinit var adapter: CounterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        initRecyclerView()
        observeViewModel()
        setListeners()
    }

    private fun initRecyclerView() {
        adapter = CounterAdapter { counter, _, clickType ->
            when (clickType) {
                ClickType.ITEM_CLICK -> {
                    openCounterActivity(counter)
                }
                ClickType.OPTIONS_CLICK -> {
                    showPopupOptions(counter)
                }
            }
        }
        recyclerView.adapter = adapter
    }

    private fun openCounterActivity(counter: CounterDto) {
        val intent = Intent(this, CounterActivity::class.java)
        intent.putExtra(INTENT_COUNTER_ID, counter.id)
        startActivity(intent)
    }

    private fun observeViewModel() {
        viewModel.counterList.observe(this, Observer { counters ->
            adapter.submitList(counters) {
                recyclerView.smoothScrollToPosition(0)
            }
        })
        viewModel.statsLiveData.observe(this, Observer { stats ->
            navigation_view.header?.let {
                it.tv_counters_count.text = stats.countersCount.toString()
                it.tv_clicks_count.text = stats.clicksCount.toString()
                it.tv_long_click.text = stats.longClick.toString()
                it.tv_max_click_in_counter.text = stats.mostClicks.toString()
            }
        })
    }

    private fun setListeners() {
        bottom_bar.setNavigationOnClickListener {
            if (!drawer_layout.isDrawerOpen(navigation_view))
                drawer_layout.openDrawer(navigation_view)
            else
                drawer_layout.closeDrawer(navigation_view)
        }

        navigation_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.send_email -> sendEmail()
            }
            drawer_layout.closeDrawers()
            true
        }

        drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerOpened(drawerView: View) {
                viewModel.loadStats()
            }
        })

        fab.setOnClickListener {
            createNewCounter()
        }

        navigation_view.getHeaderView(0).darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            else
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        }
    }

    private fun sendEmail() {
        val mIntent = Intent(Intent.ACTION_SENDTO)
        mIntent.data = Uri.parse("mailto:")
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPPORT_EMAIL))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.nav_send_email_helper))
        startActivity(Intent.createChooser(mIntent, getString(R.string.nav_send_email_helper)))
    }

    private fun createNewCounter() {
        viewModel.createCounter(defaultName())
    }

    @SuppressLint("InflateParams")
    private fun showPopupOptions(counter: CounterDto) {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.dialog_options, null)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.options_title, counter.name))
        builder.setView(view)

        val input = view.findViewById<TextInputEditText>(R.id.input_name)
        input.setText(counter.name)

        builder.setPositiveButton(R.string.options_positive) { _, _ ->
            val updatedCounter = counter.copy(name = input.text.toString())
            viewModel.updateCounter(updatedCounter)
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

    private fun showDeleteDialog(counter: CounterDto) {
        val builder = AlertDialog.Builder(this)
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
