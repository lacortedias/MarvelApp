package com.example.marvelapp.framework.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.data.DbConstants.FAVORITES_COLUMN_INFO_ID
import com.example.core.data.DbConstants.FAVORITES_COLUMN_INFO_IMAGE_URL
import com.example.core.data.DbConstants.FAVORITES_COLUMN_INFO_NAME
import com.example.core.data.DbConstants.FAVORITES_TABLE_NAME

@Entity(tableName = FAVORITES_TABLE_NAME)
data class FavoriteEntity(
    @PrimaryKey
    @ColumnInfo(FAVORITES_COLUMN_INFO_ID)
    val id: Int,
    @ColumnInfo(FAVORITES_COLUMN_INFO_NAME)
    val name: String,
    @ColumnInfo(FAVORITES_COLUMN_INFO_IMAGE_URL)
    val imageUrl: String
)

fun List<FavoriteEntity>.toCharactersModel() = map {
    com.example.core.domain.model.Character(
        it.id,
        it.name,
        it.imageUrl
    )
}