package com.rockteki.aa.model

import com.google.firebase.firestore.PropertyName

data class Order(
    @get: PropertyName("orderId") @set: PropertyName("orderId") var orderId : String = "",
    @get: PropertyName("orderStoreId") @set: PropertyName("orderStoreId") var orderStoreId : String = "",
    @get: PropertyName("orderStoreName") @set: PropertyName("orderStoreName") var orderStoreName : String = "",
    @get: PropertyName("createDate") @set: PropertyName("createDate") var createDate : Long = 0,
    @get: PropertyName("expireDate") @set: PropertyName("expireDate") var expireDate : Long = 0,
    @get: PropertyName("createUserId") @set: PropertyName("createUserId") var createUserId : String = "",
    @get: PropertyName("createUserName") @set: PropertyName("createUserName") var createUserName : String = "",
    @get: PropertyName("orderSummary") @set: PropertyName("orderSummary") var orderSummary : Map<String, Int> = mapOf(),
)
