package com.rockteki.aa.model

import com.google.firebase.firestore.PropertyName

data class Account(
    @get: PropertyName("accountId") @set: PropertyName("accountId") var accountId: String = "",
    @get: PropertyName("accountName") @set: PropertyName("accountName") var accountName: String = "",
    @get: PropertyName("accountSearchHint") @set: PropertyName("accountSearchHint") var accountSearchHint: List<String> = arrayListOf()
)
