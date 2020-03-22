package workshop.akbolatss.tools.touchcounter.ui.counter

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Property
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_counter.*
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import workshop.akbolatss.tools.touchcounter.ui.ViewModelFactory
import workshop.akbolatss.tools.touchcounter.utils.INTENT_COUNTER_ID
import workshop.akbolatss.tools.touchcounter.utils.PopupView
import workshop.akbolatss.tools.touchcounter.utils.getCurrentTime
import workshop.akbolatss.tools.touchcounter.utils.showToast
import java.util.*
import javax.inject.Inject


class CounterActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: CounterViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(CounterViewModel::class.java)
    }

    private lateinit var adapter: ClickAdapter

    private lateinit var timer: Timer

    private var downTiming: Long = 0L
    private var holdTiming: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)

        initViewModel()

        initRecyclerView()
        setObservers()
        setListeners()
    }

    private fun initViewModel() {
        val counterId = intent.getLongExtra(INTENT_COUNTER_ID, -1)
        viewModel.setCounterId(counterId)
    }

    private fun initRecyclerView() {
        adapter = ClickAdapter()
        recyclerView.adapter = adapter
    }

    private fun setObservers() {
        viewModel.counter.observe(this, Observer { counterObject ->
            if (counterObject == null) {
                showToast { "Error of loading Counter " }
                onBackPressed()
            }
        })
        viewModel.clickList.observe(this, Observer { clickObjects ->
            adapter.submitList(clickObjects)
            tv_counter.text = clickObjects.size.toString()
            recyclerView.smoothScrollToPosition(adapter.itemCount)
        })
    }

    private lateinit var animator: ObjectAnimator

    private val property: Property<TextView, Int> =
        object : Property<TextView, Int>(Int::class.javaPrimitiveType, "textColor") {
            override operator fun get(`object`: TextView): Int? {
                return `object`.currentTextColor
            }

            override operator fun set(`object`: TextView, value: Int?) {
                `object`.setTextColor(value!!)
            }
        }

    private fun setListeners() {
        animator = ObjectAnimator.ofInt(
            tv_timing, property,
            ContextCompat.getColor(this, R.color.md_blue_grey_500),
            ContextCompat.getColor(this, R.color.md_blue_500),
            ContextCompat.getColor(this, R.color.md_red_500),
            ContextCompat.getColor(this, R.color.md_green_500),
            ContextCompat.getColor(this, R.color.md_grey_500),
            ContextCompat.getColor(this, R.color.colorAccent)
        )
        animator.duration = 20000L
        animator.setEvaluator(ArgbEvaluator())
        animator.interpolator = DecelerateInterpolator(2f)

        setupPopupView(info_timing)

        button.setOnTouchListener { _, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    downTiming = getCurrentTime()
                    executeTimer()
                    animator.start()
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    timer.cancel()
                    animator.cancel()
                    addCounter()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener true
        }
    }


    private fun setupPopupView(icon: AppCompatImageView) {
        val popupView = PopupView(this)
        icon.setOnClickListener { v ->
            removeFocusFromCurrent()
            popupView.showPopup(v)
        }
    }

    private fun removeFocusFromCurrent() {
        val currentFocus = window.currentFocus
        currentFocus?.clearFocus()
    }

    private fun executeTimer() {
        timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                val currentTiming = System.currentTimeMillis()
                holdTiming = currentTiming.minus(downTiming)
                runOnUiThread {
                    tv_timing.text = "$holdTiming ms"
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask, 0, 1)
    }

    private fun addCounter() {
        val index = adapter.itemCount + 1
        val clickObject = ClickObject(downTiming, holdTiming, index)
        viewModel.addClickObject(clickObject)
    }

    override fun onPause() {
        viewModel.updateCounter()
        super.onPause()
    }
}
