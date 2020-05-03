package workshop.akbolatss.tools.touchcounter.ui.counter

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Property
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_counter.*
import timber.log.Timber
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.ui.ViewModelFactory
import workshop.akbolatss.tools.touchcounter.ui.counter.ClickAdapter.Companion.ITEM_POSITION_CHANGED
import workshop.akbolatss.tools.touchcounter.utils.DarkThemeDelegate
import workshop.akbolatss.tools.touchcounter.utils.INTENT_COUNTER_ID
import workshop.akbolatss.tools.touchcounter.utils.PopupView
import workshop.akbolatss.tools.touchcounter.utils.toast
import javax.inject.Inject

class CounterActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var darkThemeDelegate: DarkThemeDelegate

    private val viewModel: CounterViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(CounterViewModel::class.java)
    }

    private lateinit var adapter: ClickAdapter

    private lateinit var animator: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)
        initViewModel()

        initRecyclerView()
        initAnimator()

        observeViewModel()
        setListeners()
        Timber.d("onCreate")
    }

    private fun initViewModel() {
        val counterId = intent.getLongExtra(INTENT_COUNTER_ID, -1)
        viewModel.setCounterId(counterId)
    }

    private fun initRecyclerView() {
        adapter = ClickAdapter()
        recyclerView.adapter = adapter
        val itemSwipeHelper = SwipeToDeleteCallback { clickPos ->
            val click = adapter.currentList[clickPos]
            click?.let {
                viewModel.removeClick(it)
            }
        }
        ItemTouchHelper(itemSwipeHelper).attachToRecyclerView(recyclerView)
    }

    private fun initAnimator() {
        animator = ObjectAnimator.ofInt(
            tv_timing, property,
            ContextCompat.getColor(this, R.color.md_blue_grey_500),
            ContextCompat.getColor(this, R.color.md_blue_500),
            ContextCompat.getColor(this, R.color.md_red_500),
            ContextCompat.getColor(this, R.color.md_green_500),
            ContextCompat.getColor(this, R.color.md_grey_500),
            ContextCompat.getColor(this, R.color.blue)
        ).apply {
            duration = 20000L
            setEvaluator(ArgbEvaluator())
            interpolator = DecelerateInterpolator(2f)
        }
    }

    private fun observeViewModel() {
        viewModel.counter.observe(this, Observer { counterDto ->
            if (counterDto == null) {
                toast("Error occurred, please try open again")
                onBackPressed()
            }
        })
        viewModel.clickList.observe(this, Observer { clicks ->
            adapter.submitList(clicks) {
                adapter.notifyItemRangeChanged(
                    0, clicks.size,
                    ITEM_POSITION_CHANGED
                )
                tv_counter.text = clicks.size.toString()
                recyclerView.smoothScrollToPosition(clicks.size)
            }
        })
        viewModel.heldMillis.observe(this, Observer { millis ->
            tv_timing.text = getString(R.string.millis, millis)
        })
        darkThemeDelegate.nightModeLive.observe(this, Observer { nightMode ->
            nightMode?.let {
                delegate.localNightMode = it
            }
        })
    }

    private fun setListeners() {
        setupPopupView()
        btnClick.setOnTouchListener { _, event ->
            return@setOnTouchListener when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    btnHold()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    btnRelease()
                    true
                }
                else -> false
            }
        }
    }

    private fun btnHold() {
        btnClick.isPressed = true
        animator.start()
        viewModel.executeTask()
    }

    private fun btnRelease(isForce: Boolean = false) {
        viewModel.cancelTask()
        viewModel.createClick(isForce)
        animator.cancel()
        btnClick.isPressed = false
    }

    private fun setupPopupView() {
        val popupView = PopupView(this)
        info_timing.setOnClickListener { v ->
            removeFocusFromCurrent()
            popupView.showPopup(v)
        }
    }

    private fun removeFocusFromCurrent() {
        val currentFocus = window.currentFocus
        currentFocus?.clearFocus()
    }

    override fun onPause() {
        btnRelease(true)
        viewModel.updateCounter()
        super.onPause()
    }

    private val property: Property<TextView, Int> =
        object : Property<TextView, Int>(Int::class.javaPrimitiveType, "textColor") {
            override operator fun get(`object`: TextView): Int {
                return `object`.currentTextColor
            }

            override operator fun set(`object`: TextView, value: Int) {
                `object`.setTextColor(value)
            }
        }
}