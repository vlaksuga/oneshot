package com.rockteki.aa

import android.content.Context
import android.content.SharedPreferences

class AppSharedPreferences(context: Context) {
    companion object {
        const val PREFS_FILE_NAME = "prefs";
    }
    private val preferences : SharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, 0);

    var fuid : String?
        get() = preferences.getString("fuid", "")
        set(uid) { preferences.edit().putString("fuid", uid).apply() }

    var accountName : String?
        get() = preferences.getString("accountName", "")
        set(username) { preferences.edit().putString("accountName", username).apply() }

}