package workshop.akbolatss.tools.touchcounter.ui.counter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.TimeUnit
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.data.dto.ClickDto
import workshop.akbolatss.tools.touchcounter.databinding.ItemClickBinding

class ClickListRVA(
    private val isUseSecondsEnabled: Boolean
) : ListAdapter<ClickDto, ClickListRVA.CounterVH>(DIFF_CALLBACK) {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CounterVH {
        val inflater = LayoutInflater.from(parent.context)
        return CounterVH(
            inflater.inflate(
                R.layout.item_click,
                parent,
                false
            ), handler, isUseSecondsEnabled
        )
    }

    override fun onBindViewHolder(holder: CounterVH, position: Int) {
        val introAction = getItem(position)
        holder.bind(introAction)
    }

    override fun onBindViewHolder(holder: CounterVH, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads)
        else
            holder.setItemPosition()
    }

    class CounterVH(
        itemView: View,
        private val handler: Handler,
        private val isUseSecondsEnabled: Boolean
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding: ItemClickBinding = ItemClickBinding.bind(itemView)

        private val customRunnable: TextViewAutoUpdateRunnable =
            TextViewAutoUpdateRunnable(handler, binding.timestamp)

        fun bind(clickObject: ClickDto) {
            handler.removeCallbacks(customRunnable)
            customRunnable.tvLastUpdate = binding.timestamp
            customRunnable.init(binding.timestamp, clickObject.createTime)
            handler.postDelayed(customRunnable, 100)

            setHeldTiming(clickObject)
            setItemPosition()
        }

        private fun setHeldTiming(clickObject: ClickDto) {
            val heldTiming = if (isUseSecondsEnabled) {
                TimeUnit.MILLISECONDS.toSeconds(clickObject.heldMillis)
            } else {
                clickObject.heldMillis
            }

            binding.timing.text = heldTiming.toString()
        }

        fun setItemPosition() {
            binding.index.text = (layoutPosition + 1).toString()
        }
    }
}

private val DIFF_CALLBACK: DiffUtil.ItemCallback<ClickDto> =
    object : DiffUtil.ItemCallback<ClickDto>() {

        override fun areItemsTheSame(oldItem: ClickDto, newItem: ClickDto): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ClickDto, newItem: ClickDto): Boolean {
            return oldItem.createTime == newItem.createTime &&
                oldItem.heldMillis == newItem.heldMillis
        }
    }

class SwipeToDeleteCallback(
    private val itemPos: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(
    0, ItemTouchHelper.LEFT
) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        itemPos(viewHolder.layoutPosition)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.7f
}
