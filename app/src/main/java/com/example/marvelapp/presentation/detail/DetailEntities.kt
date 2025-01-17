package com.example.marvelapp.presentation.detail

import androidx.annotation.StringRes

data class DetailChildVE(
    val id: Int,
    val titleCategory: String,
    val imageUrl: String,
    val titleParentRes: Int
)

data class DetailParentVE(
    @StringRes
    val categoryStringResId: Int?,
    val detailChildList: MutableList<DetailChildVE> = mutableListOf()
)