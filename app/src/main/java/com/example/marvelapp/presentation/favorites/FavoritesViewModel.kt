package com.example.marvelapp.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.core.usecase.GetFavoritesUseCase
import com.example.core.usecase.SearchFavoritesUseCase
import com.example.core.usecase.base.CoroutinesDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val searchFavoritesUseCase: SearchFavoritesUseCase,
    private val coroutinesDispatchers: CoroutinesDispatchers
) : ViewModel() {

    var currentSearchQuery = ""

    private val action = MutableLiveData<Action>()
    private val actionSearch = MutableLiveData<ActionSearch>()

    val stateSearch: LiveData<UiStateSearch> = actionSearch
        .switchMap {
            liveData(coroutinesDispatchers.main()){
                when (it){
                    is ActionSearch.Search -> {
                        searchFavoritesUseCase.invoke(
                            SearchFavoritesUseCase.SearchFavoritesParams(currentSearchQuery)
                        ).catch {
                            emit(UiStateSearch.SearchResult(emptyList()))
                        }.collect{
                            val items = it.map { character ->
                                FavoritesItem(
                                    character.id,
                                    character.name,
                                    character.imageUrl
                                )
                            }
                            emit(UiStateSearch.SearchResult(items))
                        }

                    }
                }
            }
        }

    val state: LiveData<UiState> = action
        .distinctUntilChanged()
        .switchMap {
            liveData(coroutinesDispatchers.main()) {
                when (it) {
                    is Action.GetAllFavorites -> {
                        getFavoritesUseCase.invoke()
                            .catch {
                                emit(UiState.ShowEmpty)
                            }
                            .collect {
                                val items = it.map { character ->
                                    FavoritesItem(
                                        character.id,
                                        character.name,
                                        character.imageUrl
                                    )
                                }
                                val uiState = if (items.isNullOrEmpty()) {
                                    UiState.ShowEmpty
                                } else UiState.ShowFavorite(items)

                                emit(uiState)
                            }
                    }
                    else -> {
                        getFavoritesUseCase.invoke()
                        .catch {
                            emit(UiState.ShowEmpty)
                        }
                        .collect {
                            val items = it.map { character ->
                                FavoritesItem(
                                    character.id,
                                    character.name,
                                    character.imageUrl
                                )
                            }
                            val uiState = if (items.isNullOrEmpty()) {
                                UiState.ShowEmpty
                            } else UiState.ShowFavorite(items)

                            emit(uiState)
                        }
                    }
                }
            }
        }

    fun getAllFavorites() {
        action.value = Action.GetAllFavorites
    }

    fun searchCharacters() {
        actionSearch.value = ActionSearch.Search
    }

    fun closeSearch() {
        if (currentSearchQuery.isNotEmpty()) {
            currentSearchQuery = ""
        }
    }

    sealed class UiState {
        data class ShowFavorite(val favorites: List<FavoritesItem>) : UiState()
        object ShowEmpty : UiState()
    }

    sealed class UiStateSearch {
        data class SearchResult(val charactersFavorites: List<FavoritesItem>) : UiStateSearch()
    }

    sealed class Action {
        object GetAllFavorites : Action()
    }

    sealed class ActionSearch {
        object Search : ActionSearch()
    }
}