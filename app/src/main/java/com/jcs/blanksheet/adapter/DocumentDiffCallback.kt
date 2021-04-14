package com.jcs.blanksheet.adapter

import androidx.recyclerview.widget.DiffUtil
import com.jcs.blanksheet.model.Document

/**
 * Created by Jardson Costa on 13/04/2021.
 */

class DocumentDiffCallback: DiffUtil.ItemCallback<Document>() {

//    override fun getOldListSize(): Int {
//        return oldDocumentList.size
//    }
//
//    override fun getNewListSize(): Int {
//        return newDocumentList.size
//    }
//
//
//    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//        return oldDocumentList[oldItemPosition].id == newDocumentList[newItemPosition].id
//    }
//
//    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//        val oldList = oldDocumentList[oldItemPosition]
//        val newList = oldDocumentList[oldItemPosition]
//        return oldList.title == newList.title;
//    }
//
//    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
//        // Implemente o método se você for usar o ItemAnimator
//        return super.getChangePayload(oldItemPosition, newItemPosition)
//    }

    override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
        TODO("Not yet implemented")
    }

    override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
        TODO("Not yet implemented")
    }

}