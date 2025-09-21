package workshop.akbolatss.tools.touchcounter.ui

import org.junit.runner.RunWith
import org.junit.runners.Suite
import workshop.akbolatss.tools.touchcounter.ui.counter.ClickListViewModelTest
import workshop.akbolatss.tools.touchcounter.ui.list.CounterListViewModelTest

@RunWith(Suite::class)
@Suite.SuiteClasses(
    ClickListViewModelTest::class,
    CounterListViewModelTest::class
)
class UiTestSuite
