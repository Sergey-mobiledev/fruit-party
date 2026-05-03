package com.example.fruitparty.ui.blogs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.example.fruitparty.R
import com.example.fruitparty.data.model.blog.Blog
import com.example.fruitparty.databinding.ItemBlogBinding

class BlogsAdapter(
    private val openSomeBlog: (String) -> Unit
) : androidx.recyclerview.widget.ListAdapter<Blog, BlogsAdapter.ViewHolder>(
    DiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBlogBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, openSomeBlog)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Blog = currentList[position]
        holder.bind(item)
    }

    class ViewHolder(
        private val binding: ItemBlogBinding,
        private val openSomeBlog: (String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Blog) {
            binding.apply {
                Glide.with(blogImage)
                    .load(item.img.isobar)
                    .transition(
                        GenericTransitionOptions.with(R.anim.fade_in)
                    )
                    .centerCrop()
                    .error(R.drawable.image_error)
                    .into(blogImage)
                blogTitle.text = item.title.rendered
                blogButton.setOnClickListener {
                    openSomeBlog(item.title.rendered)
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Blog>() {

        override fun areItemsTheSame(oldItem: Blog, newItem: Blog): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Blog, newItem: Blog): Boolean {
            return oldItem == newItem
        }
    }
}