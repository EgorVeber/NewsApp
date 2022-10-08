package ru.gb.veber.newsapi.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import java.util.*
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.databinding.SelectSourcesAutocompileBinding
import ru.gb.veber.newsapi.model.Sources

import java.util.ArrayList;

class AutoCompleteCountryAdapter(
    context: Context,
    countryList: List<Sources>,
) : ArrayAdapter<Sources>(context, 0, countryList) {

    private val countryListFull: List<Sources>

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
        if (sources?.isLike == true) {
            binding.checkSources.show()
        } else {
            binding.checkSources.hide()
        }
        binding.checkSources.tag = sources
//        binding.checkSources.isChecked = true
        return binding.root
    }


    private val countryFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            val suggestions: MutableList<Sources> = ArrayList()
            if (constraint == null || constraint.length == 0) {
                suggestions.addAll(countryListFull)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in countryListFull) {
                    if (item.name!!.toLowerCase().contains(filterPattern)) {
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
            addAll(p1?.values as List<Sources>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any): CharSequence {
            return (resultValue as Sources).name.toString()
        }
    }
}