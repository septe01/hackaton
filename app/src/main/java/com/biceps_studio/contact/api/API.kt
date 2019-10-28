package com.biceps_studio.task_layout.api

import android.util.Log
import com.biceps_studio.contact.data.ContactData
import com.biceps_studio.contact.data.ResponseData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

class API {

    companion object {
        const val BASE_URL = "https://address-book-exp-api.herokuapp.com/"
        const val ONE_MINUTE = 60L

        private fun getService() : Service {
            //Membuat interceptor untuk mentracking request dan response API ke dalam Log.Error dengan TAG 'API'
            val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) { Log.e("API", message) }
            })

            //Menapilkan Request dan Response secara detail termasuk Request & Response body
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            //Mensetting Response secara manual, termasuk pembatasan waktu response dan juga response header
            val client: OkHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(ONE_MINUTE, TimeUnit.SECONDS)
                .readTimeout(ONE_MINUTE, TimeUnit.SECONDS)
                .writeTimeout(ONE_MINUTE, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                        val origin: Request = chain.request()

                        val request: Request = origin.newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("token", "4b58014f32943c9d2baf758a6c226631")
                            .build()

                        return chain.proceed(request)
                    }
                }).build()

            val gson: Gson = GsonBuilder().setLenient().create()

            //Inisialisasi retrofit termasuk setting baseUrl dan juga handle JSON menjadi ContactData
            val retrofit: Retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(Service::class.java)
        }

        //Memanggil fungsi getContacts yg ada di interface Sevice secara ringkas
        fun getContacts(callback: Callback<ResponseData>){
            getService().getContacts().enqueue(callback)
        }

        //Memanggil fungsi getDetail yg ada di interface Sevice secara ringkas
        fun getDetail(id: Int, callback: Callback<ContactData>){
            getService().getDetail(id).enqueue(callback)
        }

        //Memanggil fungsi addContact yg ada di interface Sevice secara ringkas
        fun addContact(hashMap: HashMap<String, String>, callback: Callback<ContactData>){
            getService().addContact(hashMap).enqueue(callback)
        }

        //Memanggil fungsi updateContact yg ada di interface Sevice secara ringkas
        fun updateContact(contactData: ContactData, callback: Callback<Array<Int>>){
            getService().updateContact(contactData.id, contactData).enqueue(callback)
        }

        //Memanggil fungsi deleteContact yg ada di interface Sevice secara ringkas
        fun deleteContact(id: Int, callback: Callback<Int>){
            getService().deleteContact(id).enqueue(callback)
        }
    }

    interface Service {

        @GET("users")
        fun getContacts() : Call<ResponseData>

        @GET("users/{id}")
        fun getDetail(@Path("id") id: Int) : Call<ContactData>

        @POST("users")
        fun addContact(@Body hashMap: HashMap<String, String>) : Call<ContactData>

        @PATCH("users/{id}")
        fun updateContact(@Path("id") id: Int, @Body contactData: ContactData) : Call<Array<Int>>

        @DELETE("users/{id}")
        fun deleteContact(@Path("id") id: Int) : Call<Int>
    }
}