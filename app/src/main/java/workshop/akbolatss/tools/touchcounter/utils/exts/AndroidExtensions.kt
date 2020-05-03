package workshop.akbolatss.tools.touchcounter.utils.exts

import android.content.Context
import android.content.res.Resources
import android.widget.Toast
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.init(t: T?): MutableLiveData<T> {
    this.postValue(t)
    return this
}

fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()