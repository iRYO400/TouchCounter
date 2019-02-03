package workshop.akbolatss.tools.touchcounter

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import java.sql.Timestamp
import java.text.ParseException
import java.util.*

fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

public const val TAG = "COUNTER_TAG"

const val INTENT_COUNTER_ID = "counterId"


// Quick & dirty logcat extensions
inline fun <reified T> T.logd(message: () -> String) = Log.d(T::class.java.simpleName, message())

inline fun Context.showToast(message: () -> String) = Toast.makeText(this, message(), Toast.LENGTH_LONG).show()

inline fun <reified T> T.loge(error: Throwable, message: () -> String) = Log.d(T::class.java.simpleName, message(), error)

fun Context.defaultName(index: Int): String {
    return getString(R.string.default_name, index)
}

fun convertTime(timestamp: Long): String {
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