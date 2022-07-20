package com.rockteki.aa.model

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Choice (
    @get: PropertyName("choiceId") @set: PropertyName("choiceId") var choiceId: String = "",
    @get: PropertyName("choiceAccountId") @set: PropertyName("choiceAccountId") var choiceAccountId: String = "",
    @get: PropertyName("choiceQuantity") @set: PropertyName("choiceQuantity") var choiceQuantity: Int = 0,
    @get: PropertyName("choiceProductId") @set: PropertyName("choiceProductId") var choiceProductId: String = "",
    @get: PropertyName("choiceProductName") @set: PropertyName("choiceProductName") var choiceProductName: String = "",
    @get: PropertyName("choiceProductSize") @set: PropertyName("choiceProductSize") var choiceProductSize: String = "",
    @get: PropertyName("choiceProductThumb") @set: PropertyName("choiceProductThumb") var choiceProductThumb: String = "",
    @get: PropertyName("choiceProductPrice") @set: PropertyName("choiceProductPrice") var choiceProductPrice: Int = 0,
    @get: PropertyName("choiceMemo") @set: PropertyName("choiceMemo") var choiceMemo: String = ""
) : Serializable