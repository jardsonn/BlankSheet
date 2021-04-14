package com.jcs.blanksheet.utils

/**
 * Created by Jardson Costa on 13/04/2021.
 */

enum class Sort {
    BY_NAME_ASC, BY_NAME_DESC, BY_DATE_ASC, BY_DATE_DESC;

    companion object{
         fun get(index: Int): String? {
            return when(index){
                0 -> {BY_NAME_ASC.toString()}
                1 -> {BY_NAME_DESC.toString()}
                2 -> {BY_DATE_ASC.toString()}
                3 -> {BY_DATE_DESC.toString()}
                else -> null
            }
        }
    }
}