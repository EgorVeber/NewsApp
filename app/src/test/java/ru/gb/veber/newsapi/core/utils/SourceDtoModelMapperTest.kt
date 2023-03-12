package ru.gb.veber.newsapi.core.utils

import org.junit.Assert.*
import org.junit.Test
import ru.gb.veber.newsapi.model.Source
import ru.gb.veber.newsapi.data.network.SourceDTO
import ru.gb.veber.newsapi.data.mapper.toSource

class SourceDtoModelMapperTest {

    @Test
    fun sourceDtoModelMapper_mapToSource_Equals() {
        val id = "id"
        val name = "sources"
        val value = SourceDTO(id, name)
        assertEquals(Source(id, name), value.toSource())
    }

    @Test
    fun sourceDtoModelMapper_mapToSource_NotEquals() {
        val id = "id"
        val name = "sources"
        val value = SourceDTO(id, name)
        assertNotEquals((Source(id, name + "notEquals")), value.toSource())
    }

    @Test
    fun sourceDtoModelMapper_mapToSource_NotSame() {
        val id = "id"
        val name = "sources"
        val value = SourceDTO(id, name)
        assertNotSame((Source(id, name)), value.toSource())
    }

    @Test(expected = AssertionError::class)
    fun sourceDtoModelMapper_mapToSource_Same() {
        val id = "id"
        val name = "sources"
        val value = SourceDTO(id, name)
        assertSame((Source(id, name)), value.toSource())
    }
}
