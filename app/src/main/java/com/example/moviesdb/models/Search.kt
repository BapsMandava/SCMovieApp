package com.example.moviesdb.models

import com.google.gson.annotations.SerializedName

data class Search (
    @SerializedName("Title") val title : String="",
    @SerializedName("Year") val year : Int=0,
    @SerializedName("imdbID") val imdbID : String="",
    @SerializedName("Type") val type : String="",
    @SerializedName("Poster") val poster : String=""
)