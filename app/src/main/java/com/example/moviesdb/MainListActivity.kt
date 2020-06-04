package com.example.moviesdb

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dbstest.utils.Constants
import com.example.dbstest.utils.Constants.VIEW_TYPE_ITEM
import com.example.dbstest.utils.Constants.VIEW_TYPE_LOADING
import com.example.moviesdb.adapters.DataListAdapter
import com.example.moviesdb.models.Search
import com.example.moviesdb.network.ServiceGenerator
import com.example.moviesdb.utils.OnLoadMoreListener
import com.example.moviesdb.utils.RecyclerViewLoadMoreScroll
import com.example.moviesdb.viewmodels.MovieListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList


class MainListActivity : BaseActivity() {

    companion object {
        val TAG = MainListActivity::class.java.simpleName
    }

    private lateinit var dataListViewModel: MovieListViewModel
    private lateinit var adapter: DataListAdapter
    private lateinit var dataRepoList: ArrayList<Search>
    var count:Int = 1;
    lateinit var mLayoutManager: RecyclerView.LayoutManager
    lateinit var scrollListener: RecyclerViewLoadMoreScroll
    var movieName: String = "Marvel"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ServiceGenerator(this)
        supportActionBar?.title = getString(R.string.app_name)
        setAdapter()
        //** Set the Layout Manager of the RecyclerView
        setRVLayoutManager()

        //** Set the scrollListerner of the RecyclerView
        setRVScrollListener()

        initialiseViewModel()
        fetchDataRepos(count)
    }

    private fun setAdapter() {
        adapter = DataListAdapter(this,{ item -> doClick(item) })
        adapter.notifyDataSetChanged()
        rv_repo.adapter = adapter

    }

    fun setRVLayoutManager() {
        mLayoutManager = GridLayoutManager(this, 2)
        rv_repo.layoutManager = mLayoutManager
        rv_repo.setHasFixedSize(true)
        rv_repo.adapter = adapter
        (mLayoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    VIEW_TYPE_ITEM -> 1
                    VIEW_TYPE_LOADING -> 2 //number of columns of the grid
                    else -> -1
                }
            }
        }
    }

    private fun setRVScrollListener() {
        scrollListener =
            RecyclerViewLoadMoreScroll(mLayoutManager as GridLayoutManager)
        scrollListener.setOnLoadMoreListener(object :
            OnLoadMoreListener {
            override fun onLoadMore() {
                showProgressBarBottom(true)
                count +=1
                LoadMoreData()
            }
        })

        rv_repo.addOnScrollListener(scrollListener)
    }

    private fun LoadMoreData() {
        adapter.addLoadingView()
        dataRepoList = ArrayList()
        Handler().postDelayed({
            fetchDataRepos(count)
            adapter.removeLoadingView()

           /* //Update the recyclerView in the main thread
            rv_repo.post {
                adapter.notifyDataSetChanged()
            }*/
        }, 1000)

    }





    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem: MenuItem = menu!!.findItem(R.id.search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.queryHint = "Enter Movie Name"
        searchView.setOnQueryTextListener(searchViewListener)
        searchView.setOnCloseListener {
            count = 1
            movieName = "Marvel"
            fetchDataRepos(1)
            return@setOnCloseListener false
        }
        return true
    }

    private fun initialiseViewModel() {
        dataListViewModel =
            ViewModelProviders.of(this).get(MovieListViewModel(application)::class.java)
        dataListViewModel.getStatus().observe(this, Observer { handleStatus(it) })
    }

    private fun  fetchDataRepos(pageNo: Int) {
        if(hasNetwork()) {
            showProgressBar(true)
            dataListViewModel.getRepos(Constants.API_KEY,movieName,Constants.TYPE,pageNo).observe(this, Observer { repoList ->
                Log.i(TAG, "Viewmodel response: $repoList")

                repoList?.let {

                    showProgressBar(false)
                    if(!it.Response.equals("False")) {
                        if (it?.Search?.isNotEmpty()) {
                            empty_list.visibility = View.GONE
                            dataRepoList = it.Search as ArrayList<Search>
                            adapter.addData(dataRepoList)
                            showProgressBarBottom(false)
                        } else {
                            empty_list.visibility = View.VISIBLE
                        }
                    } else {
                        empty_list.visibility = View.VISIBLE
                    }
                }
            })
        }else{
            showNetworkMessage(hasNetwork())
        }
    }

    private fun handleStatus(status: String?) {
        showProgressBar(false)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error Message")
        builder.setMessage(status)

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }
    private val searchViewListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return if (newText.isNullOrEmpty()) false
                else {
                    adapter.clear()
                    count = 1
                    movieName = newText
                    fetchDataRepos(count)
                    true
                }
            }

        }

    fun doClick(data:Bundle){

        val intent = Intent(this, MovieDetailsActivity::class.java).apply {
            putExtra("data", data)
        }
        startActivity(intent)

    }

    fun showProgressBar(visibility: Boolean) {
        progress_bar.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
    }

    fun showProgressBarBottom(visibility: Boolean) {
        progress_bar_bottom.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
    }
}


