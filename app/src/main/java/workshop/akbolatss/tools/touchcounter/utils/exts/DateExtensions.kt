package workshop.akbolatss.tools.touchcounter.utils.exts

import android.text.format.DateUtils
import java.text.ParseException
import java.util.Calendar
import java.util.Date
import timber.log.Timber

fun Date.formatAsRelativeInMinutes(): String {
    return try {
        DateUtils.getRelativeTimeSpanString(
            this.time,
            Calendar.getInstance().timeInMillis,
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE,
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
            DateUtils.SECOND_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE,
        ).toString()
    } catch (e: ParseException) {
        Timber.e(e)
        this.time.toString()
    }
}
