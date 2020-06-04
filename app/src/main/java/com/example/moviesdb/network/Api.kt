package com.example.moviesdb.network

import com.example.dbstest.utils.Constants
import com.example.moviesdb.models.MovieDataResult
import com.example.moviesdb.models.MovieListResult
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*


interface Api {
        @GET("?")
        fun getAllMovies(
                @Query("apikey") apiKey: String = Constants.API_KEY,
                @Query("s") name: String,
                @Query("type") type: String,
                @Query("page") pageNo: Int
        ): Observable<MovieListResult>


        @GET("?")
        fun getTitleDataRepos(@Query("apikey") apiKey: String = Constants.API_KEY,
                              @Query("i") i: String ):  Observable<MovieDataResult>
}