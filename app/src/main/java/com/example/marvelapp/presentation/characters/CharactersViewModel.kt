package com.example.marvelapp.presentation.characters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.core.domain.model.Character
import com.example.core.usecase.GetCharactersUseCase
import com.example.core.usecase.base.CoroutinesDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
    coroutinesDispatchers: CoroutinesDispatchers
) : ViewModel() {

    var currentSearchQuery = ""

    private val action = MutableLiveData<Action>()

    val state: LiveData<UiState> = action
        .distinctUntilChanged()
        .switchMap {
            when (it) {
                is Action.Search, Action.Sort, Action.Initialize -> {
                    getCharactersUseCase.invoke(
                        GetCharactersUseCase.GetCharactersParams(currentSearchQuery, getPagingConfig())
                    ).cachedIn(viewModelScope).map { pagingData ->
                        UiState.SearchResult(pagingData)
                    }.asLiveData(coroutinesDispatchers.main())
                }

                is Action.CleanAction -> {
                    liveData(coroutinesDispatchers.main()) {
                        emit(UiState.Cleaned)
                    }
                }
            }
        }

    fun charactersPagingData(query: String): Flow<PagingData<Character>> {
        return getCharactersUseCase.invoke(
            GetCharactersUseCase.GetCharactersParams(query, getPagingConfig())
        ).cachedIn(viewModelScope)
    }

    private fun getPagingConfig() = PagingConfig(
        pageSize = 20
    )

    fun searchCharacters() {
        if (action.value == Action.Search) {
            action.value = Action.CleanAction
        }
        action.value = Action.Search
    }

    fun applySort() {
        if (action.value == Action.Sort) {
            action.value = Action.CleanAction
        }
        action.value = Action.Sort
    }

    fun initialize() {
        if (action.value == Action.Initialize) {
            action.value = Action.CleanAction
        }
        action.value = Action.Initialize
    }

    fun closeSearch() {
        if (currentSearchQuery.isNotEmpty()) {
            currentSearchQuery = ""
        }
    }

    sealed class UiState {
        data class SearchResult(val data: PagingData<Character>) : UiState()
        object Cleaned : UiState()
    }

    sealed class Action {
        object Search : Action()
        object Sort : Action()
        object Initialize : Action()
        object CleanAction : Action()
    }
}