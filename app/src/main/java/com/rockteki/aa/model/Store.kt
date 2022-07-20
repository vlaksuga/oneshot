package com.rockteki.aa.model

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Store(
    @get: PropertyName("storeId") @set: PropertyName("storeId") var storeId: String = "",
    @get: PropertyName("storeName") @set: PropertyName("storeName") var storeName: String = "",
) : Serializable
