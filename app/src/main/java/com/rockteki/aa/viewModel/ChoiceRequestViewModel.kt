package com.rockteki.aa.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rockteki.aa.model.Choice
import com.rockteki.aa.model.FireStoreRepository

class ChoiceRequestViewModel : ViewModel() {
    var firebaseRepository = FireStoreRepository()
    var choiceRequests : MutableLiveData<List<Choice>> = MutableLiveData();

    fun listChoiceResults(storeId: String, orderId: String) : LiveData<List<Choice>> {
        firebaseRepository.listChoice(storeId, orderId) { choiceRequests.value = it }
        return choiceRequests
    }

}