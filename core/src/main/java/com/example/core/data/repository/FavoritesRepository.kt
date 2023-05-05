package com.example.core.data.repository

import com.example.core.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    suspend fun filterFavorites(query: String) : Flow<List<Character>>

    fun getAllFavorites(): Flow<List<Character>>

    suspend fun isFavorite(characterId: Int): Boolean

    suspend fun saveFavorite(character: Character)

    suspend fun deleteFavorite(character: Character)
}