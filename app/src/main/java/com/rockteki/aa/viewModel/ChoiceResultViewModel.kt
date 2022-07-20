package com.rockteki.aa.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rockteki.aa.model.Choice
import com.rockteki.aa.model.FireStoreRepository

class ChoiceResultViewModel(var accountId: String, var storeId: String, var orderId: String) : ViewModel() {

    private val db = FireStoreRepository()

    private val choiceResults: MutableLiveData<List<Choice>> by lazy {
        MutableLiveData<List<Choice>>().also {
            loadChoiceResult()
        }
    }

    fun getChoiceRequests() : LiveData<List<Choice>> {
        return choiceResults;
    }

    private fun loadChoiceResult() {

    }


}