package com.example.marvelapp.presentation.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.core.domain.model.Comic
import com.example.core.usecase.GetCharacterCategoriesUseCase
import com.example.core.usecase.GetCharactersUseCase
import com.example.core.usecase.base.ResultStatus
import com.example.marvelapp.R
import com.example.marvelapp.presentation.characters.CharactersViewModel
import com.example.testing.MainCoroutineRule
import com.example.testing.model.CharactersFactoryTest
import com.example.testing.model.ComicFactoryTest
import com.example.testing.model.EventFactoryTest
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var getCharacterCategoriesUseCase: GetCharacterCategoriesUseCase

    @Mock
    private lateinit var uiStateObserver: Observer<DetailViewModel.UiState>

    private lateinit var detailViewModel: DetailViewModel

    private val character = CharactersFactoryTest().create(CharactersFactoryTest.Hero.ThreeDMan)
    private val comics = listOf(ComicFactoryTest().create(ComicFactoryTest.FakeComic.FakeComic1))
    private val events = listOf(EventFactoryTest().create(EventFactoryTest.FakeEvent.FakeEvent1))

    @Before
    fun setUp() {
        getCharacterCategoriesUseCase = mock()
        uiStateObserver = mock()
        detailViewModel = DetailViewModel(getCharacterCategoriesUseCase)
        detailViewModel.uiState.observeForever(uiStateObserver)
    }

    @Test
    fun `should notify uiState with Success from UiState when get character categories returns success`() =
        runTest {

            //arrange
            whenever(getCharacterCategoriesUseCase.invoke(any()))
                .thenReturn(
                    flowOf(
                        ResultStatus.Success(comics to events)
                    )
                )
            //act
            detailViewModel.getCharacterCategories(character.id)
            //assert

            verify(uiStateObserver).onChanged(isA<DetailViewModel.UiState.Success>())

            val uiStateSuccess = detailViewModel.uiState.value as DetailViewModel.UiState.Success
            val categoriesParentList = uiStateSuccess.detailParentList

            assertEquals(2, categoriesParentList.size)
            assertEquals(
                R.string.details_comics_category,
                categoriesParentList[0].categoryStringResId
            )
            assertEquals(
                R.string.details_events_category,
                categoriesParentList[1].categoryStringResId
            )
        }

    @Test
    fun `should notify uiState with Success from UiState when get character categories returns only comics`() =
        runTest {

            //arrange
            whenever(getCharacterCategoriesUseCase.invoke(any()))
                .thenReturn(
                    flowOf(
                        ResultStatus.Success(comics to emptyList())
                    )
                )
            //act
            detailViewModel.getCharacterCategories(character.id)
            //assert

            verify(uiStateObserver).onChanged(isA<DetailViewModel.UiState.Success>())

            val uiStateSuccess = detailViewModel.uiState.value as DetailViewModel.UiState.Success
            val categoriesParentList = uiStateSuccess.detailParentList

            assertEquals(1, categoriesParentList.size)
            assertEquals(
                R.string.details_comics_category,
                categoriesParentList[0].categoryStringResId
            )
        }

    @Test
    fun `should notify uiState with Success from UiState when get character categories returns only events`() =
        runTest {

            //arrange
            whenever(getCharacterCategoriesUseCase.invoke(any()))
                .thenReturn(
                    flowOf(
                        ResultStatus.Success(emptyList<Comic>() to events)
                    )
                )
            //act
            detailViewModel.getCharacterCategories(character.id)
            //assert

            verify(uiStateObserver).onChanged(isA<DetailViewModel.UiState.Success>())

            val uiStateSuccess = detailViewModel.uiState.value as DetailViewModel.UiState.Success
            val categoriesParentList = uiStateSuccess.detailParentList

            assertEquals(1, categoriesParentList.size)
            assertEquals(
                R.string.details_events_category,
                categoriesParentList[0].categoryStringResId
            )
        }

    @Test
    fun `should notify uiState with Empty from UiState when get character categories returns an empty result list`() =
        runTest {

            //arrange
            whenever(getCharacterCategoriesUseCase.invoke(any()))
                .thenReturn(
                    flowOf(
                        ResultStatus.Success(emptyList<Comic>() to emptyList())
                    )
                )
            //act
            detailViewModel.getCharacterCategories(character.id)
            //assert

            verify(uiStateObserver).onChanged(isA<DetailViewModel.UiState.Empty>())
        }

    @Test
    fun `should notify uiState with Error from UiState when get character categories returns an exception`() =
        runTest {

            //arrange
            whenever(getCharacterCategoriesUseCase.invoke(any()))
                .thenReturn(
                    flowOf(
                        ResultStatus.Error(Throwable())
                    )
                )
            //act
            detailViewModel.getCharacterCategories(character.id)
            //assert

            verify(uiStateObserver).onChanged(isA<DetailViewModel.UiState.Error>())
        }

}