package com.example.marvelapp.framework.paging

import androidx.paging.PagingSource
import com.example.core.data.repository.CharactersRemoteDataSource
import com.example.core.domain.model.Character
import com.example.marvelapp.factory.response.DataWrapperResponseFactoryTest
import com.example.marvelapp.framework.network.response.DataWrapperResponse
import com.example.testing.MainCoroutineRule
import com.example.testing.model.CharactersFactoryTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CharactersPagingSourceTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var charactersPagingSource: CharactersPagingSource

    @Mock
    lateinit var charactersRemoteDataSource: CharactersRemoteDataSource<DataWrapperResponse>

    private val dataWrapperResponseFactoryTest = DataWrapperResponseFactoryTest()

    private val charactersFactoryTest = CharactersFactoryTest()

    @Before
    fun setUp() {
        charactersPagingSource = CharactersPagingSource(charactersRemoteDataSource, "")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return a success load result when load is called`() =
        runTest {
            whenever(charactersRemoteDataSource.fetchCharacters(any()))
                .thenReturn(dataWrapperResponseFactoryTest.create())

            val result = charactersPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )

            val expected = listOf(
                charactersFactoryTest.create(CharactersFactoryTest.Hero.ThreeDMan),
                charactersFactoryTest.create(CharactersFactoryTest.Hero.ABomb)
            )

            assertEquals(
                PagingSource.LoadResult.Page(
                    data = expected,
                    prevKey = null,
                    nextKey = 20
                ),
                result
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return a error load result when load is called`() =
        runTest {

            val exception = RuntimeException()
            whenever(charactersRemoteDataSource.fetchCharacters(any()))
                .thenThrow(exception)


            val result = charactersPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )

            assertEquals(
                PagingSource.LoadResult.Error<Int, Character>(exception),
                result
            )
        }
}