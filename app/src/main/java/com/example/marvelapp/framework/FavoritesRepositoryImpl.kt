package com.example.marvelapp.framework

import com.example.core.data.repository.FavoritesLocalDataSource
import com.example.core.data.repository.FavoritesRepository
import com.example.core.domain.model.Character
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val favoritesLocalDataSource: FavoritesLocalDataSource
) : FavoritesRepository {
    override fun getAllFavorites(): Flow<List<Character>> {
        return favoritesLocalDataSource.getAllFavoritesLocalDataSource()
    }

    override suspend fun isFavorite(characterId: Int): Boolean {
        return favoritesLocalDataSource.isFavorite(characterId)
    }

    override suspend fun saveFavorite(character: Character) {
        favoritesLocalDataSource.saveFavoriteLocalDataSource(character)
    }

    override suspend fun deleteFavorite(character: Character) {
        favoritesLocalDataSource.deleteFavoriteLocalDataSource(character)
    }
}