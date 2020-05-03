package workshop.akbolatss.tools.touchcounter.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import workshop.akbolatss.tools.touchcounter.di.scope.ActivityScope
import workshop.akbolatss.tools.touchcounter.ui.counter.ClickListActivity
import workshop.akbolatss.tools.touchcounter.ui.list.CounterListActivity

@Module
abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeNavigationActivity(): CounterListActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeCounterActivity(): ClickListActivity
}