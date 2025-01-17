package com.example.marvelapp.framework.network.response

import com.google.gson.annotations.SerializedName

data class DataContainerResponse<T>(

    @SerializedName("total")
    var total: Int,
    @SerializedName("offset")
    var offset: Int,
    @SerializedName("results")
    var results: List<T>

)
