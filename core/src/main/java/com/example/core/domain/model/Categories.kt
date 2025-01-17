package com.example.core.domain.model

data class Categories (
    val comics: List<Comic>,
    val events: List<Event>,
    val series: List<Serie>
)