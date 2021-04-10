package com.jcs.blanksheet.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

/**
 * Created by Jardson Costa on 04/04/2021.
 */

@Entity(tableName = "main_table")
data class Document(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "content")
    var content: String,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "dateForOrder")
    var dateForOrder: String
){
    @Ignore
    var isChecked: Boolean = false
}