package com.example.fruitparty.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.example.fruitparty.R
import com.example.fruitparty.databinding.ActivityMainBinding
import com.example.fruitparty.ui.chooseGameActivity.ChooseGameActivity
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel by viewModel<MainViewModel>()
    private var profileAvatar: Bitmap? = null
    private var isSpinningMode: Boolean = false
    private var currentLines = 0
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)
    private val showFromCenterAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.show_from_centr)
    }
    private val hideToCenterAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.hide_to_centr)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mainViewModel.apply {
            liveDataUser.observe(this@MainActivity) { user ->
                binding.apply {
                    userAvatar.setImageBitmap(user.avatar)
                    userUsername.text = user.userName
                    userNameSurname.text = user.name + " " + user.surname
                    if (currentLines != user.lineal) {
                        showLines(user.lineal)
                    }
                    currentLines = user.lineal
                    linealQuantity.text = user.lineal.toString()
                    rateLinealQuantity.text = user.rateLineal.toString()
                    val betQuantity = user.lineal * user.rateLineal
                    this.rateQuantity.text = betQuantity.toString()
                    rateQuantityFun.text = (betQuantity.toDouble() / 100).toString() + " fun"
                    creditsQuantity.text = user.credits.toString()
                    creditsQuantityFun.text = (user.credits.toDouble() / 100).toString() + " fun"
                    isSpinningMode = user.isSpinningMode
                    if (isSpinningMode) {
                        buttonNavigateToChooseGameFragment.isClickable = false
                        buttonInfo.isClickable = false
                        buttonMenu.isClickable = false
                        linealLayout.isClickable = false
                        linealBack.setBackgroundResource(R.drawable.rate_rate_lineal_back_gradient_un_selected)
                        linealQuantity.setTextColor(Color.WHITE)
                        rateLinealLayout.isClickable = false
                        rateLinealBack.setBackgroundResource(R.drawable.rate_rate_lineal_back_gradient_un_selected)
                        rateLinealQuantity.setTextColor(Color.WHITE)
                    } else {
                        buttonNavigateToChooseGameFragment.isClickable = true
                        buttonInfo.isClickable = true
                        buttonMenu.isClickable = true
                        linealLayout.isClickable = true
                        linealBack.setBackgroundResource(R.drawable.lineal_background_gradient_radius_10)
                        linealQuantity.setTextColor(resources.getColor(R.color.yellow_school_bus_color))
                        rateLinealLayout.isClickable = true
                        rateLinealBack.setBackgroundResource(R.drawable.rate_lineal_background_gradient_corners_10)
                        rateLinealQuantity.setTextColor(resources.getColor(R.color.pearl_night_2))
                    }
                    if (findNavController(R.id.fragment_container).currentDestination?.id == R.id.bonusGameFragment) {
                        winQuantity.text = user.bonusWin.toString()
                        winQuantityFun.text = (user.bonusWin.toDouble() / 100).toString() + " fun"
                    } else {
                        winQuantity.text = user.currentWin.toString()
                        winQuantityFun.text = (user.currentWin.toDouble() / 100).toString() + " fun"
                    }
                }
            }
            liveDataCurrentFragmentId.observe(this@MainActivity) {
                when (it) {
                    R.id.gameFragment -> {
                        binding.apply {
                            showTopControlPanel()
                            showView(buttonNavigateToChooseGameFragment)
                            showBottomControlPanel()
                            showView(binding.buttonInfo)
                        }
                    }
                    R.id.bonusGameFragment -> {
                        hideView(binding.buttonInfo)
                        hideTopControlPanel()
                    }
                    R.id.blogsFragment, R.id.privacyPolicyFragment -> {
                        binding.apply {
                            hideTopControlPanel()
                            hideBottomControlPanel()
                            hideView(buttonNavigateToChooseGameFragment)

                        }
                    }
                    R.id.someBlogFragment -> {}
                }
            }
            subscribeUser()
            subscribeCurrentFragmentId()
        }
        binding.apply {
            buttonNavigateToChooseGameFragment.setOnClickListener {
                startChooseGameActivity()
            }

            buttonMenu.setOnClickListener {
                if (backgroundLayout.isVisible) {
                    hideMenu()
                } else {
                    showMenu()
                }
            }
        }
    }

    private fun showLines(lines: Int) {
        scope.coroutineContext.cancelChildren()
        binding.apply {
            when (lines) {
                1 -> {
                    if (firstLine.visibility == View.GONE) {
                        showView(firstLine)
                    }
                    if (secondLine.visibility == View.VISIBLE) {
                        hideView(secondLine)
                    }
                    if (thirdLine.visibility == View.VISIBLE) {
                        hideView(thirdLine)
                    }
                }
                2 -> {
                    if (firstLine.visibility == View.GONE) {
                        showView(firstLine)
                    }
                    if (secondLine.visibility == View.GONE) {
                        showView(secondLine)
                    }
                    if (thirdLine.visibility == View.VISIBLE) {
                        hideView(thirdLine)
                    }
                }
                3 -> {
                    if (firstLine.visibility == View.GONE) {
                        showView(firstLine)
                    }
                    if (secondLine.visibility == View.GONE) {
                        showView(secondLine)
                    }
                    if (thirdLine.visibility == View.GONE) {
                        showView(thirdLine)
                    }
                }
            }
            scope.launch(Dispatchers.Main) {
                delay(1500)
                if (firstLine.visibility == View.VISIBLE) {
                    hideView(firstLine)
                }
                if (secondLine.visibility == View.VISIBLE) {
                    hideView(secondLine)
                }
                if (thirdLine.visibility == View.VISIBLE) {
                    hideView(thirdLine)
                }
            }
        }
    }


    private fun startChooseGameActivity() {
        val chooseGameActivityIntent = Intent(this, ChooseGameActivity::class.java)
        startActivity(chooseGameActivityIntent)
        finish()
    }

    private fun saveUserChanges() {
        if (profileAvatar != null) {
            mainViewModel.updateUserAvatar(profileAvatar!!)
        }
        binding.apply {
            val name = editName.text.toString()
            val surName = editSurname.text.toString()
            val userName = editUsername.text.toString()
            if (name.isNotEmpty() && name.isNotBlank()) {
                mainViewModel.updateName(name)
            }
            if (surName.isNotEmpty() && surName.isNotBlank()) {
                mainViewModel.updateSurName(surName)
            }
            if (userName.isNotEmpty() && userName.isNotBlank()) {
                mainViewModel.updateUserName(userName)
            }
        }
        switchToDefaultUserMode()
    }

    private fun switchToEditUserMode() {
        binding.apply {
            hideView(buttonBlog)
            hideView(buttonPrivacy)
            hideView(buttonEdit)
            hideView(userUsername)
            hideView(userNameSurname)
            showView(editName)
            editName.isCursorVisible = true
            showView(editSurname)
            editSurname.isCursorVisible = true
            showView(editUsername)
            editUsername.isCursorVisible = true
            buttonEditAvatar.apply {
                showView(this)
                setOnClickListener {
                    launchEditAvatar()
                }
            }
            buttonSaveChanges.apply {
                showView(this)
                setOnClickListener {
                    saveUserChanges()
                }
            }
        }
    }

    private fun switchToDefaultUserMode() {
        binding.apply {
            showView(buttonBlog)
            showView(buttonPrivacy)
            showView(buttonEdit)
            showView(userUsername)
            showView(userNameSurname)
            hideView(buttonSaveChanges)
            hideView(buttonEditAvatar)
            hideView(editName)
            editName.isCursorVisible = false
            hideView(editSurname)
            editSurname.isCursorVisible = false
            hideView(editUsername)
            editUsername.isCursorVisible = false
            buttonEditAvatar.apply {
                hideView(this)
                setOnClickListener(null)
            }
            buttonSaveChanges.apply {
                hideView(this)
                setOnClickListener(null)
            }
        }
    }

    private fun showMenu() {
        binding.apply {
            backgroundLayout.apply {
                setOnClickListener {
                    hideMenu()
                }
                isVisible = true
                isClickable = true
                showView(binding.menuLayout)
            }
            buttonBlog.apply {
                setOnClickListener {
                    findNavController(R.id.fragment_container).navigate(R.id.action_gameFragment_to_blogsFragment)
                    hideMenu()
                }
                isClickable = true
            }
            buttonPrivacy.apply {
                setOnClickListener {
                    findNavController(R.id.fragment_container).navigate(R.id.action_gameFragment_to_privacyPolicyFragment)
                    hideMenu()
                }
                isClickable = true
            }
            buttonCloseMenu.apply {
                setOnClickListener {
                    hideMenu()
                }
                isClickable = true
            }
            buttonEdit.apply {
                setOnClickListener {
                    switchToEditUserMode()
                }
                isClickable = true
            }
        }
    }

    private fun hideMenu() {
        binding.apply {
            if (buttonSaveChanges.isVisible) {
                switchToDefaultUserMode()
            }
            backgroundLayout.apply {
                setOnClickListener(null)
                isVisible = false
                isClickable = false
                hideView(binding.menuLayout)
            }
            buttonBlog.apply {
                setOnClickListener(null)
                isClickable = false
            }
            buttonPrivacy.apply {
                setOnClickListener(null)
                isClickable = false
            }
            buttonCloseMenu.apply {
                setOnClickListener(null)
                isClickable = false
            }
        }
    }

    private fun showTopControlPanel() {
        binding.apply {
            topControlPanel.apply {
                if (!isVisible) {
                    startAnimation(showFromCenterAnimation)
                    isVisible = true
                    isClickable = true
                    isFocusable = true
                }
            }
            buttonMenu.apply {
                isVisible = true
                isClickable = true
                isFocusable = true
            }
        }
    }

    private fun hideTopControlPanel() {
        binding.apply {
            topControlPanel.apply {
                if (isVisible) {
                    startAnimation(hideToCenterAnimation)
                    isVisible = false
                    isClickable = false
                    isFocusable = false
                }
            }
            buttonNavigateToChooseGameFragment.apply {
                isVisible = false
                isClickable = false
                isFocusable = false
            }
            buttonMenu.apply {
                isVisible = false
                isClickable = false
                isFocusable = false
            }
        }
    }

    private fun showBottomControlPanel() {
        binding.apply {
            bottomControlPanel.apply {
                if (!isVisible) {
                    startAnimation(showFromCenterAnimation)
                    isVisible = true
                    isClickable = true
                    isFocusable = true
                }
            }
            linealLayout.apply {
                showView(this)
                setOnClickListener {
                    mainViewModel.addLineal()
                }
            }
            rateLinealLayout.apply {
                showView(this)
                setOnClickListener {
                    mainViewModel.addRateLineal()
                }
            }
            buttonInfo.apply {
                showView(this)
                setOnClickListener {
                    showInfoView()
                }
            }
        }
    }

    private fun hideBottomControlPanel() {
        binding.apply {
            bottomControlPanel.apply {
                if (isVisible) {
                    startAnimation(hideToCenterAnimation)
                    isVisible = false
                    isClickable = false
                    isFocusable = false
                }
            }
            linealLayout.apply {
                hideView(this)
                setOnClickListener(null)
            }
            rateLinealLayout.apply {
                hideView(this)
                setOnClickListener(null)
            }
            buttonInfo.apply {
                hideView(this)
                setOnClickListener(null)
            }
        }
    }

    private fun showInfoView() {
        binding.apply {
            backgroundLayout.apply {
                setOnClickListener {
                    hideInfoView()
                }
                isVisible = true
                isClickable = true
                showView(infoLayout)
            }
            buttonCloseInfo.apply {
                setOnClickListener {
                    hideInfoView()
                }
                isClickable = true
            }
        }
    }

    private fun hideInfoView() {
        binding.apply {
            backgroundLayout.apply {
                setOnClickListener(null)
                isVisible = false
                isClickable = false
                hideView(infoLayout)
            }
            buttonCloseInfo.apply {
                setOnClickListener(null)
                isClickable = true
            }
        }
    }

    private fun showView(view: View) {
        view.apply {
            if (!isVisible) {
                startAnimation(showFromCenterAnimation)
                visibility = View.VISIBLE
                isClickable = true
            }
        }
    }

    private fun hideView(view: View) {
        view.apply {
            if (isVisible) {
                startAnimation(hideToCenterAnimation)
                visibility = View.GONE
                isClickable = false
            }
        }
    }

    private fun launchEditAvatar() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 456)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 456 && data != null) {
            val bmp = data.extras?.get("data")
            if (bmp != null) {
                val bitmap = bmp as Bitmap
                profileAvatar = bitmap
                binding.userAvatar.setImageBitmap(bitmap)
            }
        }
    }

    override fun onBackPressed() {
        if (findNavController(R.id.fragment_container).currentDestination?.id == R.id.bonusGameFragment) {
            findNavController(R.id.fragment_container).navigateUp()
            return
        }
        if (isSpinningMode) {
            return
        }
        if (binding.backgroundLayout.isVisible) {
            hideMenu()
            return
        }
        if (binding.infoLayout.isVisible) {
            hideInfoView()
            return
        }
        when (findNavController(R.id.fragment_container).currentDestination?.id) {
            R.id.gameFragment -> {
                mainViewModel.resetCurrentWin()
                startChooseGameActivity()
                finish()
            }
            else -> {
                findNavController(R.id.fragment_container).navigateUp()
            }
        }
    }
}