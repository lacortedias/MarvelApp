package com.example.core.data.repository

import com.example.core.domain.model.CharacterPaging
import com.example.core.domain.model.Comic
import com.example.core.domain.model.Event
import com.example.core.domain.model.Serie

interface CharactersRemoteDataSource {

    suspend fun fetchCharacters(queries: Map<String, String>): CharacterPaging

    suspend fun fetchComics(characterId: Int, offset: Int): List<Comic>

    suspend fun fetchEvents(characterId: Int, offset: Int): List<Event>

    suspend fun fetchSeries(characterId: Int, offset: Int): List<Serie>

}