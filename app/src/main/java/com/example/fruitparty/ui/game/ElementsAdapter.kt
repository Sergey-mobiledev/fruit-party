package com.example.fruitparty.ui.game

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fruitparty.R
import com.example.fruitparty.data.model.element.Element
import com.example.fruitparty.data.services.Constants.STRAWBERRY
import com.example.fruitparty.databinding.ItemElementBinding

class ElementsAdapter() :
    androidx.recyclerview.widget.ListAdapter<Element, ElementsAdapter.ViewHolder>(
        DiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemElementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Element = getItem(position)
        holder.bind(item, holder.itemView.context)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.clearAnimations()
        super.onViewRecycled(holder)
    }

    class ViewHolder(
        private val binding: ItemElementBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            private const val WIN_SCALE_UP_DURATION_MS = 1000L
            private const val WIN_SCALE_DOWN_DURATION_MS = 1000L
        }

        fun bind(item: Element, context: Context) {
            binding.apply {
                Glide.with(binding.root)
                    .load(item.image)
                    .into(imageElement)
                if (item.isWin) {
                    if (item.name == STRAWBERRY) {
                        animateStrawberry(imageBack)
                    } else {
                        animateElement(imageElement)
                    }
                } else {
                    if (item.name == STRAWBERRY) {
                        imageBack.setBackgroundColor(getColor(context, R.color.bluish))
                    } else {
                        resetElement(imageElement)
                    }
                }
            }
        }

        private fun resetElement(image: ImageView) {
            image.animate().cancel()
            image.imageAlpha = 255
            image.scaleX = 1f
            image.scaleY = 1f
        }

        private fun animateElement(image: ImageView) {
            image.animate().cancel()
            image.animate()
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setDuration(WIN_SCALE_UP_DURATION_MS)
                .withEndAction {
                    image.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(WIN_SCALE_DOWN_DURATION_MS)
                        .start()
                }
                .start()
        }

        private fun animateStrawberry(image: ImageView) {
            image.animate().cancel()
            val yellow = getColor(image.context, R.color.yellow_school_bus_color)
            val blue = getColor(image.context, R.color.bluish)
            image.postDelayed({
                image.setBackgroundColor(yellow)
                image.postDelayed({
                    image.setBackgroundColor(blue)
                    image.postDelayed({
                        image.setBackgroundColor(yellow)
                        image.postDelayed({
                            image.setBackgroundColor(blue)
                            image.postDelayed({
                                image.setBackgroundColor(yellow)
                                image.postDelayed({
                                    image.setBackgroundColor(blue)
                                    image.postDelayed({
                                        image.setBackgroundColor(yellow)
                                        image.postDelayed({
                                            image.setBackgroundColor(blue)
                                        }, 490)
                                    }, 490)
                                }, 490)
                            }, 490)
                        }, 490)
                    }, 490)
                }, 490)
            }, 0)
        }

        fun clearAnimations() {
            binding.imageElement.animate().cancel()
            binding.imageBack.setBackgroundColor(getColor(binding.root.context, R.color.bluish))
            binding.imageElement.scaleX = 1f
            binding.imageElement.scaleY = 1f
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Element>() {

        override fun areItemsTheSame(oldItem: Element, newItem: Element): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Element, newItem: Element): Boolean {
            return oldItem == newItem
        }
    }
}