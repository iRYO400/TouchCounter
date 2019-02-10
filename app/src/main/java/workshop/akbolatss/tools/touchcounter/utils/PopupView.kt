package workshop.akbolatss.tools.touchcounter.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import workshop.akbolatss.tools.touchcounter.R

class PopupView(context: Context) {

    private val popupWindow: PopupWindow

    init {
        val inflater = LayoutInflater.from(context)
        val contentView: View = inflater.inflate(R.layout.view_popup, null)
        popupWindow = PopupWindow(contentView, WRAP_CONTENT, WRAP_CONTENT)
        popupWindow.isFocusable = true
    }

    fun showPopup(anchor: View) {
        setDismissListener()
        popupWindow.showAsDropDown(anchor)
    }

    private fun setDismissListener() {
        popupWindow.setTouchInterceptor { _, event ->
            val dismissPopup = event.action == MotionEvent.ACTION_OUTSIDE
            if (dismissPopup)
                popupWindow.dismiss()
            dismissPopup
        }
    }
}
