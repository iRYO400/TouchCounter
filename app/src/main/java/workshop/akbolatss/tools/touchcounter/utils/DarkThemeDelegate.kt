package workshop.akbolatss.tools.touchcounter.utils

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DarkThemeDelegate(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        const val SHARED_NIGHT_MODE = "_nightMode"
        const val DEFAULT_NIGHT_MODE = AppCompatDelegate.MODE_NIGHT_NO
    }

    private val nightMode: Int
        get() = sharedPreferences.getInt(SHARED_NIGHT_MODE, DEFAULT_NIGHT_MODE)

    private val _nightModelLive = MutableLiveData<Int>()
    val nightModeLive: LiveData<Int>
        get() = _nightModelLive

    var isDarkTheme = false
        get() = nightMode == AppCompatDelegate.MODE_NIGHT_YES
        set(value) {
            sharedPreferences.edit().putInt(
                SHARED_NIGHT_MODE, if (value) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            ).apply()
            field = value
        }

    private val _isDarkThemeLive = MutableLiveData<Boolean>()
    val isDarkThemeLive: LiveData<Boolean>
        get() = _isDarkThemeLive

    private val preferenceChangedListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                SHARED_NIGHT_MODE -> {
                    _nightModelLive.value = nightMode
                    _isDarkThemeLive.value = isDarkTheme
                }
            }
        }

    init {
        _nightModelLive.value = nightMode
        _isDarkThemeLive.value = isDarkTheme

        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangedListener)
    }
}