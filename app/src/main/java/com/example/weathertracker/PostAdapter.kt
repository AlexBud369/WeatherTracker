package com.example.weathertracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(
    private var posts: List<Post> = emptyList(),
    private val onItemClick: ((Post) -> Unit)? = null
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPostIdBadge: TextView = itemView.findViewById(R.id.tvPostIdBadge)
        val tvPostUserId: TextView = itemView.findViewById(R.id.tvPostUserId)
        val tvPostTitle: TextView = itemView.findViewById(R.id.tvPostTitle)
        val tvPostBody: TextView = itemView.findViewById(R.id.tvPostBody)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        bindPostData(holder, post)
    }

    private fun bindPostData(holder: PostViewHolder, post: Post) {
        holder.tvPostIdBadge.text = "#${post.id}"
        holder.tvPostUserId.text = "Автор: ${post.userId}"
        holder.tvPostTitle.text = post.title
        holder.tvPostBody.text = post.body
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(post)
        }
    }

    override fun getItemCount(): Int = posts.size

    fun updateList(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}
