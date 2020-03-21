package workshop.akbolatss.tools.touchcounter.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import workshop.akbolatss.tools.touchcounter.di.scope.ActivityScope
import workshop.akbolatss.tools.touchcounter.ui.NavigationActivity

@Module
abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeNavigationActivity(): NavigationActivity
}