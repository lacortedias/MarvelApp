package com.example.marvelapp.framework.network.response

import com.google.gson.annotations.SerializedName

data class DataContainerResponse<T>(

    @SerializedName("total")
    val total: Int,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("results")
    val results: List<T>

)
