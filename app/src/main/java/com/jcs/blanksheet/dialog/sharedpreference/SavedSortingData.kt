package com.jcs.blanksheet.dialog.sharedpreference

import android.content.SharedPreferences
import androidx.lifecycle.LiveData


/**
 * Created by Jardson Costa on 13/04/2021.
 */

abstract class SavedSortingData<T>(
    val prefs: SharedPreferences,
    private val key: String,
    private val defValue: T
) : LiveData<T>() {

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _ , mKey ->
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


    open fun getOrder(
        key: String,
        defaultValue: String
    ): SavedSortingDataLiveData {
        return SavedSortingDataLiveData(prefs, key, defaultValue)
    }


    class SavedSortingDataLiveData(
        pref: SharedPreferences,
        key: String, defValue: String
    ) :
        SavedSortingData<String>(pref, key, defValue) {
        override fun getValueFromPreferences(key: String?, defValue: String): String {
            return prefs.getString(key, defValue)!!
        }
    }


}