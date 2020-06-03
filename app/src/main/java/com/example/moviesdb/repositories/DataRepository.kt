package com.example.dbstest.repositories

import android.content.Context
import android.widget.Toast
import com.example.dbstest.models.DataDetailRepo
import com.example.dbstest.models.DataRepo
import com.example.dbstest.network.Api
import com.example.dbstest.network.ApiClient
import com.example.dbstest.network.ServiceGenerator
import com.example.dbstest.utils.ConnectivityUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import io.reactivex.Observable


class DataRepository(private val context: Context) {

    private var dataRepoApiClient: ApiClient = ApiClient()

    fun getAllDataRepos(): Observable<List<DataRepo>> {
        val hasConnection = ConnectivityUtil.isNetworkAvailable(context)
        var observableFromApi: Observable<List<DataRepo>>? = null
        if (hasConnection) {
            observableFromApi = getDataReposFromApi()
        }

        return Observable.concatArrayEager(observableFromApi)
    }



    fun getTitleDetails(id: Int): Observable<DataDetailRepo> {
        val hasConnection = ConnectivityUtil.isNetworkAvailable(context)
        var observableFromApi:  Observable<DataDetailRepo>? = null
        if (hasConnection) {
            observableFromApi = getTitleDetailsFromApi(id)
        }

        return Observable.concatArrayEager(observableFromApi)
    }

    private fun getDataReposFromApi(): Observable<List<DataRepo>> {
        return dataRepoApiClient.getDataRepos()
    }

    private fun getTitleDetailsFromApi(id:Int): Observable<DataDetailRepo> {
        return dataRepoApiClient.getTitleDetailsRepos(id)
    }

}