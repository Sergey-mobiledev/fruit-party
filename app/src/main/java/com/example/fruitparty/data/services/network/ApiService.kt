package com.example.fruitparty.data.services.network

import com.example.fruitparty.data.model.blog.Blog
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    suspend fun getBlogs(@Url url: String): List<Blog>

    @GET
    suspend fun getResponse(@Url url: String)
}