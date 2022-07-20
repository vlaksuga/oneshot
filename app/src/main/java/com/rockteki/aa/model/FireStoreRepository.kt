package com.rockteki.aa.model

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase


class FireStoreRepository {

    private val fireStoreDB = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser

    private fun getDocument(docRef: String, success: (DocumentSnapshot) -> Unit) {
        fireStoreDB.document(docRef).get().addOnSuccessListener { success(it) }
    }

    private fun getCollection(query: Query, success: (QuerySnapshot) -> Unit) {
        query.get().addOnSuccessListener { success(it) }
    }

    private fun getCollection(query: Query, success: (QuerySnapshot) -> Unit, fail: ((Exception) -> Unit)?) {
        query.get().addOnSuccessListener { success(it) }
        if (fail == null) { query.get().addOnFailureListener {} }
        else { query.get().addOnFailureListener { fail(it) } }
    }

    private fun updateDocument(colRef: String, docId: String, data: Map<String, Any?>): Task<Void> {
        return fireStoreDB.collection(colRef).document(docId).update(data)
    }

    private fun setDocument(colRef: String, docId: String, data: Map<String, Any?>): Task<Void> {
        return fireStoreDB.collection(colRef).document(docId).set(data)
    }

    private fun deleteDocument(colRef: String, docId: String): Task<Void> {
        return fireStoreDB.collection(colRef).document(docId).delete()
    }

    private fun onCollectionChange(colRef: String, change: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit) {
        fireStoreDB.collection(colRef).addSnapshotListener { value, error -> change(value, error) }
    }

   fun listOpenChoiceRequestOf(accountId: String, callback: (List<ChoiceRequest>) -> Unit) {
       val query : Query = fireStoreDB.collection("accounts/${accountId}/choiceRequests").whereEqualTo("isOpen", true)
       getCollection(query) { callback(it.toObjects()); }
   }

    fun listChoiceRequestCreatedBy(accountId: String, callback: (List<ChoiceRequest>) -> Unit) {
        val query : Query = fireStoreDB.collection("accounts/${accountId}/choiceRequests").whereEqualTo("createUserId", accountId)
        getCollection(query) { callback(it.toObjects()); }
    }

    fun listMyAccountGroupOf(accountId: String, callback: (List<MyAccountGroup>) -> Unit) {
        val query : Query = fireStoreDB.collection("accounts/${accountId}/myAccountGroups").whereEqualTo("isAvailable", true)
        getCollection(query) { callback(it.toObjects()); }
    }

    fun listChoice(storeId: String, orderId: String, callback: (List<Choice>) -> Unit) {
        if(user != null) {
            val query : Query = fireStoreDB.collection("stores/${storeId}/orders/${orderId}/choices").whereEqualTo("choiceAccountId", user.uid)
            getCollection(query) { callback(it.toObjects()); }
        }
    }

    fun listChoiceAll(storeId: String, orderId: String, callback: (QuerySnapshot) -> Unit) {
        val query : Query = fireStoreDB.collection("stores/${storeId}/orders/${orderId}/choices")
        getCollection(query) { callback(it); }
    }

    fun listStore(callback: (List<Store>) -> Unit) {
        val query : Query = fireStoreDB.collection("stores")
        getCollection(query) { callback(it.toObjects()); }
    }

    fun listMemberOf(accountId:String, groupId: String, callback: (List<Account>) -> Unit) {
        val query : Query = fireStoreDB.collection("accounts/${accountId}/myAccountGroups/${groupId}/accounts").whereEqualTo("isAvailable", true)
        getCollection(query) { callback(it.toObjects()); }
    }

    fun listProductByKind(storeId: String, kind: String, callback: (List<Product>) -> Unit) {
        val query : Query = fireStoreDB.collection("stores/${storeId}/products").whereEqualTo("productKind", kind)
        getCollection(query) { callback(it.toObjects()); }
    }

    fun setOrder(storeId: String, orderId: String, data: Map<String, Any?>): Task<Void> {
        return setDocument("stores/${storeId}/orders", orderId, data)
    }

    fun setChoiceRequest(accountId: String, choiceRequestId: String, data: Map<String, Any?>): Task<Void> {
        return setDocument("accounts/${accountId}/choiceRequests", choiceRequestId, data)
    }

    fun deleteChoice(storeId: String, storeOrderId: String, choiceId: String): Task<Void> {
        return deleteDocument("stores/${storeId}/orders/${storeOrderId}/choices", choiceId)
    }

    fun updateChoice(storeId: String, orderId: String, choiceId: String, data: Map<String, Any?>): Task<Void> {
        return updateDocument("stores/${storeId}/orders/${orderId}/choices", choiceId, data)
    }

    fun onChoiceChange(storeId: String, orderId: String, change: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit) {
        onCollectionChange("stores/${storeId}/orders/${orderId}/choices") { value, error -> change(value, error) }
    }

    fun closeOrder(storeId: String, orderId: String, data: Map<String, Any?>): Task<Void> {
        return updateDocument("stores/${storeId}/orders", orderId, data)
    }

    fun closeChoiceRequest(accountId: String, choiceRequestId: String, data: Map<String, Boolean>): Task<Void> {
        return updateDocument("accounts/${accountId}/choiceRequests", choiceRequestId, data)
    }

    fun getMyAccount(uid: String, callback: (Account?) -> Unit) {
        getDocument("accounts/${uid}") { callback(it.toObject()) }
    }

    fun setAccount(uid: String, data: Map<String, Any?>): Task<Void> {
        return setDocument("accounts", uid, data)
    }

    fun setChoice(storeId: String, orderId: String, choiceId: String, data: Map<String, Any?>): Task<Void> {
        return setDocument("stores/${storeId}/orders/${orderId}/choices", choiceId, data)
    }


}