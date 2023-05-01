package com.example.marvelapp.presentation.characters

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.example.core.usecase.GetCharactersUseCase
import com.example.testing.MainCoroutineRule
import com.example.testing.model.CharactersFactoryTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CharactersViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
//
//    @get:Rule
//    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    lateinit var getCharacterUseCase: GetCharactersUseCase

    private lateinit var charactersViewModel: CharactersViewModel

    private val charactersFactoryTest = CharactersFactoryTest()

    private val pagingDataCharacters = PagingData.from(
        listOf(
            charactersFactoryTest.create(CharactersFactoryTest.Hero.ThreeDMan),
            charactersFactoryTest.create(CharactersFactoryTest.Hero.ABomb)
        )
    )

    @Before
    fun setUp() {

        getCharacterUseCase = mock()
        charactersViewModel = CharactersViewModel(
            getCharacterUseCase,
            mainCoroutineRule.testDispatcherProvider
        )
    }

    @Test
    fun `should validate the paging data object values when calling charactersPagingData`() =
        runTest {
            whenever(
                getCharacterUseCase.invoke(any())
            ).thenReturn(
                flowOf(
                    pagingDataCharacters
                )
            )

            val result = charactersViewModel.charactersPagingData("")
            assertNotNull(result.first())
        }

    @Test(expected = RuntimeException::class)
    fun `should throw an exception when the calling to the use case returns an exception`() =
        runTest {
            whenever(
                getCharacterUseCase.invoke(any())
            ).thenThrow(java.lang.RuntimeException())

            charactersViewModel.charactersPagingData("")
        }
}