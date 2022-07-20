package com.rockteki.aa.model

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class MyAccountGroup(
    @get: PropertyName("myAccountGroupId") @set: PropertyName("myAccountGroupId") var myAccountGroupId: String = "",
    @get: PropertyName("myAccountGroupName") @set: PropertyName("myAccountGroupName") var myAccountGroupName: String = "",
    @get: PropertyName("myAccountGroupSize") @set: PropertyName("myAccountGroupSize") var myAccountGroupSize: Int = 0,
    @get: PropertyName("isAvailable") @set: PropertyName("isAvailable") var isAvailable: Boolean = true
) : Serializable
