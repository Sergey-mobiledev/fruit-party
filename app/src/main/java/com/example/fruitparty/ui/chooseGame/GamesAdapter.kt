package com.example.fruitparty.ui.chooseGame

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fruitparty.R
import com.example.fruitparty.data.model.game.Game
import com.example.fruitparty.databinding.ItemGameBinding

class GamesAdapter(
    private val openGame: (String) -> Unit
) : androidx.recyclerview.widget.ListAdapter<Game, GamesAdapter.ViewHolder>(
    DiffCallback
) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGameBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, openGame)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Game = getItem(position)
        val margin20dp = (20 * holder.itemView.resources.displayMetrics.density).toInt()
        val defaultEndMargin = (12 * holder.itemView.resources.displayMetrics.density).toInt()
        holder.itemView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            marginStart = if (position == 0) margin20dp else 0
            marginEnd = if (position == itemCount - 1) margin20dp else defaultEndMargin
        }
        holder.bind(item, holder.itemView.context)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).name.hashCode().toLong()
    }

    class ViewHolder(
        private val binding: ItemGameBinding,
        private val openGame: (String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private val valueAnimator: ValueAnimator = ValueAnimator.ofInt(0, 20, -20, 18, -18, 15, -15, 6, -6, 0).apply {
            duration = 500
            addUpdateListener {
                val value = it.animatedValue as Int
                // Use only translation to avoid expensive relayout on every frame.
                binding.root.translationX = value.toFloat()
            }
        }

        private fun startShakeAnimation(view: View) {
            valueAnimator.cancel()
            view.translationX = 0f
            valueAnimator.start()
        }

        fun bind(item: Game, context: Context) {
            binding.apply {
                if (item.isOpen) {
                    gameImage.apply {
                        setImageResource(item.image!!)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                    imagePlay.isVisible = true
                    gameText.apply {
                        text = "Ready To Play"
                        setTextColor(Color.WHITE)
                    }
                } else {
                    gameImage.apply {
                        setBackgroundColor(Color.BLACK)
                        setImageResource(R.drawable.image_lock)
                        scaleType = ImageView.ScaleType.CENTER_INSIDE
                    }
                    imagePlay.isVisible = false
                    gameText.apply {
                        text = "Locked"
                        setTextColor(getColor(context, R.color.deep_purplish_blue))

                    }
                }
                itemGameView.setOnClickListener {
                    if (item.isOpen) {
                        openGame(item.name!!)
                    } else {
                        startShakeAnimation(root)
                    }
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Game>() {

        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem == newItem
        }
    }
}