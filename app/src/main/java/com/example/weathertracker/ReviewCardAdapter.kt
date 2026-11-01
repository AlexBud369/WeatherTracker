package com.example.weathertracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewCardAdapter(
    private var reviews: List<Review>,
    private val onItemClick: (Review) -> Unit,
    private val onEditClick: (Review) -> Unit,
    private val onDeleteClick: (Review) -> Unit
) : RecyclerView.Adapter<ReviewCardAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCardCity: TextView = view.findViewById(R.id.tvCardCity)
        val tvCardStatus: TextView = view.findViewById(R.id.tvCardStatus)
        val tvCardTitle: TextView = view.findViewById(R.id.tvCardTitle)
        val tvCardSnippet: TextView = view.findViewById(R.id.tvCardSnippet)
        val tvCardDate: TextView = view.findViewById(R.id.tvCardDate)
        val btnCardEdit: ImageButton = view.findViewById(R.id.btnCardEdit)
        val btnCardDelete: ImageButton = view.findViewById(R.id.btnCardDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_card, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        bindTextData(holder, review)
        bindStatusBadge(holder, review.status)
        bindClickListeners(holder, review)
    }

    private fun bindTextData(holder: ReviewViewHolder, review: Review) {
        holder.tvCardCity.text = review.city
        holder.tvCardTitle.text = review.title.ifEmpty { "Отзыв о погоде" }
        holder.tvCardSnippet.text = review.comment
        holder.tvCardDate.text = review.date
        holder.tvCardStatus.text = review.status
    }

    private fun bindStatusBadge(holder: ReviewViewHolder, status: String) {
        if (status == "Совпало") {
            holder.tvCardStatus.setBackgroundResource(R.drawable.bg_badge_matched)
            holder.tvCardStatus.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            holder.tvCardStatus.setBackgroundResource(R.drawable.bg_badge_mismatched)
            holder.tvCardStatus.setTextColor(Color.parseColor("#D32F2F"))
        }
    }

    private fun bindClickListeners(holder: ReviewViewHolder, review: Review) {
        holder.itemView.setOnClickListener { onItemClick(review) }
        holder.btnCardEdit.setOnClickListener { onEditClick(review) }
        holder.btnCardDelete.setOnClickListener { onDeleteClick(review) }
    }

    override fun getItemCount(): Int = reviews.size

    fun updateList(newList: List<Review>) {
        reviews = newList
        notifyDataSetChanged()
    }
}