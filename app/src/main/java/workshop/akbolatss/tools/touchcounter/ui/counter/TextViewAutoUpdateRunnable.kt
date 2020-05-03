package workshop.akbolatss.tools.touchcounter.ui.counter

import android.os.Handler
import android.widget.TextView
import workshop.akbolatss.tools.touchcounter.utils.formatAsRelativeInSeconds
import java.util.Date

class TextViewAutoUpdateRunnable(
    private val handler: Handler,
    var tvLastUpdate: TextView
) : Runnable {

    private var initialTimestamp: Date = Date()

    override fun run() {
        tvLastUpdate.text = initialTimestamp.formatAsRelativeInSeconds()
        handler.postDelayed(this, 10000)
    }

    fun init(textView: TextView, initTime: Date) {
        tvLastUpdate = textView
        initialTimestamp = initTime
    }
}