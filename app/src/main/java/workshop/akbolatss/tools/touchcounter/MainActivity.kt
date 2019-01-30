package workshop.akbolatss.tools.touchcounter

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.util.Property
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private var downTiming: Long = 0L
    private var holdTiming: Long = 0L

    private val list = MutableLiveData<ArrayList<ClickObject>>().default(ArrayList())

    private lateinit var adapter: CounterAdapter

    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initRecyclerView()
        setObservers()
        setListeners()
    }

    private fun initRecyclerView() {
        adapter = CounterAdapter(list) { clickObject: ClickObject, position: Int ->

        }
        recyclerView.adapter = adapter
    }

    private fun setObservers() {
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

    private fun executeTimer() {
        timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                val currentTiming = System.currentTimeMillis()
                holdTiming = currentTiming.minus(downTiming)
                runOnUiThread {
                    tv_timing.text = holdTiming.toString() + " ms"
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask, 0, 1)
    }

    private fun addCounter() {
        val clickObject = ClickObject(downTiming, holdTiming, adapter.itemCount)
        tv_counter.text = adapter.itemCount.toString()
        list.value!!.add(clickObject)
        adapter.notifyItemInserted(adapter.itemCount)
        recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
    }

    private fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }
}
