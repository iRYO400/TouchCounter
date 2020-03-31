package workshop.akbolatss.tools.touchcounter.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import workshop.akbolatss.tools.touchcounter.di.ViewModelKey
import workshop.akbolatss.tools.touchcounter.ui.ViewModelFactory
import workshop.akbolatss.tools.touchcounter.ui.counter.CounterViewModel
import workshop.akbolatss.tools.touchcounter.ui.list.NavigationViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(NavigationViewModel::class)
    abstract fun bindNavigationViewModel(navigationViewModel: NavigationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CounterViewModel::class)
    abstract fun bindCounterViewModel(counterViewModel: CounterViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}