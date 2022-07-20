package com.rockteki.aa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rockteki.aa.model.adapters.StoreListAdapter
import com.rockteki.aa.model.adapters.MyAccountGroupListAdapter
import com.rockteki.aa.model.Store
import com.rockteki.aa.model.MyAccountGroup
import com.rockteki.aa.model.FireStoreRepository
import java.util.*

class AddChoiceRequestActivity : AppCompatActivity() {

    private var selectedGroupId: String? = null;
    private var selectedStoreId: String? = null;
    private var selectedStoreName: String? = null;
    private lateinit var selectGroupResult: TextView;
    private lateinit var selectStoreResult: TextView;
    private val db = FireStoreRepository();
    private lateinit var accountId : String
    private lateinit var accountName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_choice_request);

        App.prefs.fuid?.let { fuid -> accountId = fuid }
        App.prefs.accountName?.let { name -> accountName = name }
        selectGroupResult = findViewById(R.id.add_choice_request_card_select_group_result);
        selectStoreResult = findViewById(R.id.add_choice_request_card_select_store_result);

        val closeBtn: ImageView = findViewById(R.id.add_choice_request_btn_close);
            closeBtn.setOnClickListener { super.onBackPressed(); }

        val submitBtn: Button = findViewById(R.id.add_choice_request_btn_submit);
            submitBtn.setOnClickListener { if(isValidateForm()) submitAddRequest(); }

        val selectGroupCardView: CardView = findViewById(R.id.cardView_select_account_group_from_add_choice_request);
            selectGroupCardView.setOnClickListener { showMyAccountGroupDialog(); }

        val selectStoreCardView: CardView = findViewById(R.id.cardView_select_store_from_add_choice_request);
            selectStoreCardView.setOnClickListener { showStoreDialog();}
    }

    private fun showMyAccountGroupDialog() {
        val builder = AlertDialog.Builder(this);
        val view = layoutInflater.inflate(R.layout.dialog_select_my_account_group, null, false);
            builder.setView(view);
        val dialog = builder.create();
        val dialogClose: ImageView = view.findViewById(R.id.dialog_select_my_account_group_close);
            dialogClose.setOnClickListener { dialog.dismiss(); }
            updateMyAccountGroupList(view, dialog);
            dialog.show()
    }

    private fun showStoreDialog() {
        val builder = AlertDialog.Builder(this);
        val view = layoutInflater.inflate(R.layout.dialog_select_store, null, false);
            builder.setView(view);
        val dialog = builder.create();
        val dialogClose: ImageView = view.findViewById(R.id.dialog_select_store_close);
            dialogClose.setOnClickListener { dialog.dismiss(); }
            updateStoreList(view, dialog);
            dialog.show();
    }


    private fun isValidateForm(): Boolean {
        if(selectedGroupId.isNullOrEmpty()) {
            Toast.makeText(this, "그룹을 선택해 주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(selectedStoreId.isNullOrEmpty()) {
            Toast.makeText(this, "매장을 선택해 주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private fun submitAddRequest() {
        val now = System.currentTimeMillis();
        val orderId = "order_${accountName}_$now"
        val newOrder: Map<String, Any?> = hashMapOf(
            "orderId" to orderId,
            "createUserid" to accountId,
            "createOwnerName" to accountName,
            "createDate" to now,
            "expireDate" to now + 86000
        )
        selectedStoreId?.let { storeId ->
            db.setOrder(storeId, orderId, newOrder)
                .addOnSuccessListener { createChoiceRequests(orderId) }
        }

    }

    private fun createChoiceRequests(orderId: String) {
        selectedGroupId?.let { groupId ->
            db.listMemberOf(accountId, groupId) { accountList ->
                val accountIdList = mutableListOf(accountId)
                for(account in accountList) {
                    accountIdList.add(account.accountId);
                }
                val now = System.currentTimeMillis();
                val newRequest : Map<String, Any?> = hashMapOf(
                    "storeOrderId" to orderId,
                    "choiceRequestId" to "req_$now",
                    "createDate" to now,
                    "expireDate" to now + 86000,
                    "createUserId" to accountId,
                    "createUserName" to accountName,
                    "storeName" to selectedStoreName,
                    "storeId" to selectedStoreId,
                    "requestAccountIdList" to accountIdList,
                    "isOpen" to true
                );
                for(accountId in accountIdList) {
                    db.setChoiceRequest(accountId, "req_$now", newRequest).addOnSuccessListener {
                        accountIdList.remove(accountId)
                        if(accountIdList.size <= 0) {
                            val mainIntent = Intent(this, MainActivity::class.java).apply {
                                this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(mainIntent)
                        }
                    }
                }
            }
        }
    }

    private fun updateMyAccountGroupList(view: View, dialog: AlertDialog) {
        db.listMyAccountGroupOf(accountId) { myAccountGroupList -> renderMyAccountGroupList(myAccountGroupList, view, dialog) }
    }

    private fun updateStoreList(view: View, dialog: AlertDialog) {
        db.listStore { storeList -> renderStoreList(storeList, view, dialog) }
    }

    private fun renderMyAccountGroupList(myAccountGroupList: List<MyAccountGroup>, view: View, dialog: AlertDialog) {
        val myUserGroupListAdapter = MyAccountGroupListAdapter(this, myAccountGroupList);
        myUserGroupListAdapter.setOnItemClickListener(object : MyAccountGroupListAdapter.OnItemClickListener {
            override fun onItemClick(myAccountGroup: MyAccountGroup) {
                with(myAccountGroup) {
                    selectedGroupId = myAccountGroupId;
                    selectGroupResult.text = myAccountGroupName;
                }
                dialog.dismiss();
            }
        })
        val myUserGroupRecyclerView: RecyclerView = view.findViewById(R.id.rv_myaccountgrouplist_from_dialog_select_my_account_group);
        myUserGroupRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AddChoiceRequestActivity);
            setHasFixedSize(true);
            adapter = myUserGroupListAdapter;
        }
        val dialogEmpty: TextView = view.findViewById(R.id.dialog_select_my_account_group_empty)
        dialogEmpty.visibility = if(myAccountGroupList.isNotEmpty()) View.INVISIBLE else View.VISIBLE;
    }

    private fun renderStoreList(storeList: List<Store>, view: View, dialog: AlertDialog) {
        val storeListAdapter = StoreListAdapter(this, storeList);
            storeListAdapter.setOnItemClickListener(object : StoreListAdapter.OnItemClickListener {
            override fun onItemClick(store: Store) {
                with(store) {
                    selectedStoreId = storeId;
                    selectedStoreName = storeName;
                    selectStoreResult.text = storeName;
                }
                dialog.dismiss();
            }
            })
        val storeListRecyclerView: RecyclerView = view.findViewById(R.id.rv_storelist_from_dialog_select_store);
            storeListRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@AddChoiceRequestActivity)
                setHasFixedSize(true);
                adapter = storeListAdapter;
            }
        val dialogEmpty: TextView = view.findViewById(R.id.dialog_select_store_empty)
        dialogEmpty.visibility = if(storeList.isNotEmpty()) View.INVISIBLE else View.VISIBLE;
    }




}