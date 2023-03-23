package ru.gb.veber.newsapi.presentation.sources.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.common.utils.FOCUS_BRIF
import ru.gb.veber.newsapi.common.utils.FOCUS_NAME
import ru.gb.veber.newsapi.databinding.SourcesItemBinding
import ru.gb.veber.newsapi.domain.models.Sources

class SourcesViewHolder(
    private val binding: SourcesItemBinding,
    private val listener: SourcesListener,
) : RecyclerView.ViewHolder(binding.root) {

    private fun hideDesc() = with(binding) {
        groupAbout.hide()
    }

    private fun hideSubs() = with(binding) {
        country.hide()
        totalFavorites.hide()
        totalHistory.hide()
        helperTextTH.hide()
        helperTextTF.hide()
    }

    private fun showDesc() = with(binding) {
        groupAbout.show()
    }

    private fun showSubs() = with(binding) {
        country.show()
        totalFavorites.show()
        totalHistory.show()
        helperTextTH.show()
        helperTextTF.show()
    }

    private fun itemState(item: Sources) {
        when (item.focusType) {
            FOCUS_NAME -> { hideDesc(); hideSubs(); }
            FOCUS_BRIF -> { hideDesc(); showSubs(); }
            else -> { showDesc(); showSubs(); }
        }
    }

    fun bind(item: Sources) = with(binding) {

        if (item.liked) imageFavorites.setImageResource(R.drawable.ic_favorite_36_active)
        else imageFavorites.setImageResource(R.drawable.ic_favorite_36)

        name.text = item.name
        description.text = item.description
        category.text = item.category
        language.text = item.language
        country.text = item.country
        totalFavorites.text = item.totalFavorites.toString()
        totalHistory.text = item.totalHistory.toString()
        itemState(item)

        root.setOnClickListener {
            item.focusType += if (item.focusType < 2) 2
            else -2
            listener.focus(item, item.focusType)
            itemState(item)
        }

        openNewsSources.setOnClickListener {
            listener.newsClick(item.idSources, item.name)
        }

        openWebSiteSources.setOnClickListener {
            listener.openUrl(item.url)
        }

        clickFavorites.setOnClickListener {
            listener.imageClick(item)
        }

    }
}
