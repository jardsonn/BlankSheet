package com.jcs.blanksheet.interfaces

import com.jcs.blanksheet.entity.Document

/**
 * Created by Jardson Costa on 22/03/2021.
 */

class EventClick {
    
    interface OnItemClickListener {
        fun onItemClick(position: Int, doc: Document)
    }
    
    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int, doc: Document)
    }
    
}