package com.rockteki.aa.mainFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.rockteki.aa.App
import com.rockteki.aa.R
import com.rockteki.aa.SignInActivity
import com.rockteki.aa.model.Account

class ConfigFragment : Fragment() {

    private val db = Firebase.firestore;
    private lateinit var root: View;
    private lateinit var accountId: String;
    private lateinit var currentAccount: Account
    private lateinit var accountNameTextView: TextView;
    private lateinit var accountTagTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_config, container, false);
        App.prefs.fuid?.let { accountId = it }
        accountNameTextView = root.findViewById(R.id.fm_config_my_account_name);
        accountTagTextView = root.findViewById(R.id.fm_config_my_account_tag);
        val editBtn: ImageView = root.findViewById(R.id.fm_config_btn_edit_account);
        editBtn.setOnClickListener {
            context?.let { context ->
                Intent(context, SignInActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        updateAccount();
    }

    private fun updateAccount() {
        db
            .collection("accounts")
            .document(accountId)
            .get()
            .addOnSuccessListener { doc ->
                doc.toObject<Account>()?.let { account -> currentAccount = account }
                render()
            }
    }

    private fun render() {
        accountNameTextView.text = currentAccount.accountName;
        val hashTagList = mutableListOf<String>();
        currentAccount.accountSearchHint.forEach { hint ->
            hashTagList.add("#$hint")
        }
        accountTagTextView.text = hashTagList.joinToString(", ");
    }
}