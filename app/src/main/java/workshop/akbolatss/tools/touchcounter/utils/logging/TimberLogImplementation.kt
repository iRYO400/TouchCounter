package workshop.akbolatss.tools.touchcounter.utils.logging

import timber.log.Timber
import workshop.akbolatss.tools.touchcounter.BuildConfig

object TimberLogImplementation {

    fun init() {
        val tree =
            if (BuildConfig.DEBUG) DevelopmentDebugTree()
            else ProductionTree()
        Timber.plant(tree)
    }

    private class DevelopmentDebugTree : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String? {
            return String.format(
                "TouchCounter:C:%s:%s",
                super.createStackElementTag(element),
                element.lineNumber
            )
        }
    }

    private class ProductionTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
//            if (priority == Log.ERROR || priority == Log.WARN)
//                Firebase.sendStackTrace(t);
        }
    }
}
