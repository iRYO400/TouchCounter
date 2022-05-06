package workshop.akbolatss.tools.touchcounter.ui

import org.junit.runner.RunWith
import org.junit.runners.Suite
import workshop.akbolatss.tools.touchcounter.ui.counter.CounterViewModelTest
import workshop.akbolatss.tools.touchcounter.ui.list.NavigationViewModelTest

@RunWith(Suite::class)
@Suite.SuiteClasses(
    CounterViewModelTest::class,
    NavigationViewModelTest::class
)
class UiTestSuite
