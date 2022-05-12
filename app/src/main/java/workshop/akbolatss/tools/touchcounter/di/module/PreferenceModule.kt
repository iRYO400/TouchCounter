package workshop.akbolatss.tools.touchcounter.di.module

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import workshop.akbolatss.tools.touchcounter.utils.android.DarkThemeDelegate
import workshop.akbolatss.tools.touchcounter.utils.android.IUserPreferencesDelegate
import workshop.akbolatss.tools.touchcounter.utils.android.UserPreferencesDelegate
import javax.inject.Singleton

@Module
class PreferenceModule {

    @Singleton
    @Provides
    fun provideDarkThemeDelegate(sharedPreferences: SharedPreferences): DarkThemeDelegate =
        DarkThemeDelegate(
            sharedPreferences
        )

    @Singleton
    @Provides
    fun provideUserPreferences(sharedPreferences: SharedPreferences): IUserPreferencesDelegate =
        UserPreferencesDelegate(sharedPreferences)
}