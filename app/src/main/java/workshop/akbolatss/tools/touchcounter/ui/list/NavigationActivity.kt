package workshop.akbolatss.tools.touchcounter.ui.list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.nav_header.view.*
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
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
        setObservers()
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

    private fun openCounterActivity(counter: CounterObject) {
        val intent = Intent(this, CounterActivity::class.java)
        intent.putExtra(INTENT_COUNTER_ID, counter.id)
        startActivity(intent)
    }

    private fun setObservers() {
        viewModel.counterList.observe(this, Observer { counters ->
            adapter.submitList(counters)
        })
        viewModel.statsLiveData.observe(this, Observer { stats ->
            navigation_view.header.tv_counters_count.text = stats.countersCount.toString()
            navigation_view.header.tv_clicks_count.text = stats.clicksCount.toString()
            navigation_view.header.tv_long_click.text = stats.longClick.toString()
            navigation_view.header.tv_max_click_in_counter.text = stats.mostClicks.toString()
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

    /**
     * Show Options dialog
     */
    private fun showPopupOptions(counter: CounterObject) {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.dialog_options, null)

        val builder = AlertDialog.Builder(this)
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

