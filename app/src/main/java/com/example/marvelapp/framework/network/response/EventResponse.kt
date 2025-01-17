package com.example.marvelapp.framework.network.response

import com.example.core.domain.model.Event
import com.example.marvelapp.R
import com.google.gson.annotations.SerializedName

data class EventResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("thumbnail")
    val thumbnail: ThumbnailResponse
)

fun EventResponse.toEventModel(): Event {
    return Event(
        id = this.id,
        titleCategory = title,
        imageUrl = this.thumbnail.getHttpsUrl(),
        titleParentRes = R.string.details_events_category
    )
}