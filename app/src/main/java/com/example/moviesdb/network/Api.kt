package com.example.dbstest.network

import com.example.dbstest.models.DataDetailRepo
import com.example.dbstest.models.DataRepo
import com.example.dbstest.utils.Constants
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*


interface Api {
        @GET(Constants.GET_ARTICLE)
        fun getDataRepos():  Observable<List<DataRepo>>

        @GET(Constants.GET_ARTICLE+Constants.GET_ARTICLE_ID)
        fun getTitleDataRepos(@Path("id") id:Int):  Observable<DataDetailRepo>
}