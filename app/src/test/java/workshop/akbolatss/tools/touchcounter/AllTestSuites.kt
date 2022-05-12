package workshop.akbolatss.tools.touchcounter

import org.junit.runner.RunWith
import org.junit.runners.Suite
import workshop.akbolatss.tools.touchcounter.data.DataTestSuite
import workshop.akbolatss.tools.touchcounter.ui.UiTestSuite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    DataTestSuite::class,
    UiTestSuite::class
)
class AllTestSuites