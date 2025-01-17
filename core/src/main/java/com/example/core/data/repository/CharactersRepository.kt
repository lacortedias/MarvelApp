package com.example.core.data.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.core.domain.model.Character
import com.example.core.domain.model.Comic
import com.example.core.domain.model.Event
import com.example.core.domain.model.Serie
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {

    fun getCachedCharacters(
        query: String,
        orderBy: String,
        pagingConfig: PagingConfig
    ) : Flow<PagingData<Character>>

    suspend fun getComics(characterId: Int, offset: Int): List<Comic>

    suspend fun getEvents(characterId: Int, offset: Int): List<Event>

    suspend fun getSeries(characterId: Int, offset: Int): List<Serie>

}