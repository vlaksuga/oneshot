package com.rockteki.aa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.toObject
import com.rockteki.aa.model.Choice
import com.rockteki.aa.model.ChoiceRequest
import com.rockteki.aa.model.FireStoreRepository
import com.rockteki.aa.model.adapters.ChoiceSummaryAdapter

class ConfirmOrderActivity : AppCompatActivity() {

    private val db = FireStoreRepository()
    private lateinit var accountId: String
    private lateinit var storeName: TextView
    private lateinit var submitBtn: Button
    private lateinit var choiceRequest: ChoiceRequest
    private var currentChoiceSummaryList: MutableList<Choice> = mutableListOf();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order)
        App.prefs.fuid?.let { accountId = it }
        intent.extras?.let { choiceRequest = it.get("choiceRequest") as ChoiceRequest }
        storeName = findViewById(R.id.confirm_order_store_name)
        storeName.text = choiceRequest.storeName;
        submitBtn = findViewById(R.id.confirm_order_btn_submit);
        submitBtn.setOnClickListener { confirmOrder() }
        attachSnapshotListeners()
    }

    private fun attachSnapshotListeners() {
        db.onChoiceChange(choiceRequest.storeId, choiceRequest.storeOrderId) { value, error ->
            if(error != null) { return@onChoiceChange }
            updateChoiceSummary()
        }
    }

    private fun updateChoiceSummary() {
        currentChoiceSummaryList = mutableListOf();
        db.listChoiceAll(choiceRequest.storeId, choiceRequest.storeOrderId) {
            for(doc in it.documents) {
                    doc.toObject<Choice>()?.let { newChoice ->
                        for(oldChoice in currentChoiceSummaryList) {
                            if(oldChoice.choiceProductId == newChoice.choiceProductId) {
                                oldChoice.choiceQuantity = oldChoice.choiceQuantity + newChoice.choiceQuantity;
                                return@let
                            }
                        }
                        currentChoiceSummaryList.add(newChoice);
                        return@let
                    }
                }
                render()
        }
    }

    private fun render() {
        val choiceSummaryAdapter = ChoiceSummaryAdapter(this, currentChoiceSummaryList);
        val choiceSummaryRecyclerView: RecyclerView = findViewById(R.id.rv_choicesummarylist_for_confirm);
        choiceSummaryRecyclerView.apply {
            adapter = choiceSummaryAdapter;
            layoutManager = LinearLayoutManager(this@ConfirmOrderActivity)
            setHasFixedSize(true);
        }
        val totalPrice: TextView = findViewById(R.id.confirm_order_total_price);
        totalPrice.apply {
            if(currentChoiceSummaryList.size > 0) View.VISIBLE else View.INVISIBLE;
            text = getTotalPrice(currentChoiceSummaryList)
        }

        val emptyTextView: TextView = findViewById(R.id.confirm_order_choice_list_empty);
            emptyTextView.visibility = if(currentChoiceSummaryList.size > 0) View.INVISIBLE else View.VISIBLE;
    }

    private fun getTotalPrice(choiceList: MutableList<Choice>): String {
        if(choiceList.size <= 0) { return "0"}
        var ret = 0
        for(choice in choiceList) {
            ret += (choice.choiceQuantity * choice.choiceProductPrice);
        }
        return  "총 금액 : " + "%,d".format(ret) + "원";
    }

    private fun confirmOrder() {
        db.closeOrder(choiceRequest.storeId, choiceRequest.storeOrderId, mapOf("isOpen" to false)).addOnSuccessListener {
            closeChoiceRequests()
        }
    }

    private fun closeChoiceRequests() {
        var requestTotal = choiceRequest.requestAccountIdList.size;
        Log.d("xx :", choiceRequest.requestAccountIdList.toString())
        for(requestAccountId in choiceRequest.requestAccountIdList) {
            Log.d("xx :", requestAccountId)
            db.closeChoiceRequest(requestAccountId, choiceRequest.choiceRequestId, mapOf("isOpen" to false))
                .addOnSuccessListener {
                    requestTotal -= 1
                    if(requestTotal <= 0 ) {
                        Intent(this, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(this)
                        }
                    }
                }
        }
    }
}