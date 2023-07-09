package ru.gb.veber.newsapi.presentation.search.recycler

import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.domain.models.HistorySelectModel
import ru.gb.veber.ui_common.hide
import ru.gb.veber.ui_common.show
import ru.gb.veber.ui_core.databinding.HistorySelectItemBinding

class HistorySelectViewHolder(
    private val binding: HistorySelectItemBinding,
    var listener: RecyclerListenerHistorySelect,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: HistorySelectModel) = with(binding) {

        root.setOnClickListener {
            listener.clickHistoryItem(item)
        }

        deleteHistoryItem.setOnClickListener {
            listener.deleteHistoryItem(item)
        }

        if (item.keyWord.toString().isEmpty()) {
            searchIcon.hide()
            keyWord.hide()
        } else {
            searchIcon.show()
            keyWord.show()
        }

        if (item.sourcesName.toString().isEmpty()) {
            sourcesName.hide()
            sourcesIcon.hide()
        } else {
            sourcesIcon.show()
            sourcesName.show()
        }

        keyWord.text = item.keyWord
        sourcesName.text = item.sourcesName
        dateSources.text = item.dateSources
    }
}
