package workshop.akbolatss.tools.touchcounter.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton
import workshop.akbolatss.tools.touchcounter.ApplicationMain
import workshop.akbolatss.tools.touchcounter.di.module.ActivityBindingModule
import workshop.akbolatss.tools.touchcounter.di.module.AppModule
import workshop.akbolatss.tools.touchcounter.di.module.PersistenceModule
import workshop.akbolatss.tools.touchcounter.di.module.PreferenceModule
import workshop.akbolatss.tools.touchcounter.di.module.RepositoryModule
import workshop.akbolatss.tools.touchcounter.di.module.ViewModelModule

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBindingModule::class,
        RepositoryModule::class,
        PersistenceModule::class,
        PreferenceModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent : AndroidInjector<ApplicationMain> {

    @Component.Factory
    interface Factory : AndroidInjector.Factory<ApplicationMain>
}
