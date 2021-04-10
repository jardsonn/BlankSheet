package com.jcs.blanksheet.callbacks

import android.view.View
import com.google.android.material.card.MaterialCardView
import com.jcs.blanksheet.model.Document

/**
 * Created by Jardson Costa on 22/03/2021.
 */

class EventClick {

    interface OnClickListener{
        fun onDocumentClick(position: Int, document: Document, cardView: MaterialCardView)
    }

    interface OnLongClickListener{
        fun onDocumentLongClick(position: Int, document: Document)
    }

}