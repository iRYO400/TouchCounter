package workshop.akbolatss.tools.touchcounter.utils.android

import android.content.SharedPreferences
import androidx.core.content.edit

interface IUserPreferencesDelegate {
    fun saveUseSecondsState(useSeconds: Boolean)
    fun isUseSecondsEnabled(): Boolean

    fun saveVibrationEnabledState(isEnabled: Boolean)
    fun isVibrationEnabled(): Boolean
}

class UserPreferencesDelegate(
    private val sharedPreferences: SharedPreferences
) : IUserPreferencesDelegate {

    override fun saveUseSecondsState(useSeconds: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_USE_SECONDS, useSeconds)
        }
    }

    override fun isUseSecondsEnabled(): Boolean = sharedPreferences.getBoolean(KEY_USE_SECONDS, false)

    override fun saveVibrationEnabledState(isEnabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_VIBRATION_ENABLED, isEnabled)
        }
    }

    override fun isVibrationEnabled(): Boolean = sharedPreferences.getBoolean(KEY_VIBRATION_ENABLED, false)

    companion object {
        private const val KEY_USE_SECONDS = "_useSeconds"
        private const val KEY_VIBRATION_ENABLED = "_vibrationEnabled"
    }
}
