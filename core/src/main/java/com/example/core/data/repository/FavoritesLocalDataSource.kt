package com.example.core.data.repository

import com.example.core.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface FavoritesLocalDataSource {

    fun getAllFavoritesLocalDataSource(): Flow<List<Character>>

    suspend fun saveFavoriteLocalDataSource(character: com.example.core.domain.model.Character)

    suspend fun deleteFavoriteLocalDataSource(character: com.example.core.domain.model.Character)
}