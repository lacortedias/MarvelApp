package com.example.core.data.repository

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun getAllFavorites(): Flow<List<com.example.core.domain.model.Character>>

    suspend fun isFavorite(characterId: Int): Boolean

    suspend fun saveFavorite(character: com.example.core.domain.model.Character)

    suspend fun deleteFavorite(character: com.example.core.domain.model.Character)
}