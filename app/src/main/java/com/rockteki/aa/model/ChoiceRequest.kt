package com.rockteki.aa.model

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class ChoiceRequest(
    @get: PropertyName("choiceRequestId") @set: PropertyName("choiceRequestId") var choiceRequestId : String = "",
    @get: PropertyName("createDate") @set: PropertyName("createDate") var createDate : Long = 0,
    @get: PropertyName("expireDate") @set: PropertyName("expireDate") var expireDate : Long = 0,
    @get: PropertyName("createUserId") @set: PropertyName("createUserId") var createUserId : String = "",
    @get: PropertyName("createUserName") @set: PropertyName("createUserName") var createUserName : String = "",
    @get: PropertyName("storeId") @set: PropertyName("storeId") var storeId : String = "",
    @get: PropertyName("storeName") @set: PropertyName("storeName") var storeName : String = "",
    @get: PropertyName("storeOrderId") @set: PropertyName("storeOrderId") var storeOrderId : String = "",
    @get: PropertyName("requestAccountIdList") @set: PropertyName("requestAccountIdList") var requestAccountIdList : List<String> = arrayListOf(),
    @get: PropertyName("isOpen") @set: PropertyName("isOpen") var isOpen : Boolean = true,

) : Serializable
