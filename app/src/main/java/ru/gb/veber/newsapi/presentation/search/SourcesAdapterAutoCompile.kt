package ru.gb.veber.newsapi.presentation.search

import android.content.Context
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.databinding.SelectSourcesAutocompileBinding
import ru.gb.veber.newsapi.domain.models.SourcesModel
import java.util.Locale

class SourcesAdapterAutoCompile(
    context: Context,
    countryList: List<SourcesModel>,
) : ArrayAdapter<SourcesModel>(context, 0, countryList) {

    var countryListFull: List<SourcesModel>

    init {
        countryListFull = ArrayList(countryList)
    }

    @NonNull
    override fun getFilter(): Filter {
        return countryFilter
    }

    private fun createBinding(context: Context?): SelectSourcesAutocompileBinding {
        val binding = SelectSourcesAutocompileBinding.inflate(LayoutInflater.from(context))
        binding.root.tag = binding
        return binding
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding =
            convertView?.tag as SelectSourcesAutocompileBinding? ?: createBinding(parent.context)
        val sources = getItem(position)
        binding.textSourcesName.text = sources?.name
        if (sources?.liked == true) {
            binding.checkSources.show()
        } else {
            binding.checkSources.hide()
        }
        binding.countryName.text = sources?.country
        binding.checkSources.tag = sources
        return binding.root
    }


    private val countryFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            val suggestions: MutableList<SourcesModel> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                suggestions.addAll(countryListFull)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in countryListFull) {
                    if (item.name!!.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        suggestions.add(item)
                    }
                }
            }
            results.values = suggestions
            results.count = suggestions.size
            return results
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            clear()
            addAll(p1?.values as List<SourcesModel>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any): CharSequence {
            return (resultValue as SourcesModel).name.toString()
        }
    }
}
