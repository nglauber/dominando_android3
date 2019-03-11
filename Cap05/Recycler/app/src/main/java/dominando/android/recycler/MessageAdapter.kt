package dominando.android.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message.view.*

class MessageAdapter(
    private val messages: List<Message>,
    private val callback: (Message) -> Unit) :
    RecyclerView.Adapter<MessageAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        val vh = VH(v)
        vh.itemView.setOnClickListener {
            val message = messages[vh.adapterPosition]
            callback(message)
        }
        return vh
    }
    override fun onBindViewHolder(holder: VH, pos: Int) {
        val (title, text) = messages[pos]
        holder.txtTitle.text = title
        holder.txtText.text = text
    }
    override fun getItemCount(): Int = messages.size

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.txtTitle
        var txtText: TextView = itemView.txtText
    }
}