package com.jcs.blanksheet.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.CheckedTextView
import androidx.appcompat.widget.AppCompatCheckedTextView
import com.jcs.blanksheet.R


/**
 * Created by Jardson Costa on 05/04/2021.
 */

class JcsCheckedTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatCheckedTextView(context, attrs, defStyleAttr) {

    private var listener: OnCheckedChangeListener? = null

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        this.listener = listener
    }

    fun setOnCheckedChangeListener(listener: (textChecked: JcsCheckedTextView, isChecked: Boolean) -> Unit) {
        setOnCheckedChangeListener(object : OnCheckedChangeListener {
            override fun onCheckedChanged(textChecked: JcsCheckedTextView, isChecked: Boolean) {
                listener.invoke(textChecked, isChecked)
            }
        })
    }

    fun setCheckedState(v: View, whichCheckedViews: Array<CheckedTextView>) {
        val checkedTextViewTemp = v as CheckedTextView
        for (item in whichCheckedViews) item.isChecked = false
        when (v.id) {
            R.id.rb_dialog_by_name_az,
            R.id.rb_dialog_by_name_za,
            R.id.rb_dialog_by_date_recent,
            R.id.rb_dialog_by_date_oldest -> checkedTextViewTemp.isChecked = true
            else -> {
            }
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(textChecked: JcsCheckedTextView, isChecked: Boolean)
    }
}