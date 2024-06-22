package com.example.musicstreamingservice.models

// Створюємо data class
data class CategoryModel(
    val name : String,
    val coverUrl: String,
    var songs : List<String>
) {
    constructor() : this("","", listOf())
}
