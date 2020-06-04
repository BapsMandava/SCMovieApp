package com.example.moviesdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.dbstest.utils.Constants
import com.example.moviesdb.databinding.ActivityMovieDetailsBinding
import com.example.moviesdb.viewmodels.MovieListViewModel
import kotlinx.android.synthetic.main.activity_movie_details.*

class MovieDetailsActivity : BaseActivity() {
    private lateinit var dataListViewModel: MovieListViewModel
    lateinit var binding: ActivityMovieDetailsBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details)
        binding.executePendingBindings()
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        initialiseViewModel()
        var data = intent.getBundleExtra("data")
        title = data.getString("title").toString()
        getSupportActionBar()?.setTitle(title);
        getDataDetails(data.getString("id",""))
    }

    private fun initialiseViewModel() {
        dataListViewModel =
            ViewModelProviders.of(this).get(MovieListViewModel(application)::class.java)
    }


    fun getDataDetails(id:String){
        if(hasNetwork()) {
            dataListViewModel.getSelectedMovieData(Constants.API_KEY,id).observe(this, Observer { movieDetails ->
                movieDetails?.let {
                    binding.movieData = it
                    Glide.with(this).load(it.poster).fitCenter()
                        .placeholder(R.drawable.ic_broken_image).into(ivPoster)
                }
            })
        } else {
            showNetworkMessage(hasNetwork())
        }
    }
}