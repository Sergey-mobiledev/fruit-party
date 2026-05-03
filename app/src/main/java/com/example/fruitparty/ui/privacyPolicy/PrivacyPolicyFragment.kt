package com.example.fruitparty.ui.privacyPolicy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fruitparty.R
import com.example.fruitparty.databinding.FragmentPrivacyPolicyBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PrivacyPolicyFragment : Fragment() {

    private lateinit var binding: FragmentPrivacyPolicyBinding
    private val privacyPolicyViewModel by viewModel<PrivacyPolicyViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        privacyPolicyViewModel.apply {
            emitCurrentFragment(R.id.privacyPolicyFragment)
        }
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}