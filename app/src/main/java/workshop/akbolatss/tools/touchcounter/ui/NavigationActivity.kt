package workshop.akbolatss.tools.touchcounter.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.nav_header.view.*
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.ui.NavigationTab.ListCounters
import workshop.akbolatss.tools.touchcounter.utils.SUPPORT_EMAIL
import workshop.akbolatss.tools.touchcounter.utils.defaultName
import javax.inject.Inject


class NavigationActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: NavigationViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(NavigationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        setObservers()
        setListeners()
    }

    private fun setObservers() {
        viewModel.statsLiveData.observe(this, Observer { stats ->
            navigation_view.header.tv_counters_count.text = stats.countersCount.toString()
            navigation_view.header.tv_clicks_count.text = stats.clicksCount.toString()
            navigation_view.header.tv_long_click.text = stats.longClick.toString()
            navigation_view.header.tv_max_click_in_counter.text = stats.mostClicks.toString()
        })
        viewModel.currentTabTag.observe(this, Observer {
            when (it) {
                is ListCounters -> {
                    loadFragmentByNavigationState(it)
                }
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
                R.id.list_counter -> viewModel.currentTabTag.value = ListCounters
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

    private fun loadFragmentByNavigationState(state: NavigationTab) {
        detachCurrentFragment()

        val newFragment = supportFragmentManager.findFragmentByTag(state.fragmentTag)
        if (newFragment == null) {
            val newFragmentInstance = state.javaClass.newInstance()
            supportFragmentManager.beginTransaction().apply {
                add(R.id.container, newFragmentInstance, state.fragmentTag)
                commit()
            }
        } else {
            supportFragmentManager.beginTransaction().apply {
                attach(newFragment)
                commit()
            }
        }
        viewModel::previousTabTag.set(state.fragmentTag)
    }

    private fun detachCurrentFragment() {
        val currentFragment =
            supportFragmentManager.findFragmentByTag(viewModel.previousTabTag)
        currentFragment?.let {
            supportFragmentManager.beginTransaction().apply {
                detach(it)
                commitNow()
            }
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

}

