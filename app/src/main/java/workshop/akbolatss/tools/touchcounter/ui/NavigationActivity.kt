package workshop.akbolatss.tools.touchcounter.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import workshop.akbolatss.tools.touchcounter.*
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject
import workshop.akbolatss.tools.touchcounter.room.ClicksRepository
import workshop.akbolatss.tools.touchcounter.ui.counter.CounterActivity
import workshop.akbolatss.tools.touchcounter.ui.list.OnListCallback


class NavigationActivity : AppCompatActivity(), OnListCallback {

    private var currentTabTag: MutableLiveData<String> = MutableLiveData()

    private lateinit var repository: ClicksRepository

    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = Job()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     *
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        repository = ClicksRepository(ApplicationMain.instance.appDatabase.dataDao)

        navigation_view.setCheckedItem(0)
        navigateToTab(NavigationTab.LIST)

        initListeners()
    }

    private fun initListeners() {
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
                    bottom_bar.hideOnScroll = true
                    fab.show()
                }
            }
            menuItem.isChecked = true
            drawer_layout.closeDrawers()
            true
        }

        fab.setOnClickListener {
            createNewCounter()
        }
    }

    private fun createNewCounter() {
        uiScope.launch {
            val countersCount = repository.getCountersCount()
            repository.saveCounter(
                    CounterObject(
                            getCurrentTime(),
                            count = 0,
                            name = defaultName((countersCount + 1))
                    )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelJob.cancel()
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
        this.currentTabTag.value = currentTabTag
    }

    private fun hideCurrentTab() {
        val currentTabTag = this.currentTabTag.value
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentByTag(currentTabTag) ?: return
        fragmentManager.beginTransaction()
                .detach(currentFragment)
                .commitNow()
    }
}

