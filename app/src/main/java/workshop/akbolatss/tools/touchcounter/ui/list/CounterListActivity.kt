package workshop.akbolatss.tools.touchcounter.ui.list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.AndroidInjection
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.databinding.ActivityNavigationBinding
import workshop.akbolatss.tools.touchcounter.databinding.DialogOptionsBinding
import workshop.akbolatss.tools.touchcounter.databinding.NavHeaderBinding
import workshop.akbolatss.tools.touchcounter.ui.ViewModelFactory
import workshop.akbolatss.tools.touchcounter.ui.counter.ClickListActivity
import workshop.akbolatss.tools.touchcounter.utils.INTENT_COUNTER_ID
import workshop.akbolatss.tools.touchcounter.utils.SUPPORT_EMAIL
import workshop.akbolatss.tools.touchcounter.utils.android.DarkThemeDelegate
import javax.inject.Inject

class CounterListActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var darkThemeDelegate: DarkThemeDelegate

    private val viewModel: CounterListViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(CounterListViewModel::class.java)
    }

    private lateinit var binding: ActivityNavigationBinding
    private val navHeaderBinding by lazy {
        NavHeaderBinding.bind(binding.navigationView.getHeaderView(0))
    }
    private lateinit var adapter: CounterListRVA

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        observeViewModel()
        setListeners()
    }

    private fun initRecyclerView() {
        adapter = CounterListRVA(
            onCounterClickListener = {
                openCounterActivity(it)
            }, onCounterOptionsClickListener = {
                showPopupOptions(it)
            }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun openCounterActivity(counter: CounterDto) {
        val intent = Intent(this, ClickListActivity::class.java)
        intent.putExtra(INTENT_COUNTER_ID, counter.id)
        startActivity(intent)
    }

    private fun observeViewModel() {
        viewModel.counterList.observe(this, Observer { counters ->
            adapter.submitList(counters) {
                binding.recyclerView.smoothScrollToPosition(0)
            }
        })
        viewModel.statsLiveData.observe(this, Observer { stats ->
            navHeaderBinding.tvCountersCount.text = stats.countersCount.toString()
            navHeaderBinding.tvClicksCount.text = stats.clicksCount.toString()
            navHeaderBinding.tvLongClick.text = stats.longClick.toString()
            navHeaderBinding.tvMaxClickInCounter.text = stats.mostClicks.toString()
        })
        darkThemeDelegate.nightModeLive.observe(this, Observer { nightMode ->
            nightMode?.let {
                delegate.localNightMode = it
            }
        })
        darkThemeDelegate.isDarkThemeLive.observe(this, Observer { isDarkTheme ->
            isDarkTheme?.let {
                navHeaderBinding.darkThemeSwitch.isChecked = isDarkTheme
            }
        })
    }

    private fun setListeners() {
        binding.bottomBar.setNavigationOnClickListener {
            if (!binding.drawerLayout.isDrawerOpen(binding.navigationView))
                binding.drawerLayout.openDrawer(binding.navigationView)
            else
                binding.drawerLayout.closeDrawer(binding.navigationView)
        }
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.send_email -> sendEmail()
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                viewModel.loadStats()
            }
        })

        binding.fab.setOnClickListener {
            createNewCounter()
        }

        navHeaderBinding.darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            darkThemeDelegate.isDarkTheme = isChecked
        }
    }

    private fun sendEmail() {
        val mIntent = Intent(Intent.ACTION_SENDTO)
        mIntent.data = Uri.parse("mailto:")
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPPORT_EMAIL))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.nav_send_email_subject))
        startActivity(Intent.createChooser(mIntent, getString(R.string.nav_send_email_subject)))
    }

    private fun createNewCounter() {
        viewModel.createCounter(getString(R.string.default_name))
    }

    private fun showPopupOptions(counter: CounterDto) {
        val dialogOptionsBinding = DialogOptionsBinding.inflate(layoutInflater)
        dialogOptionsBinding.inputName.setText(counter.name)

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.options_title, counter.name))
            .setView(dialogOptionsBinding.root)
            .setNegativeButton(R.string.options_negative) { dialog, _ ->
                dialog.cancel()
            }
            .setNeutralButton(R.string.options_neutral) { _, _ ->
                showDeleteDialog(counter)
            }
            .setPositiveButton(R.string.options_positive) { _, _ ->
                val updatedCounter =
                    counter.copy(name = dialogOptionsBinding.inputName.text.toString())
                viewModel.updateCounter(updatedCounter)
            }
            .show()
    }

    private fun showDeleteDialog(counter: CounterDto) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.confirmation_delete_title))
            .setMessage(getString(R.string.confirmation_delete_message))
            .setPositiveButton(R.string.confirmation_delete_positive) { _, _ ->
                viewModel.deleteCounter(counter)
            }
            .setNegativeButton(R.string.confirmation_delete_negative) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}
