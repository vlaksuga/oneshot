package com.rockteki.aa.model

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Product(
    @get: PropertyName("productId") @set: PropertyName("productId") var productId: String = "",
    @get: PropertyName("productName") @set: PropertyName("productName") var productName: String = "",
    @get: PropertyName("productThumb") @set: PropertyName("productThumb") var productThumb: String = "",
    @get: PropertyName("productPrice") @set: PropertyName("productPrice") var productPrice: Int = 0,
    @get: PropertyName("productSize") @set: PropertyName("productSize") var productSize: String = "",
    @get: PropertyName("productKind") @set: PropertyName("productKind") var productKind: String = "",
    @get: PropertyName("productTag") @set: PropertyName("productTag") var productTag: List<String> = arrayListOf(),
    @get: PropertyName("productDescription") @set: PropertyName("productDescription") var productDescription: String = "",
    @get: PropertyName("isAvailable") @set: PropertyName("isAvailable") var isAvailable: Boolean = false
) : Serializable