package workshop.akbolatss.tools.touchcounter.ui.counter

import android.os.Handler
import android.widget.TextView
import workshop.akbolatss.tools.touchcounter.convertTime

class CustomRunnable(private val handler: Handler, var holder: TextView) : Runnable {

    private var initialTimestamp: Long = 0

    override fun run() {
        holder.text = convertTime(initialTimestamp)
        handler.postDelayed(this, 10000)
    }

    fun init(textView: TextView, timestamp: Long) {
        holder = textView
        initialTimestamp = timestamp
    }
}