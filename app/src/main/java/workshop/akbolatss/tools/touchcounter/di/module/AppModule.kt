package workshop.akbolatss.tools.touchcounter.di.module

import android.app.Application
import dagger.Binds
import dagger.Module
import workshop.akbolatss.tools.touchcounter.ApplicationMain

@Module
abstract class AppModule {

    @Binds
    abstract fun application(applicationMain: ApplicationMain): Application
}