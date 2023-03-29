package com.example.core.usecase

import com.example.core.data.repository.CharactersRepository
import com.example.core.usecase.base.ResultStatus
import com.example.testing.MainCoroutineRule
import com.example.testing.model.CharactersFactoryTest
import com.example.testing.model.ComicFactoryTest
import com.example.testing.model.EventFactoryTest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
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
class GetCharacterCategoriesUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var getCharacterCategoriesUseCase: GetCharacterCategoriesUseCase

    @Mock
    lateinit var charactersRepository: CharactersRepository

    private val character = CharactersFactoryTest().create(CharactersFactoryTest.Hero.ThreeDMan)
    private val comics = listOf(ComicFactoryTest().create(ComicFactoryTest.FakeComic.FakeComic1))
    private val events = listOf(EventFactoryTest().create(EventFactoryTest.FakeEvent.FakeEvent1))


    @Before
    fun setUp() {
        charactersRepository = mock()
        getCharacterCategoriesUseCase = GetCharacterCategoriesUseCaseImpl(
            charactersRepository,
            mainCoroutineRule.testDispatcherProvider
        )
    }

    @Test
    fun `should return Success from ResultStatus when get both requests return success`() =
        runTest {

            //arrange
            whenever(charactersRepository.getComics(character.id)).thenReturn(comics)
            whenever(charactersRepository.getEvents(character.id)).thenReturn(events)

            //act
            val result = getCharacterCategoriesUseCase.invoke(
                GetCharacterCategoriesUseCase.GetCategoriesParams(characterId = character.id)
            )

            //assert
            val resultStatus = result.toList()
            assertEquals(ResultStatus.Loading, resultStatus[0])
            assertEquals(ResultStatus.Success(comics to events), resultStatus[1])
            assertTrue(resultStatus[1] is ResultStatus.Success)
        }

    @Test
    fun `should return Error from ResultStatus when get events request returns error`() =
        runTest {

            //arrange
            whenever(charactersRepository.getComics(character.id)).thenAnswer { throw Throwable() }

            //act
            val result = getCharacterCategoriesUseCase.invoke(
                GetCharacterCategoriesUseCase.GetCategoriesParams(characterId = character.id)
            )

            //assert
            val resultStatus = result.toList()
            assertEquals(ResultStatus.Loading, resultStatus[0])
            assertTrue(resultStatus[1] is ResultStatus.Error)
        }

    @Test
    fun `should return Error from ResultStatus when get comics request returns error`() =
        runTest {

            //arrange
            whenever(charactersRepository.getComics(character.id)).thenReturn(comics)
            whenever(charactersRepository.getEvents(character.id)).thenAnswer { throw Throwable() }

            //act
            val result = getCharacterCategoriesUseCase.invoke(
                GetCharacterCategoriesUseCase.GetCategoriesParams(characterId = character.id)
            )

            //assert
            val resultStatus = result.toList()
            assertEquals(ResultStatus.Loading, resultStatus[0])
            assertTrue(resultStatus[1] is ResultStatus.Error)
        }
}