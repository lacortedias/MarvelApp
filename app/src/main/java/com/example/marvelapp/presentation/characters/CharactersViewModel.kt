package com.example.marvelapp.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.core.domain.model.Character
import com.example.core.usecase.GetChatactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val getCharactersUsecase: GetChatactersUseCase
): ViewModel() {

    fun charactersPagingData(query: String): Flow<PagingData<Character>> {
        return getCharactersUsecase.invoke(
            GetChatactersUseCase.GetCharactersParams(query, getPagingConfig())
        ).cachedIn(viewModelScope)
    }

    private fun getPagingConfig() = PagingConfig(
        pageSize = 20
    )

}