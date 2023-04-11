package com.example.marvelapp.framework.db.dao

import androidx.room.Query
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.core.data.DbConstants
import com.example.marvelapp.framework.db.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM ${DbConstants.FAVORITES_TABLE_NAME}")
    fun loadFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM ${DbConstants.FAVORITES_TABLE_NAME} WHERE id = :characterId")
    suspend fun isFavorite(characterId: Int): FavoriteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(favoriteEntity: FavoriteEntity)

    @Delete
    suspend fun deleteFavorites(favoriteEntity: FavoriteEntity)
}