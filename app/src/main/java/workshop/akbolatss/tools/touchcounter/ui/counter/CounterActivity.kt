package workshop.akbolatss.tools.touchcounter.ui.counter

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Property
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_counter.*
import workshop.akbolatss.tools.touchcounter.*
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject
import java.util.*


class CounterActivity : AppCompatActivity() {

    private var downTiming: Long = 0L
    private var holdTiming: Long = 0L

    private lateinit var viewModel: CounterViewModel

    private lateinit var adapter: ClickAdapter

    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)

        initViewModel()

        initRecyclerView()
        setObservers()
        setListeners()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(CounterViewModel::class.java)
        viewModel.processRepository(ApplicationMain.instance.appDatabase.dataDao)
        viewModel.processIntent(intent)
    }

    private fun initRecyclerView() {
        adapter = ClickAdapter { clickObject: ClickObject, position: Int ->

        }
        recyclerView.adapter = adapter
    }

    private fun setObservers() {
        viewModel.counterLiveData.observe(this, androidx.lifecycle.Observer { counterObject ->
            if (counterObject == null) {
                showToast { "Error of loading Counter " }
                onBackPressed()
            }
        })
        viewModel.clicksLiveData.observe(this, androidx.lifecycle.Observer { clickObjects ->
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
        animator = ObjectAnimator.ofInt(tv_timing, property, Color.RED, Color.BLUE, Color.GREEN)
        animator.duration = 30000L
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
            popupView.showAsDropDownDef(v)
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
