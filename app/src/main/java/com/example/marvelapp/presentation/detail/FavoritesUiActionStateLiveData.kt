package com.example.marvelapp.presentation.detail

import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.core.usecase.AddFavoriteUseCase
import com.example.core.usecase.CheckFavoriteUseCase
import com.example.core.usecase.DeleteFavoriteUseCase
import com.example.marvelapp.R
import com.example.marvelapp.presentation.extensions.watchStatus
import kotlin.coroutines.CoroutineContext

class FavoritesUiActionStateLiveData(
    private val coroutineContext: CoroutineContext,
    private val checkFavoriteUseCase: CheckFavoriteUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase
) {

    @set:VisibleForTesting
    var favoriteCharacter = false

    private val action = MutableLiveData<Action>()
    val state: LiveData<UiState> = action.switchMap {
        liveData(coroutineContext) {
            when (it) {
                is Action.CheckFavorite -> {
                    checkFavoriteUseCase.invoke(
                        CheckFavoriteUseCase.Params(it.characterId)
                    ).watchStatus(
                        success = { isFavorite ->
                            favoriteCharacter = isFavorite
                            emitFavoriteIcon()
                        },
                        successUpdateChildList = {},
                        error = {}
                    )

                }
                is Action.AddFavorite -> {
                    it.detailViewArg.run {
                        addFavoriteUseCase.invoke(
                            AddFavoriteUseCase.Params(characterId, name, imageUrl)
                        ).watchStatus(
                            loading = {
                                emit(UiState.Loading)
                            },
                            success = {
                                favoriteCharacter = true
                                emitFavoriteIcon()
                            },
                            successUpdateChildList = {},
                            error = {
                                emit(UiState.Error(R.string.error_add_favorite))
                            }
                        )
                    }
                }
                is Action.DeleteFavorite -> {
                    it.detailViewArg.run {
                        deleteFavoriteUseCase.invoke(
                            DeleteFavoriteUseCase.Params(characterId, name, imageUrl)
                        ).watchStatus(
                            loading = {
                                emit(UiState.Loading)
                            },
                            success = {
                                favoriteCharacter = false
                                emitFavoriteIcon()
                            },
                            successUpdateChildList = {},
                            error = {
                                emit(UiState.Error(R.string.error_delete_favorite))
                            }
                        )
                    }
                }
            }
        }
    }

    private suspend fun LiveDataScope<UiState>.emitFavoriteIcon() {
        emit(UiState.Icon(favoriteCharacter))
    }

    fun checkFavorite(characterId: Int) {
        action.value = Action.CheckFavorite(characterId)
    }

    fun update(detailViewArg: DetailViewArg) {
        action.value = if (!favoriteCharacter){
            Action.AddFavorite(detailViewArg)
        } else Action.DeleteFavorite(detailViewArg)
    }


    sealed class UiState {
        data object Loading : UiState()
        data class Icon(val isFavorite: Boolean) : UiState()
        data class Error(@StringRes val messageResId: Int) : UiState()
    }

    sealed class Action {
        data class CheckFavorite(val characterId: Int) : Action()
        data class AddFavorite(val detailViewArg: DetailViewArg) : Action()
        data class DeleteFavorite(val detailViewArg: DetailViewArg) : Action()
    }
}