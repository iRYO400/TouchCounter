package workshop.akbolatss.tools.touchcounter.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import workshop.akbolatss.tools.touchcounter.di.scope.ActivityScope
import workshop.akbolatss.tools.touchcounter.ui.list.NavigationActivity
import workshop.akbolatss.tools.touchcounter.ui.counter.CounterActivity

@Module
abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeNavigationActivity(): NavigationActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeCounterActivity(): CounterActivity
}