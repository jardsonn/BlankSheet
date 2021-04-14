package com.jcs.blanksheet.dialog.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import com.jcs.blanksheet.dialog.sharedpreference.SharedPreferenceLiveData.SharedPreferenceStringLiveData
import com.jcs.blanksheet.utils.Constants


/**
 * Created by Jardson Costa on 14/04/2021.
 */

class PreferenceManager(val context: Context) {
    private var sharedPreferenceLiveData: SharedPreferenceStringLiveData? = null

    fun getSharedPrefs(): SharedPreferenceStringLiveData? {
        return sharedPreferenceLiveData
    }

    fun setSharedPreferences(key: String, value: String) {
        val userDetails: SharedPreferences = context.getSharedPreferences(
            Constants.SORT_BY,
            Context.MODE_PRIVATE
        )
        val editor = userDetails.edit()
        editor.putString(key, value)
        editor.apply()
        sharedPreferenceLiveData = SharedPreferenceStringLiveData(userDetails, key, value)
    }
}