package com.example.fruitparty.ui.blogs

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fruitparty.R
import com.example.fruitparty.data.services.Constants
import com.example.fruitparty.data.services.Constants.BLOG_TITLE
import com.example.fruitparty.data.services.Constants.NO_INTERNET_CONNECTION
import com.example.fruitparty.data.services.Constants.RETRY
import com.example.fruitparty.data.services.DataLoadingState
import com.example.fruitparty.databinding.FragmentBlogsBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class BlogsFragment : Fragment() {

    private lateinit var binding: FragmentBlogsBinding
    private val blogsViewModel by viewModel<BlogsViewModel>()
    private val blogsAdapter = BlogsAdapter(openSomeBlog = {
        findNavController().navigate(
            R.id.action_blogsFragment_to_someBlogFragment,
            bundleOf(BLOG_TITLE to it)
        )
    })
    private var snack: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.blogsList.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = blogsAdapter
            isMotionEventSplittingEnabled = false
        }
        blogsViewModel.apply {
            liveDataBlogsLoadingState.observe(viewLifecycleOwner) {
                Log.d(Constants.TAG_TEST, it.status.toString())
                binding.progressBar.apply {
                    when (it.status) {
                        DataLoadingState.Status.LOADING_BLOGS -> {
                            isVisible = true
                        }
                        DataLoadingState.Status.LOADED_BLOGS -> {
                            isVisible = false
                            blogsAdapter.submitList(it.blogsList)
                        }
                        DataLoadingState.Status.BAD_URL -> {
                            isVisible = false
                            showSnackBadUrl()
                        }
                        DataLoadingState.Status.ERROR_INTERNET_CONNECTION, DataLoadingState.Status.ERROR_TIMEOUT_EXPIRED -> {
                            isVisible = false
                            showSnackConnectionProblems()
                        }
                        DataLoadingState.Status.READING_BLOGS_URL_FROM_FIREBASE -> {
                            isVisible = true
                        }
                        DataLoadingState.Status.LOADED_BLOGS_URL_FROM_FIREBASE -> {
                            blogsViewModel.updateArticleUrl(it.msg!!)
                            isVisible = false
                            getBlogs()
                        }
                        else -> {}
                    }
                }
            }
            subscribeBlogsLoadingState()
            subscribeFlowDataLoadingState()
            if (blogsAdapter.currentList.isEmpty()) {
                getBlogs()
            }
            emitCurrentFragment(R.id.blogsFragment)
        }
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showSnackConnectionProblems() {
        snack = Snackbar.make(
            binding.root,
            (NO_INTERNET_CONNECTION),
            Snackbar.LENGTH_INDEFINITE
        )
            .setTextColor(Color.BLACK)
            .setBackgroundTint(Color.WHITE)
            .setAction(RETRY) {
                blogsViewModel.getBlogs()
            }
            .setActionTextColor(Color.BLACK)
        snack!!.show()
    }

    private fun showSnackBadUrl() {
        snack = Snackbar.make(
            binding.root,
            (NO_INTERNET_CONNECTION),
            Snackbar.LENGTH_INDEFINITE
        )
            .setTextColor(Color.BLACK)
            .setBackgroundTint(Color.WHITE)
            .setAction(RETRY) {
                blogsViewModel.readUrlFromFirebase()
            }
            .setActionTextColor(Color.BLACK)
        snack!!.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (snack != null) {
            if (snack!!.isShown) {
                snack!!.dismiss()
            }
        }
        blogsViewModel.cancelViewModelScopeCoroutines()
    }
}