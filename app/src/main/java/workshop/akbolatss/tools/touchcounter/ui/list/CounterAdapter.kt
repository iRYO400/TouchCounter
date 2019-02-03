package workshop.akbolatss.tools.touchcounter.ui.list

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_counter.view.*
import workshop.akbolatss.tools.touchcounter.R
import workshop.akbolatss.tools.touchcounter.convertTime
import workshop.akbolatss.tools.touchcounter.pojo.CounterObject

class CounterAdapter(
        private val clickListener: (CounterObject, Int) -> Unit
) : ListAdapter<CounterObject, CounterAdapter.CounterVH>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK: ItemCallback<CounterObject> = object : DiffUtil.ItemCallback<CounterObject>() {

            override fun areItemsTheSame(oldItem: CounterObject, newItem: CounterObject): Boolean {
                return oldItem.id == newItem.id
            }

            var hasSameName = false
            var hasSameTimestamp = false
            var hasSameCount = false

            override fun areContentsTheSame(oldItem: CounterObject, newItem: CounterObject): Boolean {
                hasSameName = TextUtils.equals(oldItem.name, newItem.name)
                hasSameTimestamp = oldItem.timestampCreating == newItem.timestampCreating
                hasSameCount = oldItem.count == newItem.count
                return hasSameName && hasSameTimestamp && hasSameCount
            }
        }
    }

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

        fun bind(counterObject: CounterObject, clickListener: (CounterObject, Int) -> Unit) {
            itemView.name.text = counterObject.name

            itemView.timestamp.text = convertTime(counterObject.timestampCreating)
            itemView.count.text = counterObject.count.toString()

            itemView.setOnClickListener {
                clickListener(counterObject, adapterPosition)
            }
        }
    }
}