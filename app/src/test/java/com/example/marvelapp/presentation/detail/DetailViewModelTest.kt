package com.example.marvelapp.presentation.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.core.usecase.GetCharacterCategoriesUseCase
import com.example.core.usecase.GetCharactersUseCase
import com.example.marvelapp.presentation.characters.CharactersViewModel
import com.example.testing.MainCoroutineRule
import com.example.testing.model.CharactersFactoryTest
import com.example.testing.model.ComicFactoryTest
import com.example.testing.model.EventFactoryTest
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest{

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

}