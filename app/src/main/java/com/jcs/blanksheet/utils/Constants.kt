package com.jcs.blanksheet.utils

import android.Manifest

/**
 * Created by Jardson Costa on 22/03/2021.
 */

object Constants {

    const val DURATION_MILLI: Int = 6000

    const val REQUEST_EXTERNAL_READ = 1

    const val REQUEST_CODE_RELOAD_LIST = 2
    const val RESULT_CODE_RELOAD_LIST = 3
    const val RESULT_RELOAD_LIST_KEY = "reload_list"

    const val DOCUMENT_ID = "doc_id"
    const val JOTTER_DATA = "jotter_data"
    const val TITLE_KEY = "title"
    const val CONTENT_KEY = "content"
    const val ANIMATION_CONTAINER_KEY = "animation_container"

    const val SORT_BY = "sort_by"
    const val RADIO_BUTTON = "radio_button_sort"
    const val SORT_BY_NAME_AZ = "sort_by_name_az"
    const val SORT_BY_NAME_ZA = "sort_by_name_za"
    const val SORT_BY_DATE_RECENT = "sort_by_date_recent"
    const val SORT_BY_DATE_OLDEST = "sort_by_date_oldest"

    const val VIEW_GRID = 1
    const val VIEW_LIST = 2

    const val REQUEST_EXTERNAL_STORAGE = 1
    val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

}