package workshop.akbolatss.tools.touchcounter.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import workshop.akbolatss.tools.touchcounter.di.ViewModelKey
import workshop.akbolatss.tools.touchcounter.ui.NavigationViewModel
import workshop.akbolatss.tools.touchcounter.ui.ViewModelFactory

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(NavigationViewModel::class)
    abstract fun bindNavigationViewModel(navigationViewModel: NavigationViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}