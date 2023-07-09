package ru.gb.veber.newsapi.presentation.sources.recycler

import androidx.recyclerview.widget.RecyclerView
import ru.gb.veber.newsapi.common.UiCoreDrawable
import ru.gb.veber.newsapi.domain.models.SourcesModel
import ru.gb.veber.newsapi.presentation.sources.SourcesFragment.Companion.FOCUS_BRIF
import ru.gb.veber.newsapi.presentation.sources.SourcesFragment.Companion.FOCUS_NAME
import ru.gb.veber.ui_common.hide
import ru.gb.veber.ui_common.show
import ru.gb.veber.ui_core.databinding.SourcesItemBinding

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

    private fun itemState(item: SourcesModel) {
        when (item.focusType) {
            FOCUS_NAME -> {
                hideDesc(); hideSubs(); }

            FOCUS_BRIF -> {
                hideDesc(); showSubs(); }

            else -> {
                showDesc(); showSubs(); }
        }
    }

    fun bind(item: SourcesModel) = with(binding) {

        if (item.liked) imageFavorites.setImageResource(UiCoreDrawable.ic_favorite_36_active)
        else imageFavorites.setImageResource(UiCoreDrawable.ic_favorite_36)

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
