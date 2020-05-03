package workshop.akbolatss.tools.touchcounter.utils.widget

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import workshop.akbolatss.tools.touchcounter.databinding.ViewPopupBinding

class PopupView(anchor: View) {

    private val popupWindow: PopupWindow

    companion object {
        fun show(anchor: View) {
            PopupView(anchor)
        }
    }

    init {
        val binding = ViewPopupBinding.inflate(LayoutInflater.from(anchor.context))
        popupWindow = PopupWindow(binding.root, WRAP_CONTENT, WRAP_CONTENT)
        popupWindow.isFocusable = true
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