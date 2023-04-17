package com.example.marvelapp.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.core.usecase.GetFavoritesUseCase
import com.example.core.usecase.base.CoroutinesDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val coroutinesDispatchers: CoroutinesDispatchers
) : ViewModel() {

    private val action = MutableLiveData<Action>()

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
            }
        }
    }

    fun getAllFavorites() {
        action.value = Action.GetAllFavorites
    }

    sealed class UiState {
        data class ShowFavorite(val favorites: List<FavoritesItem>) : UiState()
        object ShowEmpty : UiState()
    }

    sealed class Action {
        object GetAllFavorites : Action()
    }
}