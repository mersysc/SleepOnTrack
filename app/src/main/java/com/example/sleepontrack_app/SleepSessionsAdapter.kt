package com.example.sleepontrack_app
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sleepontrack_app.R

class SleepSessionsAdapter(
    private val sessions: MutableList<SleepItemDisplay>,
    private val onDelete: (SleepItemDisplay) -> Unit,
    private val onEdit: (SleepItemDisplay) -> Unit,
    private val onDetails: (SleepItemDisplay) -> Unit
) : RecyclerView.Adapter<SleepSessionsAdapter.SleepViewHolder>() {

    class SleepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.dateOfSleeptxt)
        val durationText: TextView = itemView.findViewById(R.id.hoursOfSleeptxt)
        val ratingText: TextView = itemView.findViewById(R.id.ratingSleeptxt)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val editButton: Button = itemView.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview, parent, false)
        return SleepViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SleepViewHolder, position: Int) {
        val session = sessions[position]
        holder.dateText.text = session.date
        holder.durationText.text = "Duration: ${session.duration}"
        holder.ratingText.text = if (session.rating > 0)
            "Rating: ${"⭐".repeat(session.rating)}"
        else "No rating"

        // Kliknięcia przycisków
        holder.deleteButton.setOnClickListener { onDelete(session) }
        holder.editButton.setOnClickListener { onEdit(session) }
        holder.itemView.setOnClickListener { onDetails(session) }
    }

    override fun getItemCount(): Int = sessions.size

    fun removeItem(item: SleepItemDisplay) {
        val index = sessions.indexOf(item)
        if (index != -1) {
            sessions.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateList(newList: List<SleepItemDisplay>) {
        sessions.clear()
        sessions.addAll(newList)
        notifyDataSetChanged()
    }
}

data class SleepItemDisplay(
    val date: String,
    val duration: String,
    val rating: Int,
    val notes: String
)