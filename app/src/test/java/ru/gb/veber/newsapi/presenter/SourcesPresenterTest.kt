package ru.gb.veber.newsapi.presenter

import com.github.terrakok.cicerone.Router
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.rxjava3.core.Completable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.gb.veber.newsapi.core.SearchNewsScreen
import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.model.database.entity.HistorySelectDbEntity
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepo
import ru.gb.veber.newsapi.model.repository.room.ArticleRepo
import ru.gb.veber.newsapi.model.repository.room.HistorySelectRepo
import ru.gb.veber.newsapi.model.repository.room.SourcesRepo
import ru.gb.veber.newsapi.utils.mapToHistorySelectDbEntity

class SourcesPresenterTest {

    @RelaxedMockK
    lateinit var routerMockk: Router

    @RelaxedMockK
    lateinit var accountSourcesRepoImplMockk: AccountSourcesRepo

    @RelaxedMockK
    lateinit var sourcesRepoImplMockk: SourcesRepo

    @RelaxedMockK
    lateinit var articleRepoImplMockk: ArticleRepo

    @RelaxedMockK
    lateinit var historySelectRepoImplMockk: HistorySelectRepo

    private lateinit var sourcesPresenter: SourcesPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        sourcesPresenter = SourcesPresenter(5)
        sourcesPresenter.apply {
            router = routerMockk
            accountSourcesRepoImpl = accountSourcesRepoImplMockk
            sourcesRepoImpl = sourcesRepoImplMockk
            articleRepoImpl = articleRepoImplMockk
            historySelectRepoImpl = historySelectRepoImplMockk
        }
    }


    @Test
    fun `WHEN sourcesPresenter openAllNews EXPECTED router navigateTo and historySelectRepoImpl insertSelect`() {
        every { historySelectRepoImplMockk.insertSelect(any()) } returns Completable.complete()
        sourcesPresenter.openAllNews("", "")
        verifySequence { historySelectRepoImplMockk.insertSelect(any()) }

        verify {
            listOf(accountSourcesRepoImplMockk,
                articleRepoImplMockk,
                articleRepoImplMockk) wasNot Called
        }

        verifySequence { (routerMockk.navigateTo(any())) }
    }

    @Test
    fun `WHEN sourcesPresenter openAllNews args source and name EXPECTED same args source and name`() {
        val expectedSource = "expectedSource"
        val expectedName = "expectedName"

        val slot = slot<HistorySelectDbEntity>()

        every { historySelectRepoImplMockk.insertSelect(capture(slot)) } returns Completable.complete()

        sourcesPresenter.openAllNews(expectedSource, expectedName)

        val expectedHistory = HistorySelect(0,5,sourcesId = expectedSource, sourcesName = expectedName)

        assertEquals(mapToHistorySelectDbEntity(expectedHistory),slot.captured)
    }

    @After
    fun clear() {
        unmockkAll()
    }
}