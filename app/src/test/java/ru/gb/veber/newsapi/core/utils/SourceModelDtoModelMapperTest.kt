package ru.gb.veber.newsapi.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test
import ru.gb.veber.newsapi.data.mapper.toSource
import ru.gb.veber.newsapi.data.models.SourceResponse
import ru.gb.veber.newsapi.domain.models.SourceModel

class SourceModelDtoModelMapperTest {

    @Test
    fun sourceDtoModelMapper_mapToSource_Equals() {
        val id = "id"
        val name = "sources"
        val value = SourceResponse(id, name)
        assertEquals(SourceModel(id, name), value.toSource())
    }

    @Test
    fun sourceDtoModelMapper_mapToSource_NotEquals() {
        val id = "id"
        val name = "sources"
        val value = SourceResponse(id, name)
        assertNotEquals((SourceModel(id, name + "notEquals")), value.toSource())
    }

    @Test
    fun sourceDtoModelMapper_mapToSource_NotSame() {
        val id = "id"
        val name = "sources"
        val value = SourceResponse(id, name)
        assertNotSame((SourceModel(id, name)), value.toSource())
    }

    @Test(expected = AssertionError::class)
    fun sourceDtoModelMapper_mapToSource_Same() {
        val id = "id"
        val name = "sources"
        val value = SourceResponse(id, name)
        assertSame((SourceModel(id, name)), value.toSource())
    }
}
