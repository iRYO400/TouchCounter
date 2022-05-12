package workshop.akbolatss.tools.touchcounter.data

import org.junit.runner.RunWith
import org.junit.runners.Suite
import workshop.akbolatss.tools.touchcounter.data.repository.ClickRepositoryImplTest
import workshop.akbolatss.tools.touchcounter.data.repository.CounterRepositoryImplTest

@RunWith(Suite::class)
@Suite.SuiteClasses(
//    ClickDaoTest::class,
//    CounterDaoTest::class,
    ClickRepositoryImplTest::class,
    CounterRepositoryImplTest::class
)
class DataTestSuite