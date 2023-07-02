package ru.gb.veber.newsapi.presentation.keymanagement.recycler

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.databinding.KeysManagementItemBinding
import ru.gb.veber.newsapi.domain.models.ApiKeysModel

class KeysManagementViewHolder(
    private val binding: KeysManagementItemBinding,
    private val listener: KeysListener,
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(apiKeyModel: ApiKeysModel) = with(binding) {
        checkBoxKey.isChecked = apiKeyModel.actived
        checkBoxKey.isEnabled = apiKeyModel.actived == false
        checkBoxKey.text = apiKeyModel.keyApi.subKey()
        lastRequestTv.text = apiKeyModel.lastRequest
        countKeyTv.text = apiKeyModel.countRequest + "/" + apiKeyModel.countMax

        checkBoxKey.setOnCheckedChangeListener { view, cheked ->
            if (view.isPressed) listener.checkBoxClick(apiKeyModel)
        }
    }

    private fun String.subKey(): String {
        return if (this.length >= 10) {
            substring(0, 10) + "..."
        } else {
            this
        }
    }
}
