package ru.gb.veber.newsapi.view.profile.account.settings.customize

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.CustomizeCategoryFragmentBinding
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.profile.account.settings.customize.helper.EventDraw
import ru.gb.veber.newsapi.view.profile.account.settings.customize.helper.SimpleItemTouchHelperCallback
import javax.inject.Inject

class CustomizeCategoryFragment() : Fragment(), BackPressedListener {

    private var _binding: CustomizeCategoryFragmentBinding? = null
    private val binding get() = _binding!!
    private var mItemTouchHelper: ItemTouchHelper? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val customizeCategoryViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[CustomizeCategoryViewModel::class.java]
    }

    private val listener = object : EventDraw {
        override fun onStartDrag(holder: CustomizeCategoryAdapter.ItemViewHolder) {
            mItemTouchHelper?.startDrag(holder)
        }

        override fun setNewList(dataList: MutableList<Category>) {
            Log.d("NewList", "setNewList() called with: dataList = $dataList")
        }

    }

    private val myAdapter = CustomizeCategoryAdapter(listener)

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
        initRecycler()
        initialize()
        initViewModel()

        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(myAdapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper!!.attachToRecyclerView(binding.recyclerView)
    }

    override fun onBackPressedRouter(): Boolean {
        return customizeCategoryViewModel.onBackPressedRouter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecycler() {
        binding.recyclerView.apply {
            this.adapter = myAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initViewModel() {
        customizeCategoryViewModel.getLiveDataCategory().observe(viewLifecycleOwner) { listCategory ->
                myAdapter.refreshCategory(listCategory)
        }
    }

    private fun initialize() {
        binding.backAccountScreen.setOnClickListener {
            customizeCategoryViewModel.backAccountScreen()
        }
    }

    companion object {
        fun getInstance(bundle: Bundle) = CustomizeCategoryFragment().apply { arguments = bundle }
    }
}