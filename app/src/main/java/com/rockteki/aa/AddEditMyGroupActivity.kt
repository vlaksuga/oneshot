package com.rockteki.aa


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.rockteki.aa.model.Account
import com.rockteki.aa.model.MyAccountGroup
import com.rockteki.aa.model.adapters.AccountListAdapter
import java.util.*

class AddEditMyGroupActivity : AppCompatActivity() {

    private lateinit var accountListAdapter : AccountListAdapter;
    private lateinit var currentUserListRecyclerView : RecyclerView;
    private lateinit var searchResultAdapter : AccountListAdapter
    private lateinit var searchResultListRecyclerView : RecyclerView;
    private lateinit var groupNameInput: EditText;
    private lateinit var accountId: String
    private var groupId: String? = null;
    private var addedAccountList : MutableList<Account> = arrayListOf();
    private var searchResultList : MutableList<Account> = arrayListOf();
    private lateinit var accountListEmptyMsg: TextView;
    private lateinit var dialogSearchResultEmptyMsg: TextView;
    private var isEditMode: Boolean = false;
    private val db = Firebase.firestore;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_my_group)

        App.prefs.fuid?.let { accountId = it }

        val titleTextView: TextView = findViewById(R.id.add_group_title)
        val backBtn: ImageView = findViewById(R.id.add_group_back);
            backBtn.setOnClickListener { super.onBackPressed(); }
        val addAccountBtn: ImageView = findViewById(R.id.add_group_btn_add_user);
            addAccountBtn.setOnClickListener { showAddAccountDialog(); }
        val submitBtn: Button = findViewById(R.id.add_group_btn_submit);
            submitBtn.setOnClickListener { if(isValidateForm()) { submitAddEditGroup(); } }
        val cancelBtn: Button = findViewById(R.id.add_group_btn_cancel);
            cancelBtn.setOnClickListener { super.onBackPressed() }
        val deleteBtn: Button = findViewById(R.id.add_group_btn_delete);
            deleteBtn.setOnClickListener { deleteGroup() }

        groupNameInput = findViewById(R.id.add_group_et_group_name);
        accountListEmptyMsg = findViewById(R.id.add_group_empty_userlist);

        // For Edit
        if(intent.hasExtra("myAccountGroup")) {
            isEditMode = true;
            titleTextView.text = "그룹 편집"
            val myAccountGroup = intent.extras?.get("myAccountGroup") as MyAccountGroup;
            groupNameInput.setText(myAccountGroup.myAccountGroupName);
            groupId = myAccountGroup.myAccountGroupId;
            deleteBtn.visibility = View.VISIBLE;
            db
                .collection("accounts/${accountId}/myAccountGroups/${myAccountGroup.myAccountGroupId}/accounts")
                .get()
                .addOnSuccessListener {
                    for(doc in it.documents) {
                        doc.toObject<Account>()?.let { account -> addedAccountList.add(account) }
                    }
                    updateAddedAccountList();
                }
        } else {
            updateAddedAccountList();
        }
    }

    private fun deleteGroup() {
        groupId?.let { id ->
            db
                .collection("accounts/${accountId}/myAccountGroups")
                .document(id)
                .update("isAvailable", false)
                .addOnSuccessListener { finish() }
        }
    }

    private fun isValidateForm(): Boolean {
        if(groupNameInput.text.isBlank() || groupNameInput.text.isEmpty()) {
            Toast.makeText(this, "그룹이름을 입력하세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(addedAccountList.size <= 0) {
            Toast.makeText(this, "사용자를 추가하세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private fun updateAddedAccountList() {
        accountListAdapter = AccountListAdapter(this, addedAccountList);
        accountListAdapter.setOnItemClickListener(object : AccountListAdapter.OnItemClickListener{
            override fun onItemClick(account: Account) {
                val builder = AlertDialog.Builder(this@AddEditMyGroupActivity);
                builder.apply {
                    setTitle("사용자 제외");
                    setMessage(account.accountName + "를 제외할까요?");
                    setPositiveButton("확인") { _, _ ->
                        addedAccountList.remove(account);
                        updateAddedAccountList();
                        Toast.makeText(this@AddEditMyGroupActivity, account.accountName + "이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    setNegativeButton("취소") { _, _ -> }
                    show();
                }
            }
        })
        currentUserListRecyclerView = findViewById(R.id.rv_userlist);
        currentUserListRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AddEditMyGroupActivity);
            adapter = accountListAdapter;
            setHasFixedSize(true);
        }
        accountListEmptyMsg.visibility = if (addedAccountList.size > 0) View.GONE else View.VISIBLE
    }

    private fun submitAddEditGroup() {
        val now = System.currentTimeMillis();
        var newMyAccountGroup: Map<String, Any> = mapOf();
        if(isEditMode) { removeOldAccountGroup() }
        val newGroupId = "group_${now}"
        newMyAccountGroup = mapOf(
            "myAccountGroupId" to newGroupId,
            "myAccountGroupName" to groupNameInput.text.toString(),
            "myAccountGroupSize" to addedAccountList.size,
            "isAvailable" to true
        )
        setNewAccountGroup(newGroupId, newMyAccountGroup)
    }

    private fun removeOldAccountGroup() {
        groupId?.let { id ->
            db
                .collection("accounts/${accountId}/myAccountGroups")
                .document(id)
                .update("isAvailable", false)
                .addOnSuccessListener {  }
        }
    }

    private fun setNewAccountGroup(gid: String, newMyAccountGroup: Map<String, Any>) {
        db
            .collection("accounts/${accountId}/myAccountGroups")
            .document(gid)
            .set(newMyAccountGroup)
            .addOnSuccessListener {
                for(account in addedAccountList) {
                    val newAccount: Map<String, Any> = mapOf(
                        "accountId" to account.accountId,
                        "accountName" to account.accountName,
                        "accountSearchHint" to account.accountSearchHint
                    )
                    db
                        .collection("accounts/${accountId}/myAccountGroups/${gid}/accounts")
                        .document(account.accountId)
                        .set(newAccount)
                        .addOnSuccessListener { }
                }
                finish()
            }
    }

    private fun showAddAccountDialog() {
        val builder = AlertDialog.Builder(this);
        val view = layoutInflater.inflate(R.layout.dialog_find_user, null, false);
            builder.setView(view);
        val dialog = builder.create();
        val dialogClose: ImageView = view.findViewById(R.id.dialog_find_user_close);
            dialogClose.setOnClickListener { dialog.dismiss(); }
        val dialogSearch: EditText = view.findViewById(R.id.dialog_find_user_et_search);
        dialogSearch.addTextChangedListener {
            fetchSearchResult(it.toString(), view, dialog)
        }
        updateSearchResult(view, dialog)
        dialog.show();
    }

    private fun updateSearchResult(view: View, dialog: AlertDialog) {
        dialogSearchResultEmptyMsg = view.findViewById(R.id.dialog_find_user_empty);
        searchResultAdapter = AccountListAdapter(this, searchResultList);
        searchResultAdapter.setOnItemClickListener(object : AccountListAdapter.OnItemClickListener{
            override fun onItemClick(account: Account) {
                if(addUserToAddedUserList(account)) dialog.dismiss();
            }
        })
        searchResultListRecyclerView = view.findViewById(R.id.dialog_find_user_recyclerView);
        searchResultListRecyclerView.adapter = searchResultAdapter;
        searchResultListRecyclerView.layoutManager = LinearLayoutManager(this);
        searchResultListRecyclerView.setHasFixedSize(true);
        if(searchResultList.size > 0) { dialogSearchResultEmptyMsg.visibility = View.GONE }
        else { dialogSearchResultEmptyMsg.visibility = View.VISIBLE }
    }

    private fun fetchSearchResult(hint: String, view: View, dialog: AlertDialog) {
        db
            .collection("accounts")
            .whereArrayContains("accountSearchHint", hint)
            .get()
            .addOnSuccessListener {
                searchResultList.clear();
                for(doc in it.documents) {
                    if(doc.id != accountId) {
                        doc.toObject<Account>()?.let { user -> searchResultList.add(user) }
                    }
                }
                updateSearchResult(view, dialog);
            }
    }

    private fun addUserToAddedUserList(account: Account) : Boolean {
        for(addedAccount in addedAccountList) {
            if(account.accountId == addedAccount.accountId) {
                Toast.makeText(this, "이미 추가한 사용자입니다.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        addedAccountList.add(account);
        updateAddedAccountList();
        return true;
    }
}