package com.example.dbstest.network

import android.content.Context
import android.util.Log
import com.example.dbstest.utils.ConnectivityUtil
import com.example.dbstest.utils.Constants
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

private const val TAG = "ServiceGenerator"
private const val HEADER_CACHE_CONTROL = "Cache-Control"
private const val HEADER_PRAGMA = "Pragma"
private const val cacheSize = 5 * 1024 * 1024.toLong()

class ServiceGenerator(private val context: Context) {

    companion object {
        lateinit var instance: ServiceGenerator
    }

    init {
        instance = this
    }

    private val okHttpClient = OkHttpClient.Builder()
        .cache(cache())
        .addInterceptor(httpLoggingInterceptor()) // used if network off OR on
        .addNetworkInterceptor(networkInterceptor()) // only used when network is on
        .addInterceptor(offlineInterceptor()).build()

    private fun httpLoggingInterceptor(): HttpLoggingInterceptor? {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                Log.d(TAG, "httplogging interceptor: called: $message")
            })
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        return httpLoggingInterceptor
    }

    /**
     * This interceptor will be called ONLY if the network is available
     * @return
     */
    private fun networkInterceptor(): Interceptor {
        return Interceptor { chain ->
            Log.d(TAG, "network interceptor: called.")
            val response: Response = chain.proceed(chain.request())
            val cacheControl = CacheControl.Builder()
                .maxAge(
                    5,
                    TimeUnit.MINUTES
                ) // if the request is executed in less than 5 mins, it will get from cache
                .build()
            response.newBuilder()
                .removeHeader(HEADER_PRAGMA) // header that is attached to HTTP request and says not to use cache any more
                .removeHeader(HEADER_CACHE_CONTROL) // that defines cache control
                .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                .build()
        }
    }

    /**
     * This interceptor will be called both if the network is available and if the network is not available
     * @return
     */
    private fun offlineInterceptor(): Interceptor? {
        return Interceptor { chain ->
            Log.d(TAG, "offline interceptor: called.")
            var request: Request = chain.request()

            // prevent caching when network is on. For that we use the "networkInterceptor"
            if (!ConnectivityUtil.isNetworkAvailable(context)) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(
                        2,
                        TimeUnit.HOURS
                    ) // check the cache and if the data is newer than 2 hrs old then you can use it.
                    .build()
                request = request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }
    }

    private fun cache(): Cache? {
        return Cache(File(context.cacheDir, "offlineCache"), cacheSize)
    }

    private val retroFitBuilder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)


    private val retrofit = retroFitBuilder.build()

    private val dataRepoApi = retrofit.create(Api::class.java)

    fun getDataRepoApi(): Api {
        return dataRepoApi
    }

}