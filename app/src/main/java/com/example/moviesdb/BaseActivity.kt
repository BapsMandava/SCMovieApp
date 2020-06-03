package com.example.moviesdb

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout



abstract class BaseActivity : AppCompatActivity() {

    private lateinit var mProgressBar: ProgressBar

    override fun setContentView(layoutResID: Int) {
        val constraintLayout =
            layoutInflater.inflate(R.layout.activity_base, null) as ConstraintLayout

        val frameLayout = constraintLayout.findViewById<FrameLayout>(R.id.activity_content)

        mProgressBar = constraintLayout.findViewById(R.id.progress_bar)

        layoutInflater.inflate(layoutResID, frameLayout, true)

        super.setContentView(layoutResID)
    }



    open fun hasNetwork(): Boolean {
        return this.isNetworkConnected()
    }

    open fun isNetworkConnected(): Boolean {
        val cm =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }

    fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            Toast.makeText(this, "Internet is not available", Toast.LENGTH_SHORT).show()
        }
    }





}
