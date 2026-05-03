package com.example.fruitparty.data.repository

import com.example.fruitparty.data.services.network.ApiService

class ApiRepository(private val apiService: ApiService) {

    suspend fun getBlogs(url: String) = apiService.getBlogs(url)
}