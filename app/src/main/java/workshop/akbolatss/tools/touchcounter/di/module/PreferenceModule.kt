package workshop.akbolatss.tools.touchcounter.di.module

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import workshop.akbolatss.tools.touchcounter.utils.DarkThemeDelegate
import javax.inject.Singleton

@Module
class PreferenceModule {

    @Singleton
    @Provides
    fun provideDarkThemeDelegate(sharedPreferences: SharedPreferences): DarkThemeDelegate =
        DarkThemeDelegate(sharedPreferences)
}