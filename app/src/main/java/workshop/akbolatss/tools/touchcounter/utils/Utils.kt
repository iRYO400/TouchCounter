package workshop.akbolatss.tools.touchcounter.utils

import android.content.Context
import android.content.res.Resources
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import workshop.akbolatss.tools.touchcounter.R
import java.sql.Timestamp
import java.text.ParseException
import java.util.*

const val INTENT_COUNTER_ID = "counterId"
const val SUPPORT_EMAIL = "whitesteel400@gmail.com"

inline fun Context.showToast(message: () -> String) =
    Toast.makeText(this, message(), Toast.LENGTH_LONG).show()

fun Context.defaultName(index: Int): String {
    return getString(R.string.default_name, index)
}

fun convertTime(timestamp: Long): String {
    val date = Date(Timestamp(timestamp).time)
    return try {
        val niceDateStr = DateUtils.getRelativeTimeSpanString(
            date.time,
            Calendar.getInstance().timeInMillis,
            DateUtils.MINUTE_IN_MILLIS
        )
        niceDateStr.toString()
    } catch (e: ParseException) {
        e.printStackTrace()
        Log.e("ParseException", "Unparseable date " + e.message)
        timestamp.toString()
    }
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()


fun convertTimeSeconds(timestamp: Long): String {
    val date = Date(Timestamp(timestamp).time)
    return try {
        val niceDateStr = DateUtils.getRelativeTimeSpanString(
            date.time,
            Calendar.getInstance().timeInMillis,
            DateUtils.SECOND_IN_MILLIS
        )
        niceDateStr.toString()
    } catch (e: ParseException) {
        e.printStackTrace()
        Log.e("ParseException", "Unparseable date " + e.message)
        timestamp.toString()
    }
}

fun getCurrentTime(): Long {
    return System.currentTimeMillis()
}