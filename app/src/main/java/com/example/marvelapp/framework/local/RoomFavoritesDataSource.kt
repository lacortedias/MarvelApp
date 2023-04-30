package com.example.marvelapp.framework.local

import com.example.core.data.repository.FavoritesLocalDataSource
import com.example.core.domain.model.Character
import com.example.marvelapp.framework.db.dao.FavoriteDao
import com.example.marvelapp.framework.db.entity.FavoriteEntity
import com.example.marvelapp.framework.db.entity.toCharactersModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomFavoritesDataSource @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoritesLocalDataSource {
    override fun getAllFavoritesLocalDataSource(): Flow<List<Character>> {
        return favoriteDao.loadFavorites().map {
            it.toCharactersModel()
        }
    }

    override suspend fun isFavorite(characterId: Int): Boolean {
        return favoriteDao.isFavorite(characterId) != null
    }

    override suspend fun saveFavoriteLocalDataSource(character: Character) {
        favoriteDao.insertFavorites(character.toFavoriteEntity())
    }

    override suspend fun deleteFavoriteLocalDataSource(character: Character) {
        favoriteDao.deleteFavorites(character.toFavoriteEntity())
    }

    private fun com.example.core.domain.model.Character.toFavoriteEntity() =
        FavoriteEntity(id, name, imageUrl)
}