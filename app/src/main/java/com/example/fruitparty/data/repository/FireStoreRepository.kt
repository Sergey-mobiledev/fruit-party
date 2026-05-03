package com.example.fruitparty.data.repository

import android.content.Context
import android.util.Log
import com.example.fruitparty.data.model.firestore.FireStore
import com.example.fruitparty.data.model.firestore.FireStoreModelWithUrl
import com.example.fruitparty.data.services.Constants.FIREBASE_COLLECTION
import com.example.fruitparty.data.services.Constants.FIREBASE_DOCUMENT
import com.example.fruitparty.data.services.Constants.FIREBASE_FIELD
import com.example.fruitparty.data.services.Constants.READ_BLOGS_URL_FROM_FIREBASE
import com.example.fruitparty.data.services.Constants.READ_FIREBASE
import com.example.fruitparty.data.services.Constants.TAG_TEST
import com.example.fruitparty.data.services.Constants.checkInternetConnection
import com.example.fruitparty.data.services.Constants.checkIso
import com.example.fruitparty.data.services.Constants.checkUrlStartWithHttps
import com.example.fruitparty.data.services.Constants.getUri
import com.example.fruitparty.data.services.DataLoadingState
import com.example.fruitparty.data.services.network.ApiService
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

class FireStoreRepository(private val context: Context, private val apiService: ApiService) {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)
    private val db = Firebase.firestore

    val sharedFlowDataLoadingState = MutableSharedFlow<DataLoadingState>()

    suspend fun readFirebase() {
        Log.d(TAG_TEST, READ_FIREBASE)
        sharedFlowDataLoadingState.emit(DataLoadingState.READING_FIREBASE)
        if (checkInternetConnection(context)) {
            db.collection(FIREBASE_COLLECTION).document(FIREBASE_DOCUMENT)
                .get()
                .addOnSuccessListener { result ->
                    scope.launch {
                        try {
                            withTimeout(12000) {
                                if (!result.exists()) {
                                    sharedFlowDataLoadingState.emit(
                                        DataLoadingState.errorFirebaseModel()
                                    )
                                    return@withTimeout
                                }
                                if (result == null) {
                                    sharedFlowDataLoadingState.emit(
                                        DataLoadingState.errorFirebaseModel()
                                    )
                                    return@withTimeout
                                }
                                try {
                                    val fireStoreModel = result.toObject(FireStore::class.java)
                                    Log.d(TAG_TEST, fireStoreModel.toString())
                                    if (fireStoreModel?.showContent == true) {
                                        if (checkForEmptyFields(fireStoreModel)) {
                                            if (checkIso(context, fireStoreModel)) {
                                                val fireStoreModelWithUrl = coroutineScope {
                                                    async {
                                                        return@async FireStoreModelWithUrl(
                                                            articleUrl = checkUrlStartWithHttps(
                                                                fireStoreModel.articleUrl
                                                            ),
                                                            isShowContent = fireStoreModel.showContent,
                                                            url = getUri(
                                                                context,
                                                                fireStoreModel.contentUrl
                                                            )
                                                        )
                                                    }
                                                }
                                                sharedFlowDataLoadingState.emit(
                                                    DataLoadingState.loadedFirebaseModel(
                                                        fireStoreModelWithUrl.await()
                                                    )
                                                )
                                                return@withTimeout
                                            }
                                        }
                                    }
                                    sharedFlowDataLoadingState.emit(DataLoadingState.errorFirebaseModel())
                                } catch (e: Exception) {
                                    sharedFlowDataLoadingState.emit(
                                        DataLoadingState.errorFirebaseModel()
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            sharedFlowDataLoadingState.emit(DataLoadingState.errorFirebaseModel())
                        }
                    }
                }
                .addOnFailureListener {
                    scope.launch {
                        sharedFlowDataLoadingState.emit(DataLoadingState.errorFirebaseModel())
                    }
                }
        } else {
            sharedFlowDataLoadingState.emit(DataLoadingState.errorInternetConnection())
        }
    }

    suspend fun readUrlFromFirebase() {
        sharedFlowDataLoadingState.emit(DataLoadingState.READING_BLOGS_URL_FROM_FIREBASE)
        Log.d(TAG_TEST, READ_BLOGS_URL_FROM_FIREBASE)
        if (checkInternetConnection(context)) {
            db.collection(FIREBASE_COLLECTION)
                .get()
                .addOnSuccessListener { result ->
                    scope.launch {
                        try {
                            withTimeout(3000) {
                                for (document in result) {
                                    val url = document.data.getValue(FIREBASE_FIELD).toString()
                                    Log.d(TAG_TEST, url)
                                    if (url.isEmpty()) {
                                        sharedFlowDataLoadingState.emit(
                                            DataLoadingState.errorTimeoutExpired()
                                        )
                                    } else {
                                        sharedFlowDataLoadingState.emit(
                                            DataLoadingState.loadedBlogsUrlFromFirebase(
                                                checkUrlStartWithHttps(url)
                                            )
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            sharedFlowDataLoadingState.emit(DataLoadingState.errorTimeoutExpired())
                        }
                    }
                }
                .addOnFailureListener {
                    scope.launch {
                        sharedFlowDataLoadingState.emit(DataLoadingState.errorTimeoutExpired())
                    }
                }
        } else {
            sharedFlowDataLoadingState.emit(DataLoadingState.errorInternetConnection())
        }
    }

    private suspend fun checkForEmptyFields(fireStoreModel: FireStore): Boolean {
        return isValid(checkUrlStartWithHttps(fireStoreModel.contentUrl)) &&
                fireStoreModel.simIsoArray.isNotEmpty()
    }

    private suspend fun isValid(url: String): Boolean {
        return try {
            apiService.getResponse(url)
            true
        } catch (e: Exception) {
            Log.d(TAG_TEST, e.toString())
            false
        }
    }
}