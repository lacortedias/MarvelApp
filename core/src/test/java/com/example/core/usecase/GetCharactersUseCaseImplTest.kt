package com.example.core.usecase

import androidx.paging.PagingConfig
import com.example.core.data.repository.CharactersRepository
import com.example.core.usecase.GetCharactersUseCase
import com.example.core.usecase.GetCharactersUseCaseImpl
import com.example.testing.MainCoroutineRule
import com.example.testing.model.CharactersFactoryTest
import com.example.testing.pagingsource.PagingSourceFactoryTest
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class GetCharactersUseCaseImplTest{

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    lateinit var repository: CharactersRepository

    private lateinit var getCharactersUseCase: GetCharactersUseCase

    private val hero = CharactersFactoryTest().create(CharactersFactoryTest.Hero.ThreeDMan)

    private val fakePagingSource = PagingSourceFactoryTest().create(listOf(hero))

    @Before
    fun setUp(){
        getCharactersUseCase = GetCharactersUseCaseImpl(repository)
    }


    @Test
    fun `should validate flow paging data creation when invoke from use case is called`() =
        runTest{
            whenever(repository.getCharacters(""))
                .thenReturn(fakePagingSource)

            val result = getCharactersUseCase
                .invoke(GetCharactersUseCase.GetCharactersParams("", PagingConfig(20)))

            verify(repository).getCharacters("")

            assertNotNull(result.first())
    }
}