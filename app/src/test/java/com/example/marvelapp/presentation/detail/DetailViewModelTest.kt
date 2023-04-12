package com.example.marvelapp.presentation.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.core.domain.model.Comic
import com.example.core.usecase.AddFavoriteUseCase
import com.example.core.usecase.CheckFavoriteUseCase
import com.example.core.usecase.DeleteFavoriteUseCase
import com.example.core.usecase.GetCharacterCategoriesUseCase
import com.example.core.usecase.base.ResultStatus
import com.example.marvelapp.R
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
    private lateinit var addFavoriteUseCase: AddFavoriteUseCase

    @Mock
    private lateinit var checkFavoriteUseCase: CheckFavoriteUseCase

    @Mock
    private lateinit var deleteFavoriteUseCase: DeleteFavoriteUseCase

    @Mock
    private lateinit var characterUiStateObserver: Observer<CharactersUiActionStateLiveData.UiState>

    @Mock
    private lateinit var favoriteUiStateObserver: Observer<FavoritesUiActionStateLiveData.UiState>

    private lateinit var detailViewModel: DetailViewModel

    private val character = CharactersFactoryTest().create(CharactersFactoryTest.Hero.ThreeDMan)
    private val comics = listOf(ComicFactoryTest().create(ComicFactoryTest.FakeComic.FakeComic1))
    private val events = listOf(EventFactoryTest().create(EventFactoryTest.FakeEvent.FakeEvent1))

    @Before
    fun setUp() {
        getCharacterCategoriesUseCase = mock()
        addFavoriteUseCase = mock()
        characterUiStateObserver = mock()
        detailViewModel = DetailViewModel(
            getCharacterCategoriesUseCase,
            checkFavoriteUseCase,
            addFavoriteUseCase,
            deleteFavoriteUseCase,
            mainCoroutineRule.testDispatcherProvider
        ).apply {
            charactersCategories.state.observeForever(characterUiStateObserver)
            favoritesCategories.state.observeForever(favoriteUiStateObserver)
        }
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
            detailViewModel.charactersCategories.load(character.id)
            //assert

            verify(characterUiStateObserver).onChanged(isA<CharactersUiActionStateLiveData.UiState.Success>())

            val uiStateSuccess =
                detailViewModel.charactersCategories.state.value as CharactersUiActionStateLiveData.UiState.Success
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
            detailViewModel.charactersCategories.load(character.id)
            //assert

            verify(characterUiStateObserver).onChanged(isA<CharactersUiActionStateLiveData.UiState.Success>())

            val uiStateSuccess =
                detailViewModel.charactersCategories.state.value as CharactersUiActionStateLiveData.UiState.Success
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
            detailViewModel.charactersCategories.load(character.id)
            //assert

            verify(characterUiStateObserver).onChanged(isA<CharactersUiActionStateLiveData.UiState.Success>())

            val uiStateSuccess =
                detailViewModel.charactersCategories.state.value as CharactersUiActionStateLiveData.UiState.Success
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
            detailViewModel.charactersCategories.load(character.id)
            //assert

            verify(characterUiStateObserver).onChanged(isA<CharactersUiActionStateLiveData.UiState.Empty>())
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
            detailViewModel.charactersCategories.load(character.id)
            //assert

            verify(characterUiStateObserver).onChanged(isA<CharactersUiActionStateLiveData.UiState.Error>())
        }

    @Test
    fun `should notify favorite_uiState with filled favorite icon when check favorite returns true`() =
        runTest {
            //arrange
            whenever(checkFavoriteUseCase.invoke(any()))
                .thenReturn(
                    flowOf(
                        ResultStatus.Success(true)
                    )
                )

            // Act
            detailViewModel.favoritesCategories.checkFavorite(character.id)

            // Assert
            verify(favoriteUiStateObserver).onChanged(
                isA<FavoritesUiActionStateLiveData.UiState.Icon>()
            )
            val uiState =
                detailViewModel.favoritesCategories.state.value as FavoritesUiActionStateLiveData.UiState.Icon
            assertEquals(R.drawable.ic_favorite_checked, uiState.icon)
        }

    @Test
    fun `should notify favorite_uiState with not filled favorite icon when check favorite returns false`() =
        runTest {
            //arrange
            whenever(checkFavoriteUseCase.invoke(any()))
                .thenReturn(
                    flowOf(
                        ResultStatus.Success(false)
                    )
                )

            // Act
            detailViewModel.favoritesCategories.checkFavorite(character.id)

            // Assert
            verify(favoriteUiStateObserver).onChanged(
                isA<FavoritesUiActionStateLiveData.UiState.Icon>()
            )
            val uiState =
                detailViewModel.favoritesCategories.state.value as FavoritesUiActionStateLiveData.UiState.Icon
            assertEquals(R.drawable.ic_favorite_unchecked, uiState.icon)
        }

    @Test
    fun `should notify favorite_uiState with filled favorite icon when current icon is unchecked`() =
        runTest {
            // Arrange
            whenever(addFavoriteUseCase.invoke(any()))
                .thenReturn(
                    flowOf(
                        ResultStatus.Success(Unit)
                    )
                )

            // Act
            detailViewModel.run {
                favoritesCategories.currentFavoriteIcon = R.drawable.ic_favorite_unchecked
                favoritesCategories.update(
                    DetailViewArg(character.id, character.name, character.imageUrl)
                )
            }

            // Assert
            verify(favoriteUiStateObserver).onChanged(isA<FavoritesUiActionStateLiveData.UiState.Icon>())
            val uiState =
                detailViewModel.favoritesCategories.state.value as FavoritesUiActionStateLiveData.UiState.Icon
            assertEquals(R.drawable.ic_favorite_checked, uiState.icon)
        }

    @Test
    fun `should call remove and notify favorite_uiState with filled favorite icon when current icon is checked`() =
        runTest {
            // Arrange
            whenever(deleteFavoriteUseCase.invoke(any()))
                .thenReturn(
                    flowOf(
                        ResultStatus.Success(Unit)
                    )
                )

            // Act
            detailViewModel.run {
                favoritesCategories.currentFavoriteIcon = R.drawable.ic_favorite_checked
                favoritesCategories.update(
                    DetailViewArg(character.id, character.name, character.imageUrl)
                )
            }

            // Assert
            verify(favoriteUiStateObserver).onChanged(isA<FavoritesUiActionStateLiveData.UiState.Icon>())
            val uiState =
                detailViewModel.favoritesCategories.state.value as FavoritesUiActionStateLiveData.UiState.Icon
            assertEquals(R.drawable.ic_favorite_unchecked, uiState.icon)
        }

}