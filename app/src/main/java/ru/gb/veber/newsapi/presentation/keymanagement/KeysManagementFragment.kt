package ru.gb.veber.newsapi.presentation.keymanagement

import KeysDialogFragment
import android.R.attr.bottom
import android.R.attr.left
import android.R.attr.right
import android.R.attr.top
import android.graphics.drawable.Drawable
import android.transition.TransitionManager
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.setFragmentResultListener
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.base.NewsFragment
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.observeFlow
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.common.screen.WebViewScreen
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.BundleInt
import ru.gb.veber.newsapi.common.utils.PAGE_GET_KEY
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.KeysManagementFragmentBinding
import ru.gb.veber.newsapi.domain.models.ApiKeysModel
import ru.gb.veber.newsapi.presentation.keymanagement.recycler.KeysListener
import ru.gb.veber.newsapi.presentation.keymanagement.recycler.KeysManagementAdapter
import java.lang.reflect.Field
import javax.inject.Inject


class KeysManagementFragment :
    NewsFragment<KeysManagementFragmentBinding, KeysManagementViewModel>(
        KeysManagementFragmentBinding::inflate) {

    private var accountId: Int by BundleInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)

    @Inject
    lateinit var router: Router

    private val listener = object : KeysListener {
        override fun checkBoxClick(apiKeyModel: ApiKeysModel) {
            viewModel.setNewKey(apiKeyModel)
        }

        override fun holderClick() {}
    }

    private val keysAdapter = KeysManagementAdapter(listener)

    override fun getViewModelClass(): Class<KeysManagementViewModel> =
        KeysManagementViewModel::class.java

    override fun onInject() {
        App.instance.appComponent.inject(this)
    }

    override fun onInitView() {
        binding.recyclerKeys.adapter = keysAdapter
        binding.recyclerKeys.itemAnimator = null

        binding.back.setOnClickListener {
            viewModel.back()
        }

        binding.addKeysButton.setOnClickListener {
            KeysDialogFragment().show(requireActivity().supportFragmentManager, "")
        }

        binding.goToSiteB.setOnClickListener {
            router.navigateTo(WebViewScreen(PAGE_GET_KEY))
        }

        val drawablesFields: Array<Field> = R.drawable::class.java.fields
//        val drawables: ArrayList<Drawable> = ArrayList()
//
//        for (field in drawablesFields) {
//            try {
//               // drawables.add(resources.getDrawable(field.getInt(null)))
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }


//        drawablesFields.filter { it.name != it.name.substringBefore("ic_") }
//            .filter { it.name != it.name.substringBefore("baseline") }.forEach {
//            Log.d("filter", it.name)
//        }


//       drawablesFields.filter { it.name ==it.name.substringBefore("ic_").substringBefore("baseline") && it.name == it.name.substringBefore("baseline")}
//           .forEach {
//               Log.d("forEach", it.name)
//            }

        drawablesFields.filter { it.name ==it.name.substringBefore("ic_").substringBefore("baseline") }
            .forEach {
                Log.d("forEach", it.name)
            }

        val id: Int = context?.resources?.getIdentifier("drawable/" + drawablesFields[7].name,
            null,
            requireContext().packageName) ?: R.drawable.news_api_logo
        val id2: Int = context?.resources?.getIdentifier("drawable/" + drawablesFields[8].name,
            null,
            requireContext().packageName) ?: R.drawable.news_api_logo
        val id3: Int = context?.resources?.getIdentifier("drawable/" + drawablesFields[9].name,
            null,
            requireContext().packageName) ?: R.drawable.news_api_logo

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(40, 20, 20, 20)

        binding.lineaLayout.addView(ImageView(context).apply {
            setImageResource(id)
            layoutParams = params
        })
        binding.lineaLayout.addView(ImageView(context).apply {
            setImageResource(id2)
            layoutParams = params
        })
        binding.lineaLayout.addView(ImageView(context).apply {
            setImageResource(id3)
            layoutParams = params
        })
    }

    override fun onObserveData() {
        viewModel.keysManagementFlow.observeFlow(this) { state ->
            when (state) {
                is KeysManagementViewModel.KeysManagementState.SetKeys -> {
                    setKeys(state.keys)
                }
                KeysManagementViewModel.KeysManagementState.StartedState -> {}
                KeysManagementViewModel.KeysManagementState.EmptyList -> {
                    emptyList()
                }
            }
        }

        setFragmentResultListener(RESULT_KEY_LISTENER) { _, bundle ->
            val newKey = bundle.getString(RESULT_KEY_DATA) ?: ""
            viewModel.addKey(accountId, newKey)
        }
    }

    override fun onViewInited() {
        viewModel.getKeys(accountId)
    }

    private fun setKeys(keys: List<ApiKeysModel>) {
        TransitionManager.beginDelayedTransition(binding.root)
        keysAdapter.keys = keys
        binding.emptyList.hide()
    }

    private fun emptyList() {
        binding.emptyList.show()
    }

    companion object {
        const val RESULT_KEY_LISTENER = "RESULT_KEY_LISTENER"
        const val RESULT_KEY_DATA = "RESULT_KEY_DATA"
        fun getInstance(accountId: Int) = KeysManagementFragment().apply {
            this.accountId = accountId
        }
    }
}
