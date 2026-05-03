package com.example.fruitparty.ui.bonusGame

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fruitparty.R
import com.example.fruitparty.data.model.card.Card
import com.example.fruitparty.databinding.ItemCardBinding

class CardsAdapter(
    private val openSomeCard: (Int) -> Unit
) : androidx.recyclerview.widget.ListAdapter<Card, CardsAdapter.ViewHolder>(
    DiffCallback
) {
    init {
        setHasStableIds(true)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, openSomeCard, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Card = getItem(position)
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    class ViewHolder(
        private val binding: ItemCardBinding,
        private val openSomeCard: (Int) -> Unit,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private val scale = context.resources.displayMetrics.density
        private val frontAnimationTemplate by lazy {
            AnimatorInflater.loadAnimator(context, R.animator.front_animator) as AnimatorSet
        }
        private val backAnimationTemplate by lazy {
            AnimatorInflater.loadAnimator(context, R.animator.back_animator) as AnimatorSet
        }

        fun bind(item: Card) {
            binding.apply {
                if (item.isOpen) {
                    animateCard()
                } else {
                    if (cardFront.alpha != 0f) {
                        cardFront.alpha = 0f
                        cardBack.alpha = 1f
                    }
                }
                cardBack.setImageResource(item.imageClosed)
                cardFront.setImageResource(item.imageOpen)
                cardBack.setOnClickListener {
                    openSomeCard(item.id)
                }
            }
        }

        private fun animateCard() {
            binding.apply {
                cardFront.cameraDistance = 8000 * scale
                cardBack.cameraDistance = 8000 * scale
                val frontAnimation = frontAnimationTemplate.clone() as AnimatorSet
                val backAnimation = backAnimationTemplate.clone() as AnimatorSet
                frontAnimation.setTarget(cardBack)
                backAnimation.setTarget(cardFront)
                frontAnimation.start()
                backAnimation.start()
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Card>() {

        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem == newItem
        }
    }
}