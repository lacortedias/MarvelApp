package com.example.marvelapp.presentation.characters

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.example.core.domain.model.Character
import com.example.core.usecase.GetCharactersUseCase
import com.example.marvelapp.util.TestCoroutineRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class CharactersViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    lateinit var getCharacterUseCase: GetCharactersUseCase

    private lateinit var charactersViewModel: CharactersViewModel

    private val pagingDataCharacters = PagingData.from(
        listOf(
            Character(
                "3-D man",
                "https://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784.jpg"
            ),
            Character(
                "A-Bomb (HAS)",
                "https://i.annihil.us/u/prod/marvel/i/mg/3/20/5232158de5b16.jpg"
            )
        )
    )

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {

        getCharacterUseCase = mock()
        charactersViewModel = CharactersViewModel(getCharacterUseCase)

    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should validate the paging data object values when calling charactersPagingData`() =
        runTest {
            whenever(
                getCharacterUseCase.invoke(any())
            ).thenReturn (
                flowOf(
                    pagingDataCharacters
                )
            )

            val result = charactersViewModel.charactersPagingData("")
            assertEquals(1, result.count())
        }

    @ExperimentalCoroutinesApi
    @Test(expected = RuntimeException::class)
    fun `should throw an exception when the calling to the use case returns an exception`() =
        runTest {
            whenever(getCharacterUseCase.invoke(any())
            ).thenThrow (java.lang.RuntimeException())

            charactersViewModel.charactersPagingData("")
        }

}