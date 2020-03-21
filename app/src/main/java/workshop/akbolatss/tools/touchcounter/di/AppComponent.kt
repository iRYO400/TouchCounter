package workshop.akbolatss.tools.touchcounter.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import workshop.akbolatss.tools.touchcounter.ApplicationMain
import workshop.akbolatss.tools.touchcounter.di.module.*
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBindingModule::class,
        RepositoryModule::class,
        PersistenceModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent : AndroidInjector<ApplicationMain> {

    @Component.Factory
    interface Factory : AndroidInjector.Factory<ApplicationMain>
}
