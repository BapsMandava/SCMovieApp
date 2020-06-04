package com.example.moviesdb.models

data class MovieListResult (
    val Response: String,
    val Search: List<Search>,
    val totalResults: String
)