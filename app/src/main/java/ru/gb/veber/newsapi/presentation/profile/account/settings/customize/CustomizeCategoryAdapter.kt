package ru.gb.veber.newsapi.presentation.profile.account.settings.customize

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.databinding.CustomizeCategoryItemTaskBinding
import ru.gb.veber.newsapi.presentation.profile.account.settings.customize.helper.EventDraw
import ru.gb.veber.newsapi.presentation.profile.account.settings.customize.helper.ItemTouchHelperAdapter
import java.util.*

class CustomizeCategoryAdapter(private val listener: EventDraw) :
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
            binding.categoryText.text = data.category
            binding.hamburgerCustom.setOnTouchListener {_, motionEvent ->
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    listener.onStartDrag(this)
                }
                false
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(dataList,fromPosition,toPosition)
        notifyItemMoved(fromPosition, toPosition)
        listener.setNewList(dataList)
    }

    override fun onItemDismiss(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }
}