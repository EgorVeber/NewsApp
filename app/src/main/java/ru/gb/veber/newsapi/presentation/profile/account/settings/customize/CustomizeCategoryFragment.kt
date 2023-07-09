package ru.gb.veber.newsapi.presentation.profile.account.settings.customize

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.presentation.base.NewsFragment
import ru.gb.veber.newsapi.presentation.profile.account.settings.customize.helper.EventDraw
import ru.gb.veber.newsapi.presentation.profile.account.settings.customize.helper.SimpleItemTouchHelperCallback
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import ru.gb.veber.ui_common.BUNDLE_ACCOUNT_ID_KEY
import ru.gb.veber.ui_common.utils.BundleInt
import ru.gb.veber.ui_core.databinding.CustomizeCategoryFragmentBinding

class CustomizeCategoryFragment() :
    NewsFragment<CustomizeCategoryFragmentBinding, CustomizeCategoryViewModel>(
        CustomizeCategoryFragmentBinding::inflate
    ) {

    private var mItemTouchHelper: ItemTouchHelper? = null
    private var accountID by BundleInt(BUNDLE_ACCOUNT_ID_KEY, ACCOUNT_ID_DEFAULT)

    private val listener = object : EventDraw {
        override fun onStartDrag(holder: CustomizeCategoryAdapter.ItemViewHolder) {
            mItemTouchHelper?.startDrag(holder)
        }

        override fun setNewList(dataList: MutableList<Category>) {
            Log.d("NewList", "setNewList() called with: dataList = $dataList")
        }
    }

    private val myAdapter = CustomizeCategoryAdapter(listener)

    override fun getViewModelClass(): Class<CustomizeCategoryViewModel> =
        CustomizeCategoryViewModel::class.java

    override fun onInject() {
        App.instance.appComponent.inject(this)
    }

    override fun onInitView() {
        initRecycler()
        binding.backAccountScreen.setOnClickListener {
            viewModel.backAccountScreen()
        }

    }

    override fun onObserveData() {
        viewModel.getLiveDataCategory()
            .observe(viewLifecycleOwner) { listCategory ->
                myAdapter.refreshCategory(listCategory)
            }
    }

    private fun initRecycler() {
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(myAdapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper!!.attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.apply {
            this.adapter = myAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    companion object {
        fun getInstance(accountID: Int) =
            CustomizeCategoryFragment().apply { this.accountID = accountID }
    }
}