package com.example.fruitparty.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.example.fruitparty.R
import com.example.fruitparty.data.model.element.Element
import com.example.fruitparty.data.services.ElementResult
import com.example.fruitparty.databinding.FragmentGameBinding
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random
import kotlin.random.nextInt

class GameFragment : Fragment() {
    companion object {
        private const val WIN_ANIMATION_DURATION_MS = 2300L
    }

    private lateinit var binding: FragmentGameBinding
    private val gameViewModel by viewModel<GameViewModel>()
    private val firstElementsAdapterWithoutStrawberry = ElementsAdapter()
    private val secondElementsAdapter = ElementsAdapter()
    private val thirdElementsAdapter = ElementsAdapter()
    private val fourthElementsAdapter = ElementsAdapter()
    private val fifthElementsAdapterWithoutStrawberry = ElementsAdapter()
    private lateinit var adapters: List<ElementsAdapter>
    private lateinit var recyclers: List<RecyclerView>
    private var elements = mutableMapOf<Int, List<Element>>()
    private var randomPositions = emptyList<Int>()
    private var autoMode = false
    private var spinningMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIsSpinningFalse()
        adapters = listOf(
            firstElementsAdapterWithoutStrawberry,
            secondElementsAdapter,
            thirdElementsAdapter,
            fourthElementsAdapter,
            fifthElementsAdapterWithoutStrawberry
        )
        recyclers = listOf(
            binding.firstList,
            binding.secondList,
            binding.thirdList,
            binding.fourthList,
            binding.fivesList,
        )
        for (i in 0..4) {
            recyclers[i].apply {
                layoutManager = SpeedyLinearLayoutManager(
                    requireContext(),
                    SpeedyLinearLayoutManager.VERTICAL,
                    false
                )
                adapter = adapters[i]
                setHasFixedSize(true)
                itemAnimator = null
            }
        }
        if (firstElementsAdapterWithoutStrawberry.currentList.isEmpty()) {
            gameViewModel.apply {
                getElementsListWithStrawberry {
                    requireActivity().runOnUiThread {
                        secondElementsAdapter.submitList(it.shuffled().map { element -> element.copy() })
                        thirdElementsAdapter.submitList(it.shuffled().map { element -> element.copy() })
                        fourthElementsAdapter.submitList(it.shuffled().map { element -> element.copy() })
                    }
                }
                getElementsListWithoutStrawberry {
                    requireActivity().runOnUiThread {
                        firstElementsAdapterWithoutStrawberry.submitList(it.shuffled().map { element -> element.copy() })
                        fifthElementsAdapterWithoutStrawberry.submitList(it.shuffled().map { element -> element.copy() })
                    }
                }
            }
        }

        binding.apply {
            buttonSpin.setOnClickListener {
                clickOnButtonSpin()
            }
            buttonAuto.setOnClickListener {
                if (!autoMode) {
                    setAutoMode()
                    if (!spinningMode) {
                        clickOnButtonSpin()
                    }
                } else {
                    removeAutoMode()
                }
            }
        }

        gameViewModel.apply {
            emitCurrentFragment(R.id.gameFragment)
            resetCurrentWin()
            updateCreditsPlusBonusWin()
            subscribeElementsResult()
        }
        observeElementsResult()
    }

    private fun observeElementsResult() {
        gameViewModel.liveDataElementsResult.observe(viewLifecycleOwner) {
            when (it.status) {
                ElementResult.Status.WIN -> {
                    gameViewModel.updateCreditsPlusWin(it.win!!)
                    gameViewModel.updateCurrentWin(it.win)
                    animateElements(it.line!!, it.x!!)
                    removeAutoMode()
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(WIN_ANIMATION_DURATION_MS)
                        setIsSpinningFalse()
                        if (autoMode) {
                            clickOnButtonSpin()
                        }
                    }
                }
                ElementResult.Status.LOSS -> {
                    setIsSpinningFalse()
                    if (autoMode) {
                        clickOnButtonSpin()
                    }
                }
                ElementResult.Status.BONUS_GAME -> {
                    removeAutoMode()
                    gameViewModel.updateCurrentWin(it.win!!)
                    gameViewModel.updateBonusWin(it.win)
                    animateStrawberries(it.positions!!)
                }
                ElementResult.Status.END_ANIMATION_BONUS_GAME -> {
                    findNavController().navigate(R.id.action_gameFragment_to_bonusGameFragment)
                }
                else -> Unit
            }
        }
    }

    private fun clickOnButtonSpin() {
        val onScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int
                ) {
                    when (newState) {
                        SCROLL_STATE_IDLE -> {
                            gameViewModel.checkResult(elements)
                            recyclerView.removeOnScrollListener(this)
                        }
                    }
                }
            }
        gameViewModel.apply {
            resetCurrentWin()
            isCreditsEnough {
                requireActivity().runOnUiThread {
                    if (it) {
                        elements.clear()
                        setIsSpinningTrue()
                        randomPositions = getPositions()
                        var index = -1
                        for (i in 1..3) {
                            elements[i] = listOf(
                                adapters[0].currentList[randomPositions[0] + index],
                                adapters[1].currentList[randomPositions[1] + index],
                                adapters[2].currentList[randomPositions[2] + index],
                                adapters[3].currentList[randomPositions[3] + index],
                                adapters[4].currentList[randomPositions[4] + index]
                            )
                            index += 1
                        }
                        for (i in 0..4) {
                            recyclers[i].scrollToPosition(0)
                            viewLifecycleOwner.lifecycleScope.launch {
                                delay(50)
                                recyclers[i].smoothScrollToPosition(randomPositions[i] + 1)
                            }
                        }
                        binding.fivesList.addOnScrollListener(onScrollListener)
                    }
                }
            }
        }
    }

    private fun animateStrawberries(positions: List<List<Int>>) {
        for (i in positions.indices) {
            var index = 0
            when (positions[i][0]) {
                2 -> {
                    index = -1
                }
                3 -> {
                    index = 1
                }
            }
            adapters[positions[i][1] - 1].currentList[randomPositions[positions[i][1] - 1] + index].isWin =
                true
            adapters[positions[i][1] - 1].notifyItemChanged(randomPositions[positions[i][1] - 1] + index)
            viewLifecycleOwner.lifecycleScope.launch {
                delay(3500)
                adapters[positions[i][1] - 1].currentList[randomPositions[positions[i][1] - 1] + index].isWin =
                    false
                adapters[positions[i][1] - 1].notifyItemChanged(randomPositions[positions[i][1] - 1] + index)
            }
        }
    }

    private fun animateElements(line: Int, x: Int) {
        var index = 0
        when (line) {
            2 -> {
                index = -1
            }
            3 -> {
                index = 1
            }
        }
        for (i in 0 until x) {
            adapters[i].currentList[randomPositions[i] + index].isWin = true
            adapters[i].notifyItemChanged(randomPositions[i] + index)
            viewLifecycleOwner.lifecycleScope.launch {
                // Keep in sync with WIN_ANIMATION_DURATION_MS.
                delay(WIN_ANIMATION_DURATION_MS)
                adapters[i].currentList[randomPositions[i] + index].isWin =
                    false
                adapters[i].notifyItemChanged(randomPositions[i] + index)
            }
        }
    }

    private fun getPositions(): List<Int> {
        val positions = mutableListOf<Int>()
        positions.add(Random.nextInt(75..125))
        positions.add(Random.nextInt(100..150))
        positions.add(Random.nextInt(125..175))
        positions.add(Random.nextInt(150..200))
        positions.add(Random.nextInt(200..250))
        return positions
    }


    private fun setIsSpinningTrue() {
        spinningMode = true
        binding.apply {
            buttonSpin.apply {
                setImageResource(R.drawable.image_button_spin_pressed)
                isClickable = false
            }
        }
        gameViewModel.apply {
            updateIsSpinningMode(true)
            updateCreditsMinusSpin()
        }
    }

    private fun setAutoMode() {
        autoMode = true
        binding.apply {
            buttonAuto.apply {
                setImageResource(R.drawable.image_button_auto_pressed)
            }
        }
    }

    private fun removeAutoMode() {
        autoMode = false
        binding.apply {
            buttonAuto.apply {
                setImageResource(R.drawable.image_button_auto)
            }
        }
    }

    private fun setIsSpinningFalse() {
        spinningMode = false
        binding.apply {
            buttonSpin.apply {
                if (!isClickable) {
                    setImageResource(R.drawable.image_button_spin)
                    isClickable = true
                }
            }
        }
        gameViewModel.apply {
            updateIsSpinningMode(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameViewModel.apply {
            cancelViewModelScopeCoroutines()
        }
    }
}