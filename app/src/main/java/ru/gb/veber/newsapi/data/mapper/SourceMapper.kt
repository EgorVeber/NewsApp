package ru.gb.veber.newsapi.data.mapper
import ru.gb.veber.newsapi.model.Source
import ru.gb.veber.newsapi.data.network.SourceDTO

fun SourceDTO.toSource(): Source {
    return Source(
        id = this.id,
        name = this.name
    )
}

fun getNewSource(sourcesId: String, sourcesName: String):Source{
    return Source(
        id = sourcesId,
        name = sourcesName
    )
}
