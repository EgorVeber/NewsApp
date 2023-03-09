package ru.gb.veber.newsapi.view.profile.account.settings.customize

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.databinding.CustomizeCategoryItemTaskBinding
import ru.gb.veber.newsapi.view.profile.account.settings.customize.helper.ItemTouchHelperAdapter
import kotlin.collections.ArrayList

class CustomizeCategoryAdapter() :
    RecyclerView.Adapter<CustomizeCategoryAdapter.ItemViewHolder>(), ItemTouchHelperAdapter {

    private var dataList: MutableList<Category> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            CustomizeCategoryItemTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    fun refreshCategory(categories: MutableList<Category>) {
        this.dataList = categories
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(private val binding: CustomizeCategoryItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Category) {
            binding.categoryText.text = dataList[layoutPosition].category
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        dataList.removeAt(fromPosition).apply {
            dataList.add(toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }
}