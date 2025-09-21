package workshop.akbolatss.tools.touchcounter.data.dto

import workshop.akbolatss.tools.touchcounter.utils.INITIAL

data class ClickStatsDto(
    val heldMillis: Long,
    val position: Long,
) {
    companion object {
        fun empty(): ClickStatsDto = ClickStatsDto(
            INITIAL,
            INITIAL,
        )
    }
}
