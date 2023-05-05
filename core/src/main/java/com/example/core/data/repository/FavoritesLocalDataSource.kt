package com.example.core.data.repository

import com.example.core.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface FavoritesLocalDataSource {

    fun filterFavorites(query: String) : Flow<List<Character>>

    fun getAllFavoritesLocalDataSource(): Flow<List<Character>>

    suspend fun isFavorite(characterId: Int): Boolean
    
    suspend fun saveFavoriteLocalDataSource(character: Character)

    suspend fun deleteFavoriteLocalDataSource(character: Character)
}