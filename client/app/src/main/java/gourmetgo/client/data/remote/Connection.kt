package gourmetgo.client.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import gourmetgo.client.AppConfig

class Connection {

    private var baseurl = AppConfig.API_BASE_URL

    private val okHttpClient by lazy {
        val builder = OkHttpClient.Builder()
        
        if (AppConfig.ENABLE_LOGGING) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }
        
        builder.build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseurl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}