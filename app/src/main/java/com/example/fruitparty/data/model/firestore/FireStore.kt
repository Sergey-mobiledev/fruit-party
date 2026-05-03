package com.example.fruitparty.data.model.firestore

import androidx.room.Entity
import androidx.room.PrimaryKey

data class FireStore(
    var articleUrl: String = "",
    var contentUrl: String = "",
    var showContent: Boolean? = null,
    var simIsoArray: List<String> = emptyList()
)

@Entity(tableName = "fireStoreModel")
data class FireStoreModelWithUrl(
    @PrimaryKey
    var articleUrl: String = "",
    var isShowContent: Boolean? = null,
    var url: String = ""
)