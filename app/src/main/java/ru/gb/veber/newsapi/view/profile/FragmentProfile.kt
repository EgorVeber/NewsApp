package ru.gb.veber.newsapi.view.profile

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FragmentMprofileBinding
import ru.gb.veber.newsapi.presenter.FragmentProfilePresenter
import ru.gb.veber.newsapi.view.activity.BackPressedListener

class FragmentProfile : MvpAppCompatFragment(), FragmentProfileView, BackPressedListener {

    private var _binding: FragmentMprofileBinding? = null
    private val binding get() = _binding!!

    private val presenter: FragmentProfilePresenter by moxyPresenter {
        FragmentProfilePresenter(App.instance.router)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMprofileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun init() {

        binding.progressBar.max = 100
        binding.progressBar.progress = 10


        var spanableStringBuilder =
            SpannableStringBuilder(binding.textviewsda.text)
        spanableStringBuilder.setSpan(
            ImageSpan(requireContext(), R.drawable.ic_baseline_open_in_new_24),
            spanableStringBuilder.length - 1,
            spanableStringBuilder.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        binding.textviewsda.setOnClickListener {
            Log.d("TAG", "init() called")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    companion object {
        fun getInstance(): FragmentProfile {
            return FragmentProfile()
        }
    }
}