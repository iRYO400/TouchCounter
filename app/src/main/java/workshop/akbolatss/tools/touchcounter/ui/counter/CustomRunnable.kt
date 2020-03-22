package workshop.akbolatss.tools.touchcounter.ui.counter

import android.os.Handler
import android.widget.TextView
import workshop.akbolatss.tools.touchcounter.utils.convertTimeSeconds
import java.util.*

class CustomRunnable(private val handler: Handler, var holder: TextView) : Runnable {

    private var initialTimestamp: Long = 0

    override fun run() {
        holder.text =
            convertTimeSeconds(initialTimestamp)
        handler.postDelayed(this, 10000)
    }

    fun init(textView: TextView, initTime: Date) {
        holder = textView
        initialTimestamp = initTime.time
    }
}