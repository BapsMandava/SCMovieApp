package com.example.moviesdb.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesdb.models.MovieDataResult
import com.example.moviesdb.models.MovieListResult
import com.example.moviesdb.repositories.MoviesRepository
import com.example.moviesdb.viewmodels.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class MovieListViewModel(application: Application) : AndroidViewModel(application) {
    // The ViewModel maintains a reference to the repository to get data.
    private val repository: MoviesRepository
    var data: MutableLiveData<MovieListResult> = MutableLiveData()
    private val mDisposables = CompositeDisposable()
    private val status = SingleLiveEvent<String>()
    var dataDetails: MutableLiveData<MovieDataResult> = MutableLiveData()

    init {
        repository = MoviesRepository(application.baseContext)
    }

    fun getStatus(): LiveData<String> {
        return status
    }


    fun getRepos(apiKey: String,
                 name: String,
                 type: String,
                 pageNo: Int): LiveData<MovieListResult> {
        val observable: Observable<MovieListResult> = repository.getAllDataRepos(apiKey,name,type,pageNo)
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

    fun getSelectedMovieData(apiKey: String,id:String):LiveData<MovieDataResult>  {
        val observable: Observable<MovieDataResult>? = repository.getTitleDetails(apiKey,id)
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