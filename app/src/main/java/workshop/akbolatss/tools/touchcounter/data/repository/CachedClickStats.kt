package workshop.akbolatss.tools.touchcounter.data.repository

import workshop.akbolatss.tools.touchcounter.data.dto.ClickStatsDto

class CachedClickStats(
    val counterId: Long,
    val stats: ClickStatsDto,
)
