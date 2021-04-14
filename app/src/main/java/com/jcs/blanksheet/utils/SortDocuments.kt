package com.jcs.blanksheet.utils

import android.content.Context
import com.jcs.blanksheet.model.Document
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by Jardson Costa on 13/04/2021.
 */

class SortDocuments(val context: Context, documents: List<Document>) {
    private val mPreferences =
        context.getSharedPreferences(Constants.RADIO_BUTTON, Context.MODE_PRIVATE)
    val sortBy = mPreferences.getString(Constants.SORT_BY, Constants.SORT_BY_NAME_AZ)

}