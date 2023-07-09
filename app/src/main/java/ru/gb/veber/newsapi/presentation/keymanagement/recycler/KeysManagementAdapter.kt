package ru.gb.veber.newsapi.presentation.keymanagement.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.domain.models.ApiKeysModel
import ru.gb.veber.ui_core.databinding.KeysManagementItemBinding

class KeysManagementAdapter(
    var listener: KeysListener,
) : RecyclerView.Adapter<KeysManagementViewHolder>() {

    var keys: List<ApiKeysModel> = emptyList()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(KeysDiffUtil(field, value))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeysManagementViewHolder {
        return KeysManagementViewHolder(
            KeysManagementItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            listener
        )
    }

    override fun onBindViewHolder(holder: KeysManagementViewHolder, position: Int) {
        holder.bind(keys[position])
    }

    override fun getItemCount() = keys.size
}
