package workshop.akbolatss.tools.touchcounter

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_counter.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CounterAdapter(
    private val list: MutableLiveData<ArrayList<ClickObject>>,
    private val clickListener: (ClickObject, Int) -> Unit
) : RecyclerView.Adapter<CounterAdapter.CounterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CounterVH {
        val inflater = LayoutInflater.from(parent.context)
        return CounterVH(inflater.inflate(R.layout.rv_counter, parent, false))
    }

    override fun getItemCount(): Int {
        if (list.value != null)
            return list.value!!.size
        return 0
    }

    override fun onBindViewHolder(holder: CounterVH, position: Int) {
        val introAction = list.value!![position]
        holder.bind(introAction, clickListener)
    }

    class CounterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(clickObject: ClickObject, clickListener: (ClickObject, Int) -> Unit) {

            itemView.timestamp.text = clickObject.timestamp.toString()
            itemView.index.text = clickObject.index.toString()
            itemView.timing.text = clickObject.holdTiming.toString()
        }

        private fun convertTime(timestamp: String): String {
            val mDataFormat: DateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
            return try {
                val date = mDataFormat.parse(timestamp)
                val niceDateStr = DateUtils.getRelativeTimeSpanString(
                    date.time,
                    Calendar.getInstance().timeInMillis,
                    DateUtils.MINUTE_IN_MILLIS
                )
                niceDateStr.toString()
            } catch (e: ParseException) {
                Log.e("ParseException", "Unparseable date " + e.message)
                timestamp
            }
        }
    }

}