package workshop.akbolatss.tools.touchcounter.di.module

import dagger.Binds
import dagger.Module
import workshop.akbolatss.tools.touchcounter.domain.repository.ClickRepository
import workshop.akbolatss.tools.touchcounter.room.ClicksRepositoryImpl

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindClickRepository(clicksRepositoryImpl: ClicksRepositoryImpl): ClickRepository
}