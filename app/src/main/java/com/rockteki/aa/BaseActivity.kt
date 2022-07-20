package com.rockteki.aa

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.rockteki.aa.model.Account
import com.rockteki.aa.model.FireStoreRepository
import kotlinx.coroutines.*


open class BaseActivity : AppCompatActivity() {
    var firebaseuser: FirebaseUser? = null;
    var db = FireStoreRepository()
    var account: Account? = null;


    override fun onStart() {
        Log.d("xx: Base", "onstart")
        GlobalScope.launch {
            firebaseuser = getFirebaseUserTaskAsync().await();
            if(firebaseuser == null) {
                Log.d("xx", "userisnull")
            }
            firebaseuser?.uid?.let { uid ->
                db.getMyAccount(uid) { account = it
                    Log.d("xx", account.toString())
                }
            }
        }
        super.onStart()
    }


    private fun getFirebaseUserTaskAsync() : Deferred<FirebaseUser?> =
        GlobalScope.async(Dispatchers.IO) {
            return@async FirebaseAuth.getInstance().currentUser
        }
}