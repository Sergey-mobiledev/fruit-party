package com.example.fruitparty.data.services

import com.example.fruitparty.data.model.blog.Blog
import com.example.fruitparty.data.model.firestore.FireStoreModelWithUrl
import com.example.fruitparty.data.services.Constants.NO_INTERNET_CONNECTION

class DataLoadingState constructor(
    val status: Status,
    val msg: String? = null,
    val blogsList: List<Blog>? = null,
    val fireStoreModelWithUrl: FireStoreModelWithUrl? = null,
) {
    companion object {
        val READING_FIREBASE = DataLoadingState(Status.READING_FIREBASE)
        val READING_BLOGS_URL_FROM_FIREBASE =
            DataLoadingState(status = Status.READING_BLOGS_URL_FROM_FIREBASE)

        fun loadedBlogsUrlFromFirebase(url: String) =
            DataLoadingState(Status.LOADED_BLOGS_URL_FROM_FIREBASE, url)

        fun loadedFirebaseModel(fireStoreModelWithUrl: FireStoreModelWithUrl) =
            DataLoadingState(
                Status.LOADED_FIREBASE_MODEL,
                fireStoreModelWithUrl = fireStoreModelWithUrl
            )

        val LOADING_BLOGS = DataLoadingState(status = Status.LOADING_BLOGS)
        fun loadedBlogs(blogsList: List<Blog>) =
            DataLoadingState(status = Status.LOADED_BLOGS, blogsList = blogsList)

        fun errorBadUrl() = DataLoadingState(status = Status.BAD_URL, "Error 404")
        fun errorInternetConnection() =
            DataLoadingState(status = Status.ERROR_INTERNET_CONNECTION, NO_INTERNET_CONNECTION)

        fun errorTimeoutExpired() =
            DataLoadingState(status = Status.ERROR_TIMEOUT_EXPIRED, NO_INTERNET_CONNECTION)

        fun errorFirebaseModel() = DataLoadingState(status = Status.ERROR_FIREBASE_MODEL)
    }

    enum class Status {
        READING_FIREBASE,
        READING_BLOGS_URL_FROM_FIREBASE,
        LOADED_BLOGS_URL_FROM_FIREBASE,
        LOADING_BLOGS,
        LOADED_BLOGS,
        LOADED_FIREBASE_MODEL,
        BAD_URL,
        ERROR_INTERNET_CONNECTION,
        ERROR_TIMEOUT_EXPIRED,
        ERROR_FIREBASE_MODEL,
    }
}