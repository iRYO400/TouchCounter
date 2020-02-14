package workshop.akbolatss.tools.touchcounter.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.nav_header.view.*
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
import workshop.akbolatss.tools.touchcounter.room.AppDataBase
import workshop.akbolatss.tools.touchcounter.ui.counter.CounterActivity
import workshop.akbolatss.tools.touchcounter.ui.list.OnListCallback
import workshop.akbolatss.tools.touchcounter.utils.INTENT_COUNTER_ID
import workshop.akbolatss.tools.touchcounter.utils.SUPPORT_EMAIL


class NavigationActivity : AppCompatActivity(), OnListCallback {

    private lateinit var viewModel: NavigationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        initViewModel()

        navigation_view.setCheckedItem(0)
        navigateToTab(NavigationTab.LIST)

        setObservers()
        setListeners()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(NavigationViewModel::class.java)
        viewModel.processRepository(AppDataBase.getInstance(this).dataDao)
    }

    private fun setObservers() {
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
            val id = menuItem.itemId
            when (id) {
                R.id.list_counter -> {
                    navigateToTab(NavigationTab.LIST)
                    menuItem.isChecked = true
                }
                R.id.send_email -> {
                    sendEmail()
                }
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
        viewModel.createCounter(this)
    }

    override fun onListItemClick(counterObject: CounterObject) {
        openCounterActivity(counterObject)
    }

    private fun openCounterActivity(counterObject: CounterObject) {
        val intent = Intent(this, CounterActivity::class.java)
        intent.putExtra(INTENT_COUNTER_ID, counterObject.id)
        startActivity(intent)
    }

    private fun navigateToTab(navigationTab: NavigationTab) {
        hideCurrentTab()

        val currentTabTag = navigationTab.fragmentTag
        val fragmentManager = supportFragmentManager
        var fragment = fragmentManager.findFragmentByTag(currentTabTag)
        val transaction = fragmentManager.beginTransaction()
        if (fragment == null) {
            fragment = navigationTab.navigationTabFactory.newInstance()
            transaction.add(container.id, fragment, currentTabTag)
                .commit()
        } else {
            transaction.attach(fragment)
                .commit()
        }
        viewModel.currentTabTag.value = currentTabTag
    }

    private fun hideCurrentTab() {
        val currentTabTag = viewModel.currentTabTag.value
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentByTag(currentTabTag) ?: return
        fragmentManager.beginTransaction()
            .detach(currentFragment)
            .commitNow()
    }
}

