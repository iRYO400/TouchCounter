package workshop.akbolatss.tools.touchcounter.ui.list

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_counter.view.*
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.ui.list.ClickType.ITEM_CLICK
import workshop.akbolatss.tools.touchcounter.ui.list.ClickType.OPTIONS_CLICK
import workshop.akbolatss.tools.touchcounter.utils.formatAsRelativeInMinutes

class CounterAdapter(
    private val clickListener: (CounterDto, Int, ClickType) -> Unit
) : ListAdapter<CounterDto, CounterAdapter.CounterVH>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CounterVH {
        val inflater = LayoutInflater.from(parent.context)
        return CounterVH(
            inflater.inflate(
                R.layout.rv_counter,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CounterVH, position: Int) {
        val introAction = getItem(position)
        holder.bind(introAction, clickListener)
    }

    class CounterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            counter: CounterDto,
            clickListener: (CounterDto, Int, ClickType) -> Unit
        ) {
            itemView.name.text = counter.name

            itemView.timestamp.text = counter.editTime.formatAsRelativeInMinutes()
            itemView.count.text = counter.itemCount.toString()

            itemView.setOnClickListener {
                clickListener(counter, adapterPosition, ITEM_CLICK)
            }

            itemView.img_options.setOnClickListener {
                clickListener(counter, adapterPosition, OPTIONS_CLICK)
            }
        }
    }
}

private val DIFF_CALLBACK: ItemCallback<CounterDto> =
    object : DiffUtil.ItemCallback<CounterDto>() {

        override fun areItemsTheSame(
            oldItem: CounterDto,
            newItem: CounterDto
        ): Boolean {
            return oldItem.id == newItem.id
        }

        var hasSameName = false
        var hasSameCreateTime = false
        var hasSameEditTime = false

        override fun areContentsTheSame(
            oldItem: CounterDto,
            newItem: CounterDto
        ): Boolean {
            hasSameName = TextUtils.equals(oldItem.name, newItem.name)
            hasSameCreateTime = oldItem.createTime == newItem.createTime
            hasSameEditTime = oldItem.editTime == newItem.editTime
            return hasSameName && hasSameCreateTime && hasSameEditTime
        }
    }
