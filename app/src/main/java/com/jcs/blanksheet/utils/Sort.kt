package com.jcs.blanksheet.utils

import com.jcs.blanksheet.utils.Constants.BY_DATE_OLDEST
import com.jcs.blanksheet.utils.Constants.BY_DATE_RECENT
import com.jcs.blanksheet.utils.Constants.BY_NAME_AZ
import com.jcs.blanksheet.utils.Constants.BY_NAME_ZA

/**
 * Created by Jardson Costa on 13/04/2021.
 */

enum class Sort {
    BY_NAME_ASC, BY_NAME_DESC, BY_DATE_ASC, BY_DATE_DESC;

    companion object {
        fun get(index: Int): String {
            return when (index) {
                BY_NAME_AZ -> {
                    BY_NAME_ASC.toString()
                }
                BY_NAME_ZA -> {
                    BY_NAME_DESC.toString()
                }
                BY_DATE_RECENT -> {
                    BY_DATE_ASC.toString()
                }
                BY_DATE_OLDEST -> {
                    BY_DATE_DESC.toString()
                }
                else -> BY_NAME_ASC.toString()
            }
        }
    }
}