package com.example.fruitparty.ui.bonusGame

import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fruitparty.R
import com.example.fruitparty.data.services.BonusGameState
import com.example.fruitparty.data.services.Constants.BLACK
import com.example.fruitparty.data.services.Constants.RED
import com.example.fruitparty.databinding.FragmentBonusGameBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class BonusGameFragment : Fragment() {

    private lateinit var binding: FragmentBonusGameBinding
    private val bonusGameViewModel by viewModel<BonusGameViewModel>()
    private val cardsAdapter = CardsAdapter(openSomeCard = { id ->
        val color = selectedColor ?: return@CardsAdapter
        bonusGameViewModel.openCard(id, color)
        binding.apply {
            cardsBackLayout.isClickable = true
            layoutButtonBlack.isClickable = false
            layoutButtonRed.isClickable = false
        }
    })
    private var selectedColor: String? = null
    private var animatorDrawableWin: AnimationDrawable? = null
    private var animatorDrawableLoss: AnimationDrawable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBonusGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            animWin.setImageResource(R.drawable.animation_bonus_game_win)
            animLoss.setImageResource(R.drawable.animation_bonus_game_loss)
            cardsList.apply {
                adapter = cardsAdapter
                isMotionEventSplittingEnabled = false
                layoutManager = GridLayoutManager(context, 3)
                setHasFixedSize(true)
                itemAnimator = null
            }
            buttonBack.setOnClickListener {
                findNavController().navigateUp()
            }

            layoutButtonRed.setOnClickListener {
                selectedColor = RED
                textRed.setTextColor(getColor(requireContext(), R.color.yellow_school_bus_color))
                layoutButtonRed.setBackgroundResource(R.drawable.field_selected_button)
                layoutButtonBlack.setBackgroundResource(R.drawable.choose_card_color_back)
                textBlack.setTextColor(Color.WHITE)
                cardsBackLayout.isClickable = false
            }
            layoutButtonBlack.setOnClickListener {
                selectedColor = BLACK
                textBlack.setTextColor(getColor(requireContext(), R.color.yellow_school_bus_color))
                layoutButtonBlack.setBackgroundResource(R.drawable.field_selected_button)
                layoutButtonRed.setBackgroundResource(R.drawable.choose_card_color_back)
                textRed.setTextColor(Color.WHITE)
                cardsBackLayout.isClickable = false
            }
        }
        bonusGameViewModel.apply {
            liveDataCardList.observe(viewLifecycleOwner) {
                cardsAdapter.submitList(it)
            }
            liveDataBonusGameState.observe(viewLifecycleOwner) {
                when (it.status) {
                    BonusGameState.Status.START_BONUS_GAME -> {
                        binding.apply {
                            winByChoosingColor.text = (it.win!! * 2).toString()

                        }
                    }
                    BonusGameState.Status.WIN -> {
                        updateBonusWin(it.win!!)
                        binding.apply {
                            winByChoosingColor.text = (it.win * 2).toString()
                        }
                        startWinAnimation()
                    }
                    BonusGameState.Status.LOSS -> {
                        updateBonusWin(0)
                        binding.apply {
                            winByChoosingColor.text = (0).toString()
                            buttonTake.apply {
                                setOnClickListener(null)
                                setOnClickListener {
                                    findNavController().navigateUp()
                                }
                                setImageResource(R.drawable.image_button_take)
                            }
                        }
                        startLossAnimation()
                    }
                    BonusGameState.Status.END_ANIMATION -> {
                        binding.apply {
                            if (animationWinLayout.visibility == View.VISIBLE) {
                                animationWinLayout.visibility = View.GONE
                                animatorDrawableWin?.stop()
                                buttonTake.apply {
                                    setOnClickListener(null)
                                    setImageResource(R.drawable.image_button_take)
                                    setOnClickListener {
                                        setNewTakeState()
                                    }
                                }
                            }
                            if (animationLossLayout.visibility == View.VISIBLE) {
                                animationLossLayout.visibility = View.GONE
                                animatorDrawableLoss?.stop()
                            }
                        }
                    }
                }
            }
            emitCurrentFragment(R.id.bonusGameFragment)
            emitRandomCardList()
            startBonusGame()
        }
    }

    private fun startWinAnimation() {
        binding.apply {
            animationWinLayout.visibility = View.VISIBLE
            animatorDrawableWin = animWin.drawable as AnimationDrawable
        }
        animatorDrawableWin?.start()
    }

    private fun startLossAnimation() {
        binding.apply {
            animationLossLayout.visibility = View.VISIBLE
            animatorDrawableLoss = animLoss.drawable as AnimationDrawable
        }
        animatorDrawableLoss?.start()
    }

    private fun setNewTakeState() {
        selectedColor = null
        binding.apply {
            layoutButtonBlack.isClickable = true
            layoutButtonRed.isClickable = true
            cardsBackLayout.isClickable = true
            textBlack.setTextColor(Color.WHITE)
            layoutButtonBlack.setBackgroundResource(R.drawable.choose_card_color_back)
            layoutButtonRed.setBackgroundResource(R.drawable.choose_card_color_back)
            textRed.setTextColor(Color.WHITE)
            buttonTake.apply {
                setImageResource(R.drawable.image_button_take_pressed)
                setOnClickListener(null)
            }
        }
        bonusGameViewModel.emitRandomCardList()
    }
}