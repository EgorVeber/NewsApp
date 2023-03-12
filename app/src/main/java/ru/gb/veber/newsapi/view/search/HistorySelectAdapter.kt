package ru.gb.veber.newsapi.view.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.databinding.HistorySelectItemBinding
import ru.gb.veber.newsapi.domain.models.HistorySelect


interface RecyclerListenerHistorySelect {
    fun clickHistoryItem(historySelect: HistorySelect)
    fun deleteHistoryItem(historySelect: HistorySelect)
}

class HistorySelectAdapter(
    var listener: RecyclerListenerHistorySelect,
) : RecyclerView.Adapter<HistorySelectViewHolder>() {

    var historySelectList: List<HistorySelect> = listOf()
        set(value) {
            var diffUtil = HistoryDiffUtil(field, value)
            var diffResult = DiffUtil.calculateDiff(diffUtil)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorySelectViewHolder {
        return HistorySelectViewHolder(HistorySelectItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false), listener)
    }

    override fun getItemCount() = historySelectList.size

    override fun onBindViewHolder(holder: HistorySelectViewHolder, position: Int) {
        holder.bind(historySelectList[position])
    }
}

class HistorySelectViewHolder(
    private val binding: HistorySelectItemBinding,
    var listener: RecyclerListenerHistorySelect,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: HistorySelect) = with(binding) {

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
