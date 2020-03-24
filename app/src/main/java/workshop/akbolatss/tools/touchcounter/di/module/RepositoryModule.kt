package workshop.akbolatss.tools.touchcounter.di.module

import dagger.Binds
import dagger.Module
import workshop.akbolatss.tools.touchcounter.data.repository.ClickRepositoryImpl
import workshop.akbolatss.tools.touchcounter.data.repository.CounterRepositoryImpl
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.domain.repository.CounterRepository

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindClickRepository(clicksRepositoryImpl: ClickRepositoryImpl): ClickRepository

    @Binds
    abstract fun bindCounterRepository(counterRepositoryImpl: CounterRepositoryImpl): CounterRepository
}