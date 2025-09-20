package workshop.akbolatss.tools.touchcounter.ui.list

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.data.dto.CounterDto
import workshop.akbolatss.tools.touchcounter.databinding.ItemCounterBinding
import workshop.akbolatss.tools.touchcounter.utils.exts.formatAsRelativeInMinutes

class CounterListRVA(
    private val onCounterClickListener: (CounterDto) -> Unit,
    private val onCounterOptionsClickListener: (CounterDto) -> Unit,
    private val onSelectionStateChanged: (Boolean, Int) -> Unit // Boolean for isInSelectionMode, Int for selected count
) : ListAdapter<CounterDto, CounterListRVA.CounterVH>(DIFF_CALLBACK) {

    private val selectedCounterIds = mutableSetOf<Long>()
    private var isInSelectionMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CounterVH {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_counter, parent, false)
        return CounterVH(view).apply {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                val item = getItem(position)
                if (isInSelectionMode) {
                    toggleSelection(item, position)
                } else {
                    onCounterClickListener(item)
                }
            }
            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnLongClickListener true
                val item = getItem(position)
                toggleSelection(item, position)
                true
            }
            binding.imgOptions.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                val item = getItem(position)
                // In selection mode, options button might be hidden or disabled,
                // but if it's somehow clicked, prefer options over selection.
                if (!isInSelectionMode) {
                    onCounterOptionsClickListener(item)
                } else {
                    // Or, allow options click to also toggle selection for that item
                    // toggleSelection(item, position)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: CounterVH, position: Int) {
        val counter = getItem(position)
        holder.bind(counter, selectedCounterIds.contains(counter.id))
    }

    private fun toggleSelection(counter: CounterDto, position: Int) {
        val previouslySelected = selectedCounterIds.contains(counter.id)
        if (previouslySelected) {
            selectedCounterIds.remove(counter.id)
        } else {
            selectedCounterIds.add(counter.id)
        }
        notifyItemChanged(position)

        val oldIsInSelectionMode = isInSelectionMode
        isInSelectionMode = selectedCounterIds.isNotEmpty()

        if (oldIsInSelectionMode != isInSelectionMode || oldIsInSelectionMode) {
            onSelectionStateChanged(isInSelectionMode, selectedCounterIds.size)
        }
    }

    fun getSelectedCounterIds(): List<Long> {
        return selectedCounterIds.toList()
    }

    fun clearSelection() {
        if (!isInSelectionMode && selectedCounterIds.isEmpty()) return

        val previouslySelectedPositions = mutableListOf<Int>()
        currentList.forEachIndexed { index, counterDto ->
            if (selectedCounterIds.contains(counterDto.id)) {
                previouslySelectedPositions.add(index)
            }
        }

        selectedCounterIds.clear()
        isInSelectionMode = false
        previouslySelectedPositions.forEach { notifyItemChanged(it) }
        onSelectionStateChanged(false, 0)
    }

    class CounterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemCounterBinding.bind(itemView)

        fun bind(counter: CounterDto, isSelected: Boolean) {
            binding.name.text = counter.name
            binding.timestamp.text = counter.editTime.formatAsRelativeInMinutes()
            binding.count.text = counter.itemCount.toString()
            itemView.isActivated = isSelected
        }
    }
}

private val DIFF_CALLBACK: DiffUtil.ItemCallback<CounterDto> =
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
                oldItem.editTime == newItem.editTime &&
                    oldItem.itemCount == newItem.itemCount
        }
    }
