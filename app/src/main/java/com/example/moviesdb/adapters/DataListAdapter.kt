package com.example.moviesdb.adapters

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dbstest.utils.Constants
import com.example.moviesdb.R

import com.example.moviesdb.models.Search
import kotlinx.android.synthetic.main.progress_loading.view.*

class DataListAdapter internal constructor(context: Context,  val adapterOnClick : (Bundle) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var repos = ArrayList<Search>()
    private var context: Context = context


    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun addData(dataViews: ArrayList<Search>) {
        this.repos.addAll(dataViews)
        notifyDataSetChanged()
    }

    fun addLoadingView() {
        //add loading item
        Handler().post {
            repos.add(Search())
            notifyItemInserted(repos.size - 1)
        }
    }

    fun removeLoadingView() {
        //Remove loading item
        if (repos.size != 0) {
            repos.removeAt(repos.size - 1)
            notifyItemRemoved(repos.size)
        }
    }



    inner class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val date: TextView = itemView.findViewById(R.id.year)
        val ivImage: ImageView = itemView.findViewById(R.id.ivAvatar)
        val container: CardView = itemView.findViewById(R.id.cvMovieCard)
        fun setItem(item: String,title:String) {
            var data = Bundle()
            data.putString("id",item)
            data.putString("title",title)
            container.setOnClickListener { adapterOnClick(data) }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constants.VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_datalist_item, parent, false)
            RepoViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.progress_loading, parent, false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                view.progressbar.indeterminateDrawable.colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP)
            } else {
                view.progressbar.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
            }
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is RepoViewHolder) {
            val current = repos[position]
            if(current.year>0) {
                holder.title.text = current.title
                holder.date.text = current.year.toString()

                Glide.with(context).load(current.poster)
                    .placeholder(R.drawable.ic_broken_image).into(holder.ivImage)
                holder.setItem(current.imdbID,current.title)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (repos[position] == null) {
            Constants.VIEW_TYPE_LOADING
        } else {
            Constants.VIEW_TYPE_ITEM
        }
    }


 /*   internal fun setRepos(repos: List<Search>) {
        this.repos = repos
        notifyDataSetChanged()
    }*/

    override fun getItemCount() = repos.size

   fun clear() {
       repos.clear()
       notifyDataSetChanged()
   }

}