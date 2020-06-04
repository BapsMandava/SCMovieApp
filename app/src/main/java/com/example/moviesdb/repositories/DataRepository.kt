package com.example.moviesdb.repositories

import android.content.Context
import android.widget.Toast
import com.example.dbstest.utils.ConnectivityUtil
import com.example.moviesdb.models.MovieDataResult
import com.example.moviesdb.models.MovieListResult
import com.example.moviesdb.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import io.reactivex.Observable


class MoviesRepository(private val context: Context) {

    private var dataRepoApiClient: ApiClient = ApiClient()

    fun getAllDataRepos(apiKey: String,
                        name: String,
                        type: String,
                        pageNo: Int): Observable<MovieListResult> {
        val hasConnection = ConnectivityUtil.isNetworkAvailable(context)
        var observableFromApi: Observable<MovieListResult>? = null
        if (hasConnection) {
            observableFromApi = getDataReposFromApi(apiKey,name,type,pageNo)
        }

        return Observable.concatArrayEager(observableFromApi)
    }

    fun getTitleDetails(apiKey: String,id:String): Observable<MovieDataResult> {
        val hasConnection = ConnectivityUtil.isNetworkAvailable(context)
        var observableFromApi:  Observable<MovieDataResult>? = null
        if (hasConnection) {
            observableFromApi = getMovieDetailsFromApi(apiKey,id)
        }

        return Observable.concatArrayEager(observableFromApi)
    }


    private fun getDataReposFromApi(apiKey: String,
                                    name: String,
                                    type: String,
                                    pageNo: Int): Observable<MovieListResult> {
        return dataRepoApiClient.getAllMoviesRepo(apiKey,name,type,pageNo)
    }

    private fun getMovieDetailsFromApi(apiKey: String,id:String): Observable<MovieDataResult> {
        return dataRepoApiClient.getMovieDetailsRepos(apiKey,id)
    }

}