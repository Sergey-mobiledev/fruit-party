package com.example.fruitparty.data.model.blog

data class Blog(
    val title: Rendered,
    val content: Rendered,
    val img: Isobar
)

data class Rendered(
    val rendered: String
)

data class Isobar(
    val isobar: String
)