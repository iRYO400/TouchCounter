package workshop.akbolatss.tools.touchcounter.ui.list

import workshop.akbolatss.tools.touchcounter.pojo.CounterObject

interface OnListCallback {
    fun onListItemClick(counterObject: CounterObject)
}