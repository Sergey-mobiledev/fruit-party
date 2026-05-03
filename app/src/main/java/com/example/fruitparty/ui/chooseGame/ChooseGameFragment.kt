package com.example.fruitparty.ui.chooseGame

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fruitparty.R
import com.example.fruitparty.data.model.game.Game
import com.example.fruitparty.databinding.FragmentChooseGameBinding
import com.example.fruitparty.ui.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChooseGameFragment : Fragment() {

    private lateinit var binding: FragmentChooseGameBinding
    private val chooseGameViewModel by viewModel<ChooseGameViewModel>()
    private val gamesAdapter = GamesAdapter(openGame = {
        chooseGameViewModel.emitCurrentFragment(1)
        openNextFragment()
    })
    private var isChromeOpened = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.gamesList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = gamesAdapter
            setHasFixedSize(true)
            itemAnimator = null
            isMotionEventSplittingEnabled = false
        }
        chooseGameViewModel.apply {
            emitCurrentFragment(R.id.chooseGameFragment)
        }
        gamesAdapter.submitList(Game.games)
    }

    private fun openNextFragment() {
        chooseGameViewModel.getFireStoreModel { fireStoreModel ->
            requireActivity().runOnUiThread {
                val shouldOpenContent = fireStoreModel?.isShowContent == true
                val url = fireStoreModel?.url.orEmpty()
                if (shouldOpenContent && url.isNotBlank()) {
                    openChromeTabs(url)
                } else {
                    startMainActivity()
                    requireActivity().finish()
                }
            }
        }
    }

    private fun startMainActivity() {
        val mainActivityIntent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(mainActivityIntent)
        requireActivity().finish()
    }

    private fun openChromeTabs(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        isChromeOpened = true
        customTabsIntent.launchUrl(requireActivity(), Uri.parse(url))
    }

    override fun onResume() {
        super.onResume()
        if (isChromeOpened) {
            startMainActivity()
            requireActivity().finish()
        }
    }
}
