package ru.gb.veber.newsapi.view.profile.account.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.CustomizeCategoryFragmentBinding
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import javax.inject.Inject

class CustomizeCategoryFragment : Fragment(), BackPressedListener {

    private var _binding: CustomizeCategoryFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val customizeCategoryViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[CustomizeCategoryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CustomizeCategoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.instance.appComponent.inject(this)
        initialize()
    }

    override fun onBackPressedRouter(): Boolean {
        return customizeCategoryViewModel.onBackPressedRouter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initialize() {
        binding.backAccountScreen.setOnClickListener {
            customizeCategoryViewModel.backAccountScreen()
        }
    }

    companion object {
        fun getInstance() = CustomizeCategoryFragment()
    }
}