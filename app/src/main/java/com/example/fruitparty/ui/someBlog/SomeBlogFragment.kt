package com.example.fruitparty.ui.someBlog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.example.fruitparty.R
import com.example.fruitparty.data.services.Constants.BLOG_TITLE
import com.example.fruitparty.databinding.FragmentSomeBlogBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SomeBlogFragment : Fragment() {

    private lateinit var binding: FragmentSomeBlogBinding
    private val someBlogViewModel by viewModel<SomeBlogViewModel>(parameters = {
        parametersOf(
            blogTitle
        )
    })
    private val blogTitle by lazy { requireArguments().getString(BLOG_TITLE) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSomeBlogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        someBlogViewModel.apply {
            liveDataSomeBlog.observe(viewLifecycleOwner) { blog ->
                binding.apply {
                    Glide.with(blogImage)
                        .load(blog.img.isobar)
                        .transition(
                            GenericTransitionOptions.with(R.anim.fade_in)
                        )
                        .centerCrop()
                        .error(R.drawable.image_error)
                        .into(blogImage)
                    blogTitle.text = blog.title.rendered
                    blogContent.text = blog.content.rendered
                }
            }
            subscribeSomeBlog()
            emitSomeBlog()
            emitCurrentFragment(R.id.someBlogFragment)
        }
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        someBlogViewModel.cancelViewModelScopeCoroutines()
    }
}