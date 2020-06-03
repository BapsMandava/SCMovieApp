package com.example.dbstest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dbstest.models.DataDetailRepo


import com.example.dbstest.models.DataRepo
import com.example.dbstest.repositories.DataRepository
import com.example.moviesdb.viewmodels.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class DataListViewModel(application: Application) : AndroidViewModel(application) {
    // The ViewModel maintains a reference to the repository to get data.
    private val repository: DataRepository
    var data: MutableLiveData<List<DataRepo>> = MutableLiveData()
    var dataDetails: MutableLiveData<DataDetailRepo> = MutableLiveData()
    var errorMessage: MutableLiveData<String> = MutableLiveData()
    private val mDisposables = CompositeDisposable()
    private val status = SingleLiveEvent<String>()

    init {
        repository = DataRepository(application.baseContext)
    }

    fun getStatus(): LiveData<String> {
        return status
    }


    fun getRepos(): LiveData<List<DataRepo>> {
        val observable: Observable<List<DataRepo>> = repository.getAllDataRepos()
        val result = observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                data.postValue(result)
            }, { error ->
                error.printStackTrace()
                status.value = error.localizedMessage
            }, {
                //completed
            })
        mDisposables.add(result)
        return data
    }

    fun getSelectedData(id:Int):LiveData<DataDetailRepo>  {
        val observable: Observable<DataDetailRepo>? = repository.getTitleDetails(id)
        val result = observable?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ result ->
                dataDetails.postValue(result)
            }, {
                    error -> error.printStackTrace()

            }, {
                //completed
            })
        result?.let { mDisposables.add(it) }
        return dataDetails
    }
}