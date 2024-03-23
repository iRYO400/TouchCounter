@file:Suppress("unused")

package workshop.akbolatss.tools.touchcounter

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject
import workshop.akbolatss.tools.touchcounter.di.DaggerAppComponent
import workshop.akbolatss.tools.touchcounter.utils.logging.TimberLogImplementation

class ApplicationMain : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.factory().create(this).inject(this)
        TimberLogImplementation.init()
    }

    override fun androidInjector(): AndroidInjector<Any> =
        dispatchingAndroidInjector
}
