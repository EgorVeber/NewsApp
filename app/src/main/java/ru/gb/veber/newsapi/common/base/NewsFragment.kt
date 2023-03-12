package ru.gb.veber.newsapi.common.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import javax.inject.Inject

/** Базовый фрагмент наследоватся не обязательно*/
abstract class NewsFragment<T : ViewBinding, ViewModel : NewsViewModel>(
    private val inflateBinding: (inflater: LayoutInflater, root: ViewGroup?, attachToRoot: Boolean) -> T,
) : Fragment(), BackPressedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var _binding: T? = null

    protected val binding: T
        get() = _binding!!

    protected lateinit var viewModel: ViewModel

    protected abstract fun getViewModelClass(): Class<ViewModel>

    protected abstract fun onInject()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onInject()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = inflateBinding.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitViewModel()
        onInitView()
        onObserveData()
        onStartAction()
    }

    protected open fun onInitView() {}

    private fun onInitViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)[getViewModelClass()]
    }

    protected open fun onObserveData() {}

    protected open fun onStartAction() {}


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        viewModel.onBackPressedRouter()
        return true
    }
}
