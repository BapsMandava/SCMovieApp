package com.example.moviesdb.network

import com.example.moviesdb.models.MovieDataResult
import com.example.moviesdb.models.MovieListResult
import io.reactivex.Observable
import retrofit2.http.Query


class ApiClient {
    fun getAllMoviesRepo( apiKey: String,
                      name: String,
                      type: String,
                      pageNo: Int): Observable<MovieListResult> {
        return ServiceGenerator.instance.getDataRepoApi().getAllMovies(apiKey,name,type,pageNo)
    }

    fun getMovieDetailsRepos(apiKey: String,id:String): Observable<MovieDataResult> {
        return ServiceGenerator.instance.getDataRepoApi().getTitleDataRepos(apiKey,id)
    }
}