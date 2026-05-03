package com.example.fruitparty.data.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.fruitparty.data.model.firestore.FireStore
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.util.*

object Constants {

    const val BASE_URL = "https://base.url"
    const val RETRY = "Retry!"
    const val BLOG_TITLE = "blog_title"
    const val TOAST_CLICK_BACK_TO_EXIT = "Please click BACK again to exit"
    const val NO_INTERNET_CONNECTION = "No internet connection"
    const val TAG_TEST = "tag_test"
    const val FIREBASE_COLLECTION = "fruit_party"
    const val FIREBASE_FIELD = "articleUrl"
    const val FIREBASE_DOCUMENT = "data"
    const val READ_BLOGS_URL_FROM_FIREBASE = "Read blogs url from firebase"
    const val COCONUT = "Coconut"
    const val FRUIT_PARTY = "Fruit Party"
    const val STRAWBERRY = "Strawberry"
    const val PEACH = "Peach"
    const val MANGO = "Mango"
    const val APPLE = "Apple"
    const val WATERMELON = "Watermelon"
    const val ORANGE = "Orange"
    const val CHERRY = "Cherry"
    const val READ_FIREBASE = "readFirebase"
    const val RED = "RED"
    const val BLACK = "BLACK"
    const val HEARTS = "HEARTS"
    const val DIAMONDS = "DIAMONDS"
    const val CLUBS = "CLUBS"
    const val SPADES = "SPADES"
    private const val NO_APP_ID = "no_app_id"

    suspend fun checkInternetConnection(context: Context): Boolean {
        return try {
            withTimeout(5000) {
                val deferred = CompletableDeferred<Boolean>()
                var internetConnection = false
                while (!internetConnection) {
                    val cm =
                        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val netInfo = cm.activeNetworkInfo
                    internetConnection = netInfo != null && netInfo.isConnectedOrConnecting
                    if (internetConnection) {
                        deferred.complete(true)
                    }
                    delay(1000)
                }
                deferred.await()
            }
        } catch (e: Exception) {
            false
        }
    }

    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun checkIso(context: Context, fireStoreModel: FireStore): Boolean {
        val isoList = fireStoreModel.simIsoArray
        return !(isoList.find { it == getIso(context) }.isNullOrEmpty())
    }

    private fun getIso(context: Context): String {
        return (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simCountryIso.lowercase(
            Locale.getDefault()
        )
    }

    suspend fun getUri(context: Context, mainUrl: String): String {
        val newMainUrl = checkUrlStartWithHttps(mainUrl)
        var url = ""
        var appId: String
        try {
            appId = getAppId(context)
            if (appId.isEmpty()) appId = getAppId(context)
            if (appId.isEmpty()) appId = NO_APP_ID
        } catch (e: Exception) {
            appId = NO_APP_ID
        }
        url = "${newMainUrl}?aid=${appId}_${com.example.fruitparty.BuildConfig.APPLICATION_ID}"
        Log.d(TAG_TEST, "getUri, url: $url")
        return url
    }

    fun checkUrlStartWithHttps(mainUrl: String): String {
        return if (mainUrl.startsWith("https://")) mainUrl
        else "https://$mainUrl"
    }


    private suspend fun getAppId(context: Context): String {
        return try {
            withTimeout(3000) {
                val deferred = CompletableDeferred<String>()
                FirebaseAnalytics
                    .getInstance(context)
                    .appInstanceId
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result
                            if (result != null) {
                                deferred.complete(result)
                            } else {
                                deferred.completeExceptionally(NullPointerException("Result is null"))
                            }
                        } else {
                            deferred.completeExceptionally(task.exception!!)
                        }
                    }
                deferred.await()
            }
        } catch (e: Exception) {
            Log.w(TAG_TEST, "getAppId", e)
            ""
        }
    }
}