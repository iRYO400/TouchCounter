package workshop.akbolatss.tools.touchcounter.ui.list

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.databinding.ItemCounterBinding
import workshop.akbolatss.tools.touchcounter.utils.exts.formatAsRelativeInMinutes

class CounterListRVA(
    private val onCounterClickListener: (CounterDto) -> Unit,
    private val onCounterOptionsClickListener: (CounterDto) -> Unit
) : ListAdapter<CounterDto, CounterListRVA.CounterVH>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CounterVH {
        val inflater = LayoutInflater.from(parent.context)
        return CounterVH(
            inflater.inflate(
                R.layout.item_counter,
                parent,
                false
            )
        ).apply {
            binding.root.setOnClickListener {
                holdItem?.let(onCounterClickListener)
            }
            binding.imgOptions.setOnClickListener {
                holdItem?.let(onCounterOptionsClickListener)
            }
        }
    }

    override fun onBindViewHolder(holder: CounterVH, position: Int) {
        val introAction = getItem(position)
        holder.bind(introAction)
    }

    class CounterVH(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemCounterBinding.bind(itemView)

        var holdItem: CounterDto? = null
            private set

        fun bind(
            counter: CounterDto
        ) {
            this.holdItem = counter
            binding.name.text = counter.name

            binding.timestamp.text = counter.editTime.formatAsRelativeInMinutes()
            binding.count.text = counter.itemCount.toString()
        }
    }
}

private val DIFF_CALLBACK: ItemCallback<CounterDto> =
    object : DiffUtil.ItemCallback<CounterDto>() {

        override fun areItemsTheSame(
            oldItem: CounterDto,
            newItem: CounterDto
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: CounterDto,
            newItem: CounterDto
        ): Boolean {
            return TextUtils.equals(oldItem.name, newItem.name) &&
                oldItem.createTime == newItem.createTime &&
                oldItem.editTime == newItem.editTime
        }
    }
