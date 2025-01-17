package com.example.marvelapp.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.core.usecase.GetCharacterCategoriesUseCase
import com.example.marvelapp.presentation.extensions.watchStatus
import kotlin.coroutines.CoroutineContext

class CharactersUiActionStateLiveData(
    private val coroutineContext: CoroutineContext,
    private val getCharacterCategoriesUseCase: GetCharacterCategoriesUseCase,
) {

    private val action = MutableLiveData<Action>()
    val state: LiveData<UiState> = action
        .switchMap {
            liveData(coroutineContext) {
                when (it) {
                    is Action.Load -> {
                        actionLoadAndRetry(it.characterId, it.offset)
                    }

                    is Action.Retry -> {
                        actionLoadAndRetry(it.characterId, it.offset)
                    }

                    is Action.LoadMoreCategories -> {
                        actionLoadAndRetry(it.characterId, it.offset)
                    }
                }
            }
        }

    @Suppress("LongMethod")
    private suspend fun LiveDataScope<UiState>.actionLoadAndRetry(characterId: Int, offset: Int) {

        getCharacterCategoriesUseCase.invoke(
            GetCharacterCategoriesUseCase.GetCategoriesParams(characterId, offset)
        ).watchStatus(
            loading = {
                if (offset == DEFAULT_OFFSET) {
                    emit(UiState.Loading)
                }
            },
            success = { data ->
                val detailParentList = arrayListOf<DetailParentVE>()

                val comics = data.comics
                if (comics.isNotEmpty()) {
                    val detailChildList = comics.map {
                        DetailChildVE(it.id, it.titleCategory, it.imageUrl, it.titleParentRes)
                    }.toMutableList()

                    detailParentList.add(
                        DetailParentVE(detailChildList.firstOrNull()?.titleParentRes, detailChildList)
                    )
                }

                val events = data.events
                if (events.isNotEmpty()) {
                    val detailChildList = events.map {
                        DetailChildVE(it.id, it.titleCategory, it.imageUrl, it.titleParentRes)
                    }.toMutableList()

                    detailParentList.add(
                        DetailParentVE(detailChildList.firstOrNull()?.titleParentRes, detailChildList)
                    )
                }

                val series = data.series
                if (series.isNotEmpty()) {
                    val detailChildList = series.map {
                        DetailChildVE(it.id, it.titleCategory, it.imageUrl, it.titleParentRes)
                    }.toMutableList()

                    detailParentList.add(
                        DetailParentVE(detailChildList.firstOrNull()?.titleParentRes, detailChildList)
                    )
                }

                if (detailParentList.isNotEmpty()) {
                    emit(UiState.Success(detailParentList))
                } else emit(UiState.Empty)
            },
            successUpdateChildList = { data ->
                val detailParentList = mutableListOf<DetailParentVE>()

                val comics = data.comics
                if (comics.isNotEmpty()) {
                    val detailChildList = comics.map {
                        DetailChildVE(it.id, it.titleCategory, it.imageUrl, it.titleParentRes)
                    }.toMutableList()

                    detailParentList.add(
                        DetailParentVE(detailChildList.firstOrNull()?.titleParentRes, detailChildList)
                    )
                }

                val events = data.events
                if (events.isNotEmpty()) {
                    val detailChildList = events.map {
                        DetailChildVE(it.id, it.titleCategory, it.imageUrl, it.titleParentRes)
                    }.toMutableList()

                    detailParentList.add(
                        DetailParentVE(detailChildList.firstOrNull()?.titleParentRes, detailChildList)
                    )
                }

                val series = data.series
                if (series.isNotEmpty()) {
                    val detailChildList = series.map {
                        DetailChildVE(it.id, it.titleCategory, it.imageUrl, it.titleParentRes)
                    }.toMutableList()

                    detailParentList.add(
                        DetailParentVE(detailChildList.firstOrNull()?.titleParentRes, detailChildList)
                    )
                }
                emit(UiState.SuccessUpdateChildList(detailParentList))
            },
            error = {
                emit(UiState.Error)
            }
        )
    }

    fun load(characterId: Int, offset: Int) {
        action.value = Action.Load(characterId, offset)
    }

    fun retry(characterId: Int, offset: Int) {
        action.value = Action.Retry(characterId, offset)
    }

    fun loadMoreCategories(characterId: Int, offset: Int) {
        action.value = Action.LoadMoreCategories(characterId, offset)
    }

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val detailParentList: MutableList<DetailParentVE>) : UiState()
        data class SuccessUpdateChildList(val detailParentList: MutableList<DetailParentVE>) : UiState()
        data object Error : UiState()
        data object Empty : UiState()
    }

    sealed class Action {
        data class Load(val characterId: Int, val offset: Int) : Action()
        data class Retry(val characterId: Int, val offset: Int) : Action()
        data class LoadMoreCategories(val characterId: Int, val offset: Int) : Action()
    }

    companion object {
        private const val DEFAULT_OFFSET = 0
    }
}