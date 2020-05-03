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
import workshop.akbolatss.tools.touchcounter.utils.formatAsRelativeInMinutes

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
        )
    }

    override fun onBindViewHolder(holder: CounterVH, position: Int) {
        val introAction = getItem(position)
        holder.bind(introAction, onCounterClickListener, onCounterOptionsClickListener)
    }

    class CounterVH(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemCounterBinding.bind(itemView)
        fun bind(
            counter: CounterDto,
            onCounterClickListener: (CounterDto) -> Unit,
            onCounterOptionsClickListener: (CounterDto) -> Unit
        ) {
            binding.name.text = counter.name

            binding.timestamp.text = counter.editTime.formatAsRelativeInMinutes()
            binding.count.text = counter.itemCount.toString()

            binding.root.setOnClickListener {
                onCounterClickListener(counter)
            }

            binding.imgOptions.setOnClickListener {
                onCounterOptionsClickListener(counter)
            }
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