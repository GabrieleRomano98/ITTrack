package gab.example.fausto.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import gab.example.fausto.R

data class Code(
    var id: String,
    var value: String
)

class CodeAdapter(
    private var codeList: MutableList<Code>,
    private val onDelete: (Code) -> Unit
) : RecyclerView.Adapter<CodeAdapter.CodeViewHolder>() {

    fun setCodeList(newCodes: MutableList<Code>) {
        val diffs = DiffUtil.calculateDiff(
            TimeslotDiffCallback(codeList, newCodes)
        )
        codeList = newCodes
        diffs.dispatchUpdatesTo(this)
    }

    class CodeViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {
        private val value: TextView = view.findViewById(R.id.code_value)
        private val deleteIcon: ImageView = view.findViewById(R.id.delete_icon)

        fun bind(code: Code, delete: (v: View) -> Unit) {
            value.text = code.value
            deleteIcon.setOnClickListener(delete)
        }

        fun unbind(view: View) {
            view.setOnClickListener(null)
            deleteIcon.setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodeViewHolder {
        val vg = LayoutInflater.from(parent.context)
            .inflate(R.layout.code_line, parent, false)
        return CodeViewHolder(vg)
    }

    override fun onBindViewHolder(holder: CodeViewHolder, position: Int) {
        val code = codeList[position]
        holder.bind(code) {
            val pos = codeList.indexOf(code)
            if (pos != -1) {
                codeList.removeAt(pos)
                notifyItemRemoved(pos)
                onDelete(code)
            }
        }
    }

    override fun onViewRecycled(holder: CodeViewHolder) {
        holder.unbind(holder.itemView)
    }

    override fun getItemCount(): Int = codeList.size

}

class TimeslotDiffCallback(private val ts: List<Code>, private val newTs: List<Code>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = ts.size
    override fun getNewListSize(): Int = newTs.size
    override fun areItemsTheSame(oldP: Int, newP: Int): Boolean {
        return ts[oldP].id == newTs[newP].id && ts[oldP].value == newTs[newP].value
    }

    override fun areContentsTheSame(oldP: Int, newP: Int): Boolean {
        return ts[oldP].id == newTs[newP].id
    }
}