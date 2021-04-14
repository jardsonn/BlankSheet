package com.jcs.blanksheet.dialog.sharedpreference

import android.content.SharedPreferences
import androidx.lifecycle.LiveData


/**
 * Created by Jardson Costa on 13/04/2021.
 */

abstract class SharedPreferenceLiveData<T>(
    val prefs: SharedPreferences,
    val key: String,
    val defValue: T
) : LiveData<T>() {

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, mKey ->
            if (key == mKey) {
                value = getValueFromPreferences(key, defValue = defValue)
            }
        }

    abstract fun getValueFromPreferences(key: String?, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(key, defValue = defValue)
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        prefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }


    open fun getString(
        key: String,
        defaultValue: String
    ): SharedPreferenceStringLiveData {
        return SharedPreferenceStringLiveData(prefs, key, defaultValue)
    }


    class SharedPreferenceStringLiveData(
        pref: SharedPreferences,
        key: String, defValue: String
    ) :
        SharedPreferenceLiveData<String>(pref, key, defValue) {
        override fun getValueFromPreferences(key: String?, defValue: String): String {
            return prefs.getString(key, defValue)!!
        }
    }


}