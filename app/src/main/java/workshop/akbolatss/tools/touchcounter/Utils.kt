package workshop.akbolatss.tools.touchcounter

import androidx.lifecycle.MutableLiveData

fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

public const val TAG = "COUNTER_TAG"
