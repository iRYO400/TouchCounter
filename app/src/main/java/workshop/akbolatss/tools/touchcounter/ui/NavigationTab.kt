package workshop.akbolatss.tools.touchcounter.ui

import androidx.fragment.app.Fragment
import workshop.akbolatss.tools.touchcounter.ui.list.ListCountersFragment

enum class NavigationTab(val fragmentTag: String?,
                         val navigationTabFactory: NavigationTabFactory) {
    LIST(
            ListCountersFragment::class.java.canonicalName,
            object : NavigationTabFactory {
                override fun newInstance(): Fragment {
                    return ListCountersFragment.newInstance()
                }
            })
}
