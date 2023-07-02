package ru.gb.veber.newsapi.presentation.search.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.databinding.HistorySelectItemBinding
import ru.gb.veber.newsapi.domain.models.HistorySelectModel

class HistorySelectAdapter(
    var listener: RecyclerListenerHistorySelect,
) : RecyclerView.Adapter<HistorySelectViewHolder>() {

    var historySelectModelList: List<HistorySelectModel> = listOf()
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

    override fun getItemCount() = historySelectModelList.size

    override fun onBindViewHolder(holder: HistorySelectViewHolder, position: Int) {
        holder.bind(historySelectModelList[position])
    }
}

