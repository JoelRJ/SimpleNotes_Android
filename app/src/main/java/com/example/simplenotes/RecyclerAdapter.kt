
import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplenotes.Notes
import com.example.simplenotes.R
import kotlinx.android.synthetic.main.recycler_view_adapter.view.*

class RecyclerAdapter(private val list: List<Notes>, val context: Context,
                      private val cellClickListener: CellClickListener) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_view_adapter, parent, false)
        return ViewHolder(view).listen { pos, type ->
            val item = list.get(pos)
            cellClickListener.onCellClickListener(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.listType.text = list.get(position).noteData
    }

    interface CellClickListener {
        fun onCellClickListener (selected: Notes)
    }
}




// ViewHolder class holds list view
class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val listType = view.options_text
}

// ViewHolder function listens for click
fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(adapterPosition, itemViewType)
    }
    return this
}