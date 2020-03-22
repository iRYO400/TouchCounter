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
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto


class ClickAdapter : ListAdapter<ClickDto, ClickAdapter.CounterVH>(DIFF_CALLBACK) {

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

        fun bind(clickObject: ClickDto) {
            handler.removeCallbacks(customRunnable)
            customRunnable.holder = itemView.timestamp
            customRunnable.init(itemView.timestamp, clickObject.createTime)
            handler.postDelayed(customRunnable, 100)

            itemView.index.text = layoutPosition.toString()
            itemView.timing.text = clickObject.heldMillis.toString()
        }
    }
}

private val DIFF_CALLBACK: DiffUtil.ItemCallback<ClickDto> =
    object : DiffUtil.ItemCallback<ClickDto>() {

        override fun areItemsTheSame(oldItem: ClickDto, newItem: ClickDto): Boolean {
            return oldItem.id == newItem.id
        }

        var hasSameTimestamp = false
        var hasSameMillis = false

        override fun areContentsTheSame(oldItem: ClickDto, newItem: ClickDto): Boolean {
            hasSameTimestamp = oldItem.createTime == newItem.createTime
            hasSameMillis = oldItem.heldMillis == newItem.heldMillis
            return hasSameTimestamp && hasSameMillis
        }
    }