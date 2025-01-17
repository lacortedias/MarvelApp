package com.example.marvelapp.framework.network.response

import com.example.core.domain.model.Serie
import com.example.marvelapp.R
import com.google.gson.annotations.SerializedName

data class SerieResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("thumbnail")
    var thumbnail: ThumbnailResponse,
)

fun SerieResponse.toSerieModel(): Serie {
    return Serie(
        id = this.id,
        titleCategory = title,
        imageUrl = this.thumbnail.getHttpsUrl(),
        titleParentRes = R.string.details_series_category
    )
}