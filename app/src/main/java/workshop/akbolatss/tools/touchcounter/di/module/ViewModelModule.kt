package workshop.akbolatss.tools.touchcounter.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import workshop.akbolatss.tools.touchcounter.di.ViewModelKey
import workshop.akbolatss.tools.touchcounter.ui.ViewModelFactory
import workshop.akbolatss.tools.touchcounter.ui.counter.ClickListViewModel
import workshop.akbolatss.tools.touchcounter.ui.list.CounterListViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CounterListViewModel::class)
    abstract fun bindNavigationViewModel(navigationViewModel: CounterListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ClickListViewModel::class)
    abstract fun bindCounterViewModel(counterViewModel: ClickListViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}