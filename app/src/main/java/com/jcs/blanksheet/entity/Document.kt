package com.jcs.blanksheet.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable


/**
 * Created by Jardson Costa on 04/04/2021.
 */

@Entity(tableName = "main_table")
data class Document(
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "content")
    var content: String,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "dateForOrder")
    var dateForOrder: String
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
    
    @Ignore
    var isChecked: Boolean = false
    
    @Ignore
    override fun toString(): String {
        return "Document {" +
                "id = $id,\n" +
                "title = $title,\n" +
                "content = $content,\n" +
                "date = $date,\n" +
                "dateForOrder = $dateForOrder\n" +
                "}"
    }
    
    @Ignore
    override fun hashCode(): Int = id.times(31).plus(title.hashCode()).toInt()
    
    @Ignore
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Document) return false
        val doc: Document = other
        return title == doc.title
    }
}