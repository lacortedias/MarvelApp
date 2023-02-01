package com.example.marvelapp.framework.network.response

data class DataContainerResponse(

    val total: Int,
    val offset: Int,
    val results: List<CharacterResponse>

)
