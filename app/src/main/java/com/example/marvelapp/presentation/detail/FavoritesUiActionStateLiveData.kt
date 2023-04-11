package com.example.marvelapp.presentation.detail

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.core.usecase.AddFavoriteUseCase
import com.example.core.usecase.CheckFavoriteUseCase
import com.example.marvelapp.R
import com.example.marvelapp.presentation.extensions.watchStatus
import kotlin.coroutines.CoroutineContext

class FavoritesUiActionStateLiveData(
    private val coroutineContext: CoroutineContext,
    private val checkFavoriteUseCase: CheckFavoriteUseCase,
    //private val context: Context,
    private val addFavoriteUseCase: AddFavoriteUseCase
) {

    private val action = MutableLiveData<Action>()
    val state: LiveData<UiState> = action.switchMap {
        liveData(coroutineContext) {
            when (it) {
                is Action.CheckFavorite -> {
                    checkFavoriteUseCase.invoke(
                        CheckFavoriteUseCase.Params(it.characterId)
                    ).watchStatus(
                        success = { isFavorite ->
                            var icon = R.drawable.ic_favorite_unchecked
                            if (isFavorite) {
                                icon = R.drawable.ic_favorite_checked
//                                val colorStateList = ContextCompat.getColorStateList(
//                                    context,
//                                    R.color.red_400
//                                )
//                                icon = colorStateList?.defaultColor ?: 0
                            }
                            emit(UiState.Icon(icon))
                        },
                        error = {}

                    )

                }
                is Action.Update -> {
                    it.detailViewArg.run {
                        addFavoriteUseCase.invoke(
                            AddFavoriteUseCase.Params(characterId, name, imageUrl)
                        ).watchStatus(
                            loading = {
                                emit(UiState.Loading)
                            },
                            success = {
                                emit(UiState.Icon(R.drawable.ic_favorite_checked))
                            },
                            error = {
                                emit(UiState.Error(R.string.error_add_favorite))
                            }
                        )
                    }
                }
            }
        }
    }

    fun checkFavorite(characterId: Int) {
        action.value = Action.CheckFavorite(characterId)
    }

    fun update(detailViewArg: DetailViewArg) {
        action.value = Action.Update(detailViewArg)
    }


    sealed class UiState {
        object Loading : UiState()
        data class Icon(@DrawableRes val icon: Int) : UiState()
        data class Error(@StringRes val messageResId: Int) : UiState()
    }

    sealed class Action {
        data class CheckFavorite(val characterId: Int) : Action()
        data class Update(val detailViewArg: DetailViewArg) : Action()
    }
}