package workshop.akbolatss.tools.touchcounter.ui.list

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
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
import workshop.akbolatss.tools.touchcounter.utils.android.IUserPreferencesDelegate
import javax.inject.Inject

class CounterListActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var darkThemeDelegate: DarkThemeDelegate

    @Inject
    lateinit var userPreferencesDelegate: IUserPreferencesDelegate

    private val viewModel: CounterListViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[CounterListViewModel::class.java]
    }

    private lateinit var binding: ActivityNavigationBinding

    private val navHeaderBinding by lazy {
        NavHeaderBinding.bind(binding.navigationView.getHeaderView(0))
    }

    private lateinit var adapter: CounterListRVA

    private var isInSelectionMode = false

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                adapter.clearSelection()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnBackPressed()
        setupWindowInsets()
        initView()
        initRecyclerView()
        observeViewModel()
        setListeners()
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34+
                    insets.getInsets(WindowInsetsCompat.Type.systemBars())
                } else {
                    insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
                }

            with(binding) {
                recyclerView.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
                navigationView.updatePadding(top = systemBars.top)
            }

            insets
        }
    }

    private fun initView() {
        navHeaderBinding.switchUseSeconds.isChecked = userPreferencesDelegate.isUseSecondsEnabled()
    }

    private fun initRecyclerView() {
        adapter = CounterListRVA(
            onCounterClickListener = {
                if (!isInSelectionMode) {
                    openCounterActivity(it)
                }
            },
            onCounterOptionsClickListener = {
                if (!isInSelectionMode) {
                    showPopupOptions(it)
                }
            },
            onSelectionStateChanged = { isInSelectionMode, selectedCount ->
                handleSelectionStateChanged(isInSelectionMode, selectedCount)
            }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun handleSelectionStateChanged(isInSelectionMode: Boolean, selectedCount: Int) {
        this.isInSelectionMode = isInSelectionMode
        onBackPressedCallback.isEnabled = isInSelectionMode

        with(binding) {
            if (isInSelectionMode) {
                fab.hide()
                bottomBar.navigationIcon = ContextCompat.getDrawable(
                    this@CounterListActivity,
                    R.drawable.ic_rounded_close_24,
                )
                bottomBar.replaceMenu(R.menu.selection_actions_menu)
                bottomBar.title = getString(R.string.selected_items_count, selectedCount)
                bottomBar.setNavigationOnClickListener {
                    adapter.clearSelection()
                }
            } else {
                fab.show()
                bottomBar.navigationIcon = ContextCompat.getDrawable(
                        this@CounterListActivity,
                        R.drawable.ic_rounded_menu_24,
                    )
                bottomBar.menu.clear()
                bottomBar.title = getString(R.string.app_name)
                bottomBar.setNavigationOnClickListener {
                    if (!binding.drawerLayout.isDrawerOpen(navigationView)) {
                        drawerLayout.openDrawer(navigationView)
                    } else {
                        drawerLayout.closeDrawer(navigationView)
                    }
                }
            }
        }
    }

    private fun openCounterActivity(counter: CounterDto) {
        val intent = Intent(this, ClickListActivity::class.java)
        intent.putExtra(INTENT_COUNTER_ID, counter.id)
        startActivity(intent)
    }

    private fun observeViewModel() {
        viewModel.counterList.observe(this) { counters ->
            adapter.submitList(counters) {
                if (!isInSelectionMode) {
                    binding.recyclerView.smoothScrollToPosition(0)
                }
            }
        }
        viewModel.statsLiveData.observe(this) { stats ->
            navHeaderBinding.tvCountersCount.text = stats.countersCount.toString()
            navHeaderBinding.tvClicksCount.text = stats.clicksCount.toString()
            navHeaderBinding.tvLongClick.text = stats.longClick.toString()
            navHeaderBinding.tvShortClick.text = stats.shortClick.toString()
            navHeaderBinding.tvMaxClickInCounter.text = stats.mostClicks.toString()
        }
        darkThemeDelegate.nightModeLive.observe(this) { nightMode ->
            nightMode?.let {
                delegate.localNightMode = it
            }
        }
        darkThemeDelegate.isDarkThemeLive.observe(this) { isDarkTheme ->
            isDarkTheme?.let {
                navHeaderBinding.darkThemeSwitch.isChecked = isDarkTheme
            }
        }
    }

    private fun setListeners() {
        binding.bottomBar.setNavigationOnClickListener {
            if (!binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
                binding.drawerLayout.openDrawer(binding.navigationView)
            } else {
                binding.drawerLayout.closeDrawer(binding.navigationView)
            }
        }

        binding.bottomBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_delete_selected -> {
                    val selectedIds = adapter.getSelectedCounterIds()
                    if (selectedIds.isNotEmpty()) {
                        showDeleteSelectedDialog(selectedIds)
                    }
                    true
                }

                R.id.action_wipe_selected -> {
                    val selectedIds = adapter.getSelectedCounterIds()
                    if (selectedIds.isNotEmpty()) {
                        showWipeSelectedDialog(selectedIds)
                    }
                    true
                }

                else -> false
            }
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            if (isInSelectionMode) return@setNavigationItemSelectedListener false

            when (menuItem.itemId) {
                R.id.send_email -> sendEmail()
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                if (!isInSelectionMode) { // Only load stats if not in selection mode
                    viewModel.loadStats()
                }
            }
        })

        binding.fab.setOnClickListener {
            createNewCounter()
        }

        navHeaderBinding.darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            darkThemeDelegate.isDarkTheme = isChecked
        }
        navHeaderBinding.switchUseSeconds.setOnCheckedChangeListener { _, isChecked ->
            userPreferencesDelegate.saveUseSecondsState(isChecked)
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
            .setNeutralButton(R.string.options_delete) { _, _ ->
                showDeleteDialog(counter)
            }
            .setPositiveButton(R.string.options_save) { _, _ ->
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
            .setPositiveButton(R.string.options_delete) { _, _ ->
                viewModel.deleteCounter(counter)
            }
            .setNegativeButton(R.string.options_negative) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun showDeleteSelectedDialog(selectedIds: List<Long>) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.confirmation_delete_selected_title))
            .setMessage(getString(R.string.confirmation_delete_selected_message, selectedIds.size))
            .setPositiveButton(R.string.confirmation_action_positive_delete) { _, _ ->
                viewModel.deleteCounters(selectedIds)
                adapter.clearSelection()
            }
            .setNegativeButton(R.string.options_negative) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun showWipeSelectedDialog(selectedIds: List<Long>) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.confirmation_wipe_selected_title))
            .setMessage(getString(R.string.confirmation_wipe_selected_message, selectedIds.size))
            .setPositiveButton(R.string.confirmation_action_positive_wipe) { _, _ ->
                viewModel.wipeClicksForCounters(selectedIds)
                adapter.clearSelection()
            }
            .setNegativeButton(R.string.options_negative) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}
