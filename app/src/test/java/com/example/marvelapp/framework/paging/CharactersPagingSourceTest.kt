package com.example.marvelapp.framework.paging

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator.MediatorResult
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.core.data.repository.CharactersRemoteDataSource
import com.example.core.domain.model.Character
import com.example.marvelapp.factory.response.CharacterPagingFactory
import com.example.marvelapp.framework.CharactersRepositoryImplTest
import com.example.marvelapp.framework.db.AppDatabase
import com.example.marvelapp.framework.db.entity.CharacterEntity
import com.example.testing.MainCoroutineRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CharactersPagingSourceTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var charactersRemoteMediator: CharactersRemoteMediator

    @Mock
    lateinit var charactersRemoteDataSource: CharactersRemoteDataSource

    lateinit var inMemoryDatabase: AppDatabase

    private lateinit var charactersRepositoryImplTest: CharactersRepositoryImplTest

    private val characterPagingFactoryTest = CharacterPagingFactory()

    @Before
    fun setUp() {
        charactersRemoteDataSource = mock()

        val context = ApplicationProvider.getApplicationContext<Context>()
        inMemoryDatabase = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()

        charactersRepositoryImplTest = CharactersRepositoryImplTest()

        charactersRemoteMediator = CharactersRemoteMediator("", "", inMemoryDatabase, charactersRemoteDataSource)
    }

    @After
    fun tearDown() {
        inMemoryDatabase.close()
    }

    @Test
    fun `should return a success load result when load is called`() = runTest {
        //arrange
        whenever(charactersRemoteDataSource.fetchCharacters(any())).thenReturn(characterPagingFactoryTest.create())

        val pagingState = PagingState<Int, CharacterEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        //act
        charactersRemoteMediator.load(
            loadType = LoadType.REFRESH,
            state = pagingState
        )
        charactersRemoteMediator.load(
            loadType = LoadType.APPEND,
            state = pagingState
        )

        val result = charactersRepositoryImplTest.create()
            .map {
                Character(
                    it.id,
                    it.name,
                    it.imageUrl
                )
            }

        val expected = characterPagingFactoryTest.create()

        //assert
        assertEquals(expected.characters, result)

    }

    @Test
    fun `should return a error load result when load is called`() =
        runTest {

            val exception = RuntimeException()
            whenever(charactersRemoteDataSource.fetchCharacters(any()))
                .thenThrow(exception)

            val pagingState = PagingState<Int, CharacterEntity>(
                pages = listOf(),
                anchorPosition = null,
                config = PagingConfig(pageSize = 0),
                leadingPlaceholderCount = 0
            )

            val result = charactersRemoteMediator.load(
                loadType = LoadType.REFRESH,
                state = pagingState
            )

            assertTrue(result is MediatorResult.Error)

            val errorResult = result as MediatorResult.Error
            assertEquals(exception::class, errorResult.throwable::class)
            assertEquals(exception.message, errorResult.throwable.message)

        }
}