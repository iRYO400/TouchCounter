package workshop.akbolatss.tools.touchcounter.ui

import androidx.fragment.app.Fragment
import workshop.akbolatss.tools.touchcounter.ui.list.ListCountersFragment

sealed class NavigationTab(
    val fragmentTag: String,
    val javaClass: Class<out Fragment>
) {
    object ListCounters : NavigationTab(
        ListCountersFragment::class.java.simpleName,
        ListCountersFragment::class.java
    )
}