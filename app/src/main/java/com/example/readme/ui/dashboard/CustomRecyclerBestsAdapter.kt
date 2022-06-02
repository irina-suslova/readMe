package com.example.readme.ui.dashboard

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.readme.R

class CustomRecyclerBestsAdapter (private val bests: MutableList<List<String>>) : RecyclerView.Adapter<CustomRecyclerBestsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var largeTextView: TextView? = null
        var smallTextView: TextView? = null

        init {
            largeTextView = itemView.findViewById(R.id.textViewLarge)
            smallTextView = itemView.findViewById(R.id.textViewSmall)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_best_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.largeTextView?.text = Html.fromHtml(bests[0][position].replace("\"", "", false), Html.FROM_HTML_MODE_LEGACY)
            holder.smallTextView?.text = Html.fromHtml(bests[1][position].replace("\"", "", false), Html.FROM_HTML_MODE_LEGACY)
        }
        else {
            holder.largeTextView?.text = Html.fromHtml(bests[0][position].replace("\"", "", false))
            holder.smallTextView?.text = Html.fromHtml(bests[1][position].replace("\"", "", false))
        }
    }

    override fun getItemCount() = bests[0].size
}
