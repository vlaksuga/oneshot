package com.rockteki.aa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.rockteki.aa.model.FireStoreRepository


class IntroActivity : AppCompatActivity() {
    private lateinit var mainIntent: Intent;
    private lateinit var signInIntent: Intent;
    private lateinit var loginBtn: SignInButton;
    private var firebaseUser: FirebaseUser? = null;
    private val db = FireStoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro);

        loginBtn = findViewById(R.id.google_login_btn);
            loginBtn.setOnClickListener {
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build()
                signInLauncher.launch(signInIntent)
            }

        mainIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        signInIntent = Intent(this, SignInActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }

    private val providers = arrayListOf( AuthUI.IdpConfig.GoogleBuilder().build() )


    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }



    private fun onSignInResult(res: FirebaseAuthUIAuthenticationResult?) {
        if (res?.resultCode == RESULT_OK) {
            loginWithFirebaseUser()
        } else {
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private fun loginWithFirebaseUser() {
        firebaseUser = FirebaseAuth.getInstance().currentUser;
        if(firebaseUser != null) {
            App.prefs.fuid = firebaseUser!!.uid;
            db.getMyAccount(firebaseUser!!.uid) { account ->
                account?.let {
                    App.prefs.accountName = account.accountName;
                    startActivity(mainIntent)
                    return@getMyAccount
                }
                emptyPrefs();
                startActivity(signInIntent);
            }
        } else {
            loginBtn.visibility = View.VISIBLE;
        }
    }

    private fun emptyPrefs() {
        App.prefs.accountName = "";
    }


    override fun onStart() {
        super.onStart()
        loginWithFirebaseUser();
    }
}