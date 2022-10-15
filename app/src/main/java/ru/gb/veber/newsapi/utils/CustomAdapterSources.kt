package ru.gb.veber.newsapi.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ru.gb.veber.newsapi.databinding.SelectSourcesAutocompileBinding
import ru.gb.veber.newsapi.model.Sources

typealias AutoCompileClick = (Sources) -> Unit

class CustomAdapterSources(
    private val sourcesList: List<Sources>,
    private val autoCompileClick: AutoCompileClick,
    context: Context,
) : ArrayAdapter<Sources>(context,0,sourcesList), View.OnClickListener {

    override fun getCount() = sourcesList.size
    override fun getItem(position: Int) = sourcesList[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convert: View?, parent: ViewGroup): View {
        val binding =
            convert?.tag as SelectSourcesAutocompileBinding? ?: createBinding(parent.context)
        val sources = getItem(position)
        binding.textSourcesName.text = sources.name
        binding.checkSources.tag = sources
//        binding.checkSources.isChecked = true
        return binding.root
    }

    private fun createBinding(context: Context?): SelectSourcesAutocompileBinding {
        val binding = SelectSourcesAutocompileBinding.inflate(LayoutInflater.from(context))
        binding.checkSources.setOnClickListener(this)
        binding.root.tag = binding
        return binding
    }

    override fun onClick(view: View) {
        val sources = view.tag as Sources
        autoCompileClick.invoke(sources)
    }
}