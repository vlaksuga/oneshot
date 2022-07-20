package com.rockteki.aa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.rockteki.aa.model.FireStoreRepository

class SignInActivity : AppCompatActivity() {

    private lateinit var accountNameInput: EditText;
    private lateinit var accountSearchHint: EditText;
    private lateinit var submitSignInBtn: Button;
    private var hintList: List<String> = arrayListOf();
    private val db = FireStoreRepository();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        accountNameInput = findViewById(R.id.sign_in_et_account_name);
        accountSearchHint = findViewById(R.id.sign_in_et_account_search_hint);
        submitSignInBtn = findViewById(R.id.sign_in_btn_submit);
        submitSignInBtn.setOnClickListener { if(isValidateForm()) submitSignIn() }
        App.prefs.fuid?.let { fuid -> updateAccount(fuid) }

    }

    private fun updateAccount(uid: String) {
        if(uid.isNotBlank()) {
            db.getMyAccount(uid) { account ->
                account?.let {
                    accountNameInput.setText(it.accountName);
                    val hashTagList = mutableListOf<String>();
                    it.accountSearchHint.forEach { hint ->
                        hashTagList.add("#$hint")
                    }
                    accountSearchHint.setText(hashTagList.joinToString(" "))
                }
            }
        }
    }

    private fun submitSignIn() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let {
            val newAccount: Map<String, Any?> = hashMapOf(
                "accountId" to it.uid,
                "accountName" to accountNameInput.text.toString(),
                "accountSearchHint" to hintList
            )
            db.setAccount(it.uid, newAccount).addOnSuccessListener {
                val introIntent = Intent(this, IntroActivity::class.java).apply {
                    this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(introIntent)
            }
        }
    }

    private fun isValidateForm(): Boolean {
        if(accountNameInput.text.isNullOrEmpty() || accountNameInput.text.isNullOrBlank()) {
            Toast.makeText(this, "사용자명을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        hintList = arrayListOf()
        val splits = accountSearchHint.text.toString().split("#");
        splits.forEach { hint ->
            if(hint.isNotBlank()) {
                (hintList as ArrayList<String>).add(hint.trim())
            }

        }
        if(hintList.isEmpty()) {
            Toast.makeText(this, "하나 이상의 해시태그를 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}