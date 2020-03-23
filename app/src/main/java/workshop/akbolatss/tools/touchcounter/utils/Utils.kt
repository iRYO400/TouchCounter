package workshop.akbolatss.tools.touchcounter.utils

import android.content.Context
import android.content.res.Resources
import android.text.format.DateUtils
import android.widget.Toast
import timber.log.Timber
import workshop.akbolatss.tools.touchcounter.R
import java.text.ParseException
import java.util.*

const val INTENT_COUNTER_ID = "counterId"
const val SUPPORT_EMAIL = "whitesteel400@gmail.com"

inline fun Context.showToast(message: () -> String) =
    Toast.makeText(this, message(), Toast.LENGTH_LONG).show()

fun Context.defaultName(): String {
    return getString(R.string.default_name)
}

fun String.appendIndex(index: Int): String {
    return "$this $index"
}

fun Date.formatAsRelativeInMinutes(): String {
    return try {
        DateUtils.getRelativeTimeSpanString(
            this.time,
            Calendar.getInstance().timeInMillis,
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    } catch (e: ParseException) {
        Timber.e(e)
        this.time.toString()
    }
}

fun Date.formatAsRelativeInSeconds(): String {
    return try {
        DateUtils.getRelativeTimeSpanString(
            this.time,
            Calendar.getInstance().timeInMillis,
            DateUtils.SECOND_IN_MILLIS
        ).toString()
    } catch (e: ParseException) {
        Timber.e(e)
        this.time.toString()
    }
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()


fun getCurrentTime() = Date()