package workshop.akbolatss.tools.touchcounter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView

import android.view.ViewGroup.LayoutParams.WRAP_CONTENT

class PopupView(context: Context) {

    private val popupWindow: PopupWindow
    private val contentView: View
    private val messageView: TextView

    init {
        val inflater = LayoutInflater.from(context)
        contentView = inflater.inflate(R.layout.view_popup, null)

        messageView = contentView.findViewById(R.id.tv_message)

        popupWindow = PopupWindow(contentView, WRAP_CONTENT, WRAP_CONTENT)
        popupWindow.isFocusable = true
    }

    private fun showAsDropDown(anchor: View) {
        setDismissTouchListener()
        popupWindow.showAsDropDown(anchor)
    }

    fun showAsDropDownDef(anchor: View) {
        setDismissTouchListener()
        showAsDropDown(anchor)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setDismissTouchListener() {
        popupWindow.setTouchInterceptor { _, event ->
            val dismissPopup = event.action == MotionEvent.ACTION_OUTSIDE
            if (dismissPopup)
                popupWindow.dismiss()
            dismissPopup
        }
    }
}
