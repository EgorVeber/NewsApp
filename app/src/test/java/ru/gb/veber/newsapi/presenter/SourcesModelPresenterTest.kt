package ru.gb.veber.newsapi.presenter

import com.github.terrakok.cicerone.Router
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verifySequence
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.gb.veber.newsapi.data.database.entity.HistorySelectEntity
import ru.gb.veber.newsapi.data.mapper.toHistorySelectEntity
import ru.gb.veber.newsapi.domain.models.HistorySelectModel
import ru.gb.veber.newsapi.domain.repository.AccountSourcesRepo
import ru.gb.veber.newsapi.domain.repository.ArticleRepo
import ru.gb.veber.newsapi.domain.repository.HistorySelectRepo
import ru.gb.veber.newsapi.domain.repository.SourcesRepo
import ru.gb.veber.newsapi.presentation.sources.SourcesViewModel

class SourcesModelPresenterTest {

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

    @InjectMockKs
    private lateinit var sourcesViewModel: SourcesViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        sourcesViewModel.subscribe(5)
    }

    @Test
    fun `WHEN sourcesPresenter openAllNews EXPECTED router navigateTo and historySelectRepoImpl insertSelect`() {
       // every { historySelectRepoImplMockk.insertSelect(any()) } returns Completable.complete()
        every { routerMockk.navigateTo(any()) } just runs

        sourcesViewModel.openAllNews("", "")
       // verifySequence { historySelectRepoImplMockk.insertSelect(any()) }
        verifySequence { (routerMockk.navigateTo(any())) }

    }

    @Test
    fun `WHEN sourcesPresenter openAllNews args source and name EXPECTED same args source and name`() {
        val expectedSource = "expectedSource"
        val expectedName = "expectedName"

        val slot = slot<HistorySelectEntity>()

    //    every { historySelectRepoImplMockk.insertSelect(capture(slot)) } returns Completable.complete()

        sourcesViewModel.openAllNews(expectedSource, expectedName)

        val expectedHistory = HistorySelectModel(0,5,sourcesId = expectedSource, sourcesName = expectedName)

        assertEquals(expectedHistory.toHistorySelectEntity(), slot.captured)
    }

    @After
    fun clear() {
        unmockkAll()
    }
}
