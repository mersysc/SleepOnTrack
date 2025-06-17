package com.example.sleepontrack_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SleepSessionsAdapter(
    private val sessions: MutableList<SleepItemDisplay>,
    private val onDelete: (SleepItemDisplay) -> Unit,
    private val onEdit: (SleepItemDisplay) -> Unit,
    private val onDetails: (SleepItemDisplay) -> Unit
) : RecyclerView.Adapter<SleepSessionsAdapter.SleepViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview, parent, false)
        return SleepViewHolder(view)
    }

    override fun onBindViewHolder(holder: SleepViewHolder, position: Int) {
        holder.bind(sessions[position])
    }

    override fun getItemCount(): Int = sessions.size

    fun updateList(newList: List<SleepItemDisplay>) {
        sessions.clear()
        sessions.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeItem(session: SleepItemDisplay) {
        val position = sessions.indexOf(session)
        if (position != -1) {
            sessions.removeAt(position)
            notifyItemRemoved(position)

            if (sessions.isEmpty()) {
                println("Brak zapisanych sesji snu")
            }
        }
    }

    inner class SleepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.dateOfSleeptxt)
        private val durationText: TextView = itemView.findViewById(R.id.hoursOfSleeptxt)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val btnDelete: Button = itemView.findViewById(R.id.deleteButton)
        private val btnEdit: Button = itemView.findViewById(R.id.editButton)
        private val btnDetails: Button = itemView.findViewById(R.id.sleepRecyclerView)

        fun bind(session: SleepItemDisplay) {
            dateText.text = session.date
            durationText.text = session.duration
            ratingBar.rating = session.rating.toFloat()

            btnDelete.setOnClickListener { onDelete(session) }
            btnEdit.setOnClickListener { onEdit(session) }
            btnDetails.setOnClickListener { onDetails(session) }
        }
    }
}

data class SleepItemDisplay(
    val date: String,
    val duration: String,
    val rating: Int,
    val notes: String,
    val sleepTime: String,
    val wakeTime: String
)
