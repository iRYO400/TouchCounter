package workshop.akbolatss.tools.touchcounter.ui.counter

import android.os.Handler
import android.widget.TextView
import workshop.akbolatss.tools.touchcounter.utils.formatAsRelativeInSeconds
import java.util.Date

class CustomRunnable(private val handler: Handler, var holder: TextView) : Runnable {

    private var initialTimestamp: Date = Date()

    override fun run() {
        holder.text = initialTimestamp.formatAsRelativeInSeconds()
        handler.postDelayed(this, 10000)
    }

    fun init(textView: TextView, initTime: Date) {
        holder = textView
        initialTimestamp = initTime
    }
}