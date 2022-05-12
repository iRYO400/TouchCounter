package workshop.akbolatss.tools.touchcounter.utils.exts

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.init(t: T?): MutableLiveData<T> {
    this.postValue(t)
    return this
}

fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()