package com.example.marvelapp.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.marvelapp.presentation.detail.FavoritesUiActionStateLiveData

@Composable
fun FavoriteButton(
    uiState: FavoritesUiActionStateLiveData.UiState,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge.copy(
            topEnd = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
        ),
        color = Color.White
    ) {
        var icon = Icons.Filled.FavoriteBorder
        var color = Color.Black
        if (uiState is FavoritesUiActionStateLiveData.UiState.Icon) {
            icon = if (uiState.isFavorite) {
                color = Color.Red
                Icons.Filled.Favorite
            } else {
                Icons.Filled.FavoriteBorder
            }
        }
        Icon(
            modifier = Modifier.padding(8.dp),
            imageVector = icon,
            contentDescription = null,
            tint = color)
    }
    
}

@Suppress("UnusedPrivateMember")
@Preview
@Composable
private fun FavoriteButtonPreview() {
    FavoriteButton(
        uiState = FavoritesUiActionStateLiveData.UiState.Icon(true),
        onClick = {},
    )
}

@Suppress("UnusedPrivateMember")
@Preview
@Composable
private fun NotFavoriteButtonPreview() {
    FavoriteButton(
        uiState = FavoritesUiActionStateLiveData.UiState.Icon(false),
        onClick = {},
    )
}