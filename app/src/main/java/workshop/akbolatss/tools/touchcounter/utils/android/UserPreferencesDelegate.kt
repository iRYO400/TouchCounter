package workshop.akbolatss.tools.touchcounter.utils.android

import android.content.SharedPreferences

interface IUserPreferencesDelegate {
    fun saveUseSecondsState(useSeconds: Boolean)
    fun isUseSecondsEnabled(): Boolean
}

class UserPreferencesDelegate(
    private val sharedPreferences: SharedPreferences
) : IUserPreferencesDelegate {

    override fun saveUseSecondsState(useSeconds: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_USE_SECONDS, useSeconds)
            .apply()
    }

    override fun isUseSecondsEnabled(): Boolean = sharedPreferences.getBoolean(KEY_USE_SECONDS, false)

    companion object {
        private const val KEY_USE_SECONDS = "_useSeconds"
    }
}
