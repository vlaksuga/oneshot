package com.rockteki.aa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rockteki.aa.model.Choice
import com.rockteki.aa.model.ChoiceRequest
import com.rockteki.aa.model.FireStoreRepository
import com.rockteki.aa.model.adapters.ChoiceListAdapter
import com.rockteki.aa.viewModel.ChoiceRequestViewModel
import com.rockteki.aa.viewModel.ChoiceResultViewModel
import java.io.Serializable
import java.util.*

class ChoiceResultActivity : AppCompatActivity() {
    private val db = FireStoreRepository();
    private lateinit var accountId : String
    private lateinit var choiceRequest: ChoiceRequest;
    private lateinit var emptyMsg: TextView;
    private lateinit var confirmBtn: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice_result);

        App.prefs.fuid?.let { accountId = it }

        emptyMsg = findViewById(R.id.choice_result_empty);
        confirmBtn = findViewById(R.id.choice_result_confirm);
        confirmBtn.setOnClickListener { goConfirmOrderActivity() }
        val backBtn: ImageView = findViewById(R.id.choice_result_back);
            backBtn.setOnClickListener { super.onBackPressed(); }
        val addBtn: ImageView = findViewById(R.id.choice_result_add);
            addBtn.setOnClickListener { addNewChoice() }
        intent.extras?.let {
            choiceRequest = it.get("choiceRequest") as ChoiceRequest
            confirmBtn.visibility = if(choiceRequest.createUserId == accountId) View.VISIBLE else View.GONE
        }
    }

    private fun goConfirmOrderActivity() {
        Intent(this, ConfirmOrderActivity::class.java).apply {
            putExtra("choiceRequest", choiceRequest as Serializable);
            startActivity(this);
        }
    }

    override fun onResume() {
        super.onResume();
        updateChoiceResult();
    }

    private fun addNewChoice() {
        Intent(this, ChoiceActivity::class.java).apply {
            putExtra("choiceRequest", choiceRequest as Serializable)
            startActivity(this);
        }
    }

    private fun updateChoiceResult() {
        db.listChoice(accountId, choiceRequest.storeId, choiceRequest.storeOrderId) { choiceList ->
            render(choiceList)
        }
    }

    private fun render(choiceList: List<Choice>) {
        val currentChoiceListAdapter = ChoiceListAdapter(this, choiceList);
            currentChoiceListAdapter.apply {
            setOnItemClickListener(object : ChoiceListAdapter.OnItemClickListener{
                override fun onItemClick(choice: Choice) {
                    showUpdateDialog(choice)
                }
            })
            setOnCloseClickListener(object : ChoiceListAdapter.OnCloseClickListener{
                override fun onCloseClick(choice: Choice) {
                    removeChoice(choice)
                }
            })
        }

        val currentChoiceListRecyclerView: RecyclerView = findViewById(R.id.rv_choicelist);
            currentChoiceListRecyclerView.apply {
            adapter = currentChoiceListAdapter;
            layoutManager = LinearLayoutManager(this@ChoiceResultActivity);
            setHasFixedSize(true);
        }

        emptyMsg.visibility = if(choiceList.isNotEmpty()) View.INVISIBLE else View.VISIBLE;
    }

    private fun removeChoice(choice: Choice) {
        db.deleteChoice(choiceRequest.storeId, choiceRequest.storeOrderId, choice.choiceId).addOnSuccessListener {
            updateChoiceResult();
        }
    }


    private fun showUpdateDialog(choice: Choice) {
        val builder = AlertDialog.Builder(this);
        val view = layoutInflater.inflate(R.layout.dialog_choice_option, null, false);
            builder.setView(view);
        val dialog = builder.create();
        val dialogTitle: TextView = view.findViewById(R.id.dialog_choice_option_title);
            dialogTitle.text = choice.choiceProductName + " - " + choice.choiceProductSize;
        val dialogClose: ImageView = view.findViewById(R.id.dialog_choice_option_close);
            dialogClose.setOnClickListener { dialog.dismiss(); }
        val countValue: TextView = view.findViewById(R.id.dialog_choice_option_count_value);
            countValue.text = choice.choiceQuantity.toString();
        val countPlus: ImageView = view.findViewById(R.id.dialog_choice_option_count_plus);
            countPlus.setOnClickListener { countValue.text = (countValue.text.toString().toInt() + 1).toString(); }
        val countMinus: ImageView = view.findViewById(R.id.dialog_choice_option_count_minus);
            countMinus.setOnClickListener { if(countValue.text.toString().toInt() > 0) { countValue.text = (countValue.text.toString().toInt() - 1).toString() }; }
        val optionMemo: EditText = view.findViewById(R.id.dialog_choice_option_memo);
            optionMemo.setText(choice.choiceMemo)
        val dialogSubmitBtn: Button = view.findViewById(R.id.dialog_choice_option_submit);
            dialogSubmitBtn.setOnClickListener {
            val updateChoice = mapOf<String, Any>(
                "choiceMemo" to optionMemo.text.toString(),
                "choiceQuantity" to countValue.text.toString().toInt()
            )
            updateStoreChoice(updateChoice, choice.choiceId, dialog)
        }
        val dialogDismissBtn: Button = view.findViewById(R.id.dialog_choice_option_dismiss);
            dialogDismissBtn.setOnClickListener { dialog.dismiss(); }
            dialog.show();
    }

    private fun updateStoreChoice(updateChoice: Map<String, Any>, choiceId : String, dialog: AlertDialog) {
        db.updateChoice(choiceRequest.storeId, choiceRequest.storeOrderId, choiceId, updateChoice)
            .addOnSuccessListener {
                updateChoiceResult();
                dialog.dismiss();
            }
    }
}