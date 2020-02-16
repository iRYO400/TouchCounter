package workshop.akbolatss.tools.touchcounter.ui.counter

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_click.view.*
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.pojo.ClickObject


class ClickAdapter : ListAdapter<ClickObject, ClickAdapter.CounterVH>(DIFF_CALLBACK) {

    private val handler = Handler()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CounterVH {
        val inflater = LayoutInflater.from(parent.context)
        return CounterVH(
            inflater.inflate(
                R.layout.rv_click,
                parent,
                false
            ), handler
        )
    }

    override fun onBindViewHolder(holder: CounterVH, position: Int) {
        val introAction = getItem(position)
        holder.bind(introAction)
    }

    class CounterVH(itemView: View, private val handler: Handler) :
        RecyclerView.ViewHolder(itemView) {

        private var customRunnable: CustomRunnable =
            CustomRunnable(handler, itemView.timestamp)

        fun bind(clickObject: ClickObject) {
            handler.removeCallbacks(customRunnable)
            customRunnable.holder = itemView.timestamp
            customRunnable.init(itemView.timestamp, clickObject.timestamp)
            handler.postDelayed(customRunnable, 100)

            itemView.index.text = clickObject.index.toString()
            itemView.timing.text = clickObject.holdTiming.toString()
        }
    }
}

private val DIFF_CALLBACK: DiffUtil.ItemCallback<ClickObject> =
    object : DiffUtil.ItemCallback<ClickObject>() {

        override fun areItemsTheSame(oldItem: ClickObject, newItem: ClickObject): Boolean {
            return oldItem.index == newItem.index
        }

        var hasSameId = false
        var hasSameTimestamp = false

        override fun areContentsTheSame(oldItem: ClickObject, newItem: ClickObject): Boolean {
            hasSameId = oldItem.id == newItem.id
            hasSameTimestamp = oldItem.timestamp == newItem.timestamp

            return hasSameId && hasSameTimestamp
        }
    }