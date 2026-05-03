package com.example.fruitparty.ui.chooseGameActivity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.example.fruitparty.R
import com.example.fruitparty.data.services.Constants
import com.example.fruitparty.databinding.ActivityChooseGameBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChooseGameActivity : AppCompatActivity() {

    private val binding by lazy { ActivityChooseGameBinding.inflate(layoutInflater) }
    private val chooseGameActivityViewModel by viewModel<ChooseGameActivityViewModel>()
    private var doubleBackToExitPressedOnce = false
    private var profileAvatar: Bitmap? = null
    private val showFromCenterAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.show_from_centr)
    }
    private val hideToCenterAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.hide_to_centr)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        chooseGameActivityViewModel.apply {
            liveDataUser.observe(this@ChooseGameActivity) { user ->
                binding.apply {
                    userAvatar.setImageBitmap(user.avatar)
                    userUsername.text = user.userName
                    userNameSurname.text = user.name + " " + user.surname
                }
            }
            liveDataCurrentFragmentId.observe(this@ChooseGameActivity) {
                when (it) {
                    R.id.blogsFragment, R.id.privacyPolicyFragment -> {
                        binding.apply {
                            hideTopControlPanel()
                        }
                    }
                    R.id.chooseGameFragment -> {
                        showTopControlPanel()
                    }
                    1 -> {
                        showLoading()
                    }
                }
            }
            subscribeUser()
            subscribeCurrentFragmentId()
        }
        binding.apply {
            buttonMenu.setOnClickListener {
                if (backgroundLayout.isVisible) {
                    hideMenu()
                } else {
                    showMenu()
                }
            }
        }
    }

    private fun saveUserChanges() {
        if (profileAvatar != null) {
            chooseGameActivityViewModel.updateUserAvatar(profileAvatar!!)
        }
        binding.apply {
            val name = editName.text.toString()
            val surName = editSurname.text.toString()
            val userName = editUsername.text.toString()
            if (name.isNotEmpty() && name.isNotBlank()) {
                chooseGameActivityViewModel.updateName(name)
            }
            if (surName.isNotEmpty() && surName.isNotBlank()) {
                chooseGameActivityViewModel.updateSurName(surName)
            }
            if (userName.isNotEmpty() && userName.isNotBlank()) {
                chooseGameActivityViewModel.updateUserName(userName)
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
                    findNavController(R.id.fragment_container_choose_game).navigate(R.id.action_chooseGameFragment_to_blogsFragment)
                    hideMenu()
                }
                isClickable = true
            }
            buttonPrivacy.apply {
                setOnClickListener {
                    findNavController(R.id.fragment_container_choose_game).navigate(R.id.action_chooseGameFragment_to_privacyPolicyFragment)
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
            buttonMenu.apply {
                isVisible = false
                isClickable = false
                isFocusable = false
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

    private fun showLoading() {
        binding.apply {
            progressBar.isVisible = true
            backgroundLayout.apply {
                isVisible = true
                isClickable = true
            }
        }
    }

    override fun onBackPressed() {
        if (binding.backgroundLayout.isVisible) {
            hideMenu()
            return
        }
        when (findNavController(R.id.fragment_container_choose_game).currentDestination?.id) {
            R.id.chooseGameFragment -> {
                if (doubleBackToExitPressedOnce) {
                    finish()
                    return
                }
                this.doubleBackToExitPressedOnce = true
                Toast.makeText(this, Constants.TOAST_CLICK_BACK_TO_EXIT, Toast.LENGTH_SHORT).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
                return
            }
            else -> {
                findNavController(R.id.fragment_container_choose_game).navigateUp()
            }
        }
    }
}