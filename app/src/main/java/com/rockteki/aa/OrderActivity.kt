package com.rockteki.aa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.toObject
import com.rockteki.aa.model.Choice
import com.rockteki.aa.model.ChoiceRequest
import com.rockteki.aa.model.FireStoreRepository
import com.rockteki.aa.model.adapters.ChoiceSummaryAdapter

class OrderActivity : AppCompatActivity() {

    private val db = FireStoreRepository();
    private lateinit var choiceRequest: ChoiceRequest
    private var currentChoiceSummaryList: MutableList<Choice> = mutableListOf();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        val titleTextView :TextView = findViewById(R.id.order_store_title);
        intent.extras?.let {
            choiceRequest = it.get("choiceRequest") as ChoiceRequest
            titleTextView.text = choiceRequest.storeName;
        }

        val backBtn: ImageView = findViewById(R.id.order_back);
        backBtn.setOnClickListener { super.onBackPressed(); }
        updateChoiceSummary()
    }


    private fun updateChoiceSummary() {
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
        val choiceSummaryRecyclerView: RecyclerView = findViewById(R.id.rv_choicesummarylist);
        choiceSummaryRecyclerView.apply {
            adapter = choiceSummaryAdapter;
            layoutManager = LinearLayoutManager(this@OrderActivity)
            setHasFixedSize(true);
        }
        val totalPrice: TextView = findViewById(R.id.order_total_price);
            totalPrice.apply {
                if(currentChoiceSummaryList.size > 0) View.VISIBLE else View.INVISIBLE;
                text = getTotalPrice(currentChoiceSummaryList)
            }

        val emptyTextView: TextView = findViewById(R.id.order_summary_empty);
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
}