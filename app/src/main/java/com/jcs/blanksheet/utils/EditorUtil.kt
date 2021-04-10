package com.jcs.blanksheet.utils
import android.content.Context
import android.content.SharedPreferences
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import com.jcs.blanksheet.R
import java.util.*

/**
 * Created by Jardson Costa on 23/03/2021.
 */

class EditorUtil(private val mEditText: EditText) {
    private val mEditorHistory: EditorHistory

    private val mEditTextChangeListener: EditTextChangeListener

    private var mIsUndoOrRedo = false

    private var menuItemUndo: MenuItem? = null

    private var menuItemRedo: MenuItem? = null

    private var menuItemSave: MenuItem? = null

    fun textUndoRedoObserver(menu: Menu?) {
        textRedoObserver(menu)
        textUndoObserver(menu)
    }

    private fun textUndoObserver(menu: Menu?) {
        menuItemUndo = menu?.findItem(R.id.item_menu_edit_undo)
        menuItemSave = menu?.findItem(R.id.item_menu_edit_save)
        changeMenuIconColor(mEditText.context, menuItemUndo)
        changeMenuIconColor(mEditText.context, menuItemSave)
        mEditText.addTextChangedListener { s ->
            menuItemUndo?.isEnabled = availableToUndo()
            changeMenuIconColor(mEditText.context, menuItemUndo)
            if (s!!.isEmpty()) {
                menuItemSave?.isEnabled = false
            } else {
                menuItemSave?.isEnabled = availableToUndo()
            }
            changeMenuIconColor(mEditText.context, menuItemSave)
        }
    }

    private fun textRedoObserver(menu: Menu?) {
        menuItemRedo = menu?.findItem(R.id.item_menu_edit_redo)
        menuItemSave = menu?.findItem(R.id.item_menu_edit_save)
        changeMenuIconColor(mEditText.context, menuItemRedo)
        changeMenuIconColor(mEditText.context, menuItemSave)
        mEditText.addTextChangedListener { s ->
            menuItemRedo?.isEnabled = availableToRedo()
            changeMenuIconColor(mEditText.context, menuItemRedo)
            if (s!!.isEmpty()) {
                menuItemSave?.isEnabled = false
            } else {
                menuItemSave?.isEnabled = availableToRedo()
            }
            changeMenuIconColor(mEditText.context, menuItemSave)
        }
    }

    fun clearHistory() {
        mEditorHistory.clear()
        true.changeMenuIconColor(mEditText.context, menuItemRedo)
        true.changeMenuIconColor(mEditText.context, menuItemUndo)
        true.changeMenuIconColor(mEditText.context, menuItemSave)
    }

    private fun Boolean.changeMenuIconColor(context: Context?, menuItem: MenuItem?) {
        menuItem?.isEnabled = !this
        this@EditorUtil.changeMenuIconColor(context, menuItem)
    }

    private fun changeMenuIconColor(context: Context?, menuItem: MenuItem?) {
        menuItem?.icon?.let {
            if (menuItem.isEnabled) {
                DrawableCompat.setTint(
                    it,
                    ContextCompat.getColor(context!!, R.color.appbar_color_icon)
                )
            } else {
                DrawableCompat.setTint(
                    it,
                    ContextCompat.getColor(
                        context!!,
                        R.color.appbar_color_disabled
                    )
                )
            }
        }
    }

    fun setMaxHistorySize(maxHistorySize: Int) {
        mEditorHistory.setMaxHistorySize(maxHistorySize)
    }

    fun disconnect() {
        mEditText.removeTextChangedListener(mEditTextChangeListener)
    }

    private fun availableToUndo(): Boolean {
        return mEditorHistory.mPosition > 0
    }

    private fun availableToRedo(): Boolean {
        return mEditorHistory.mPosition < mEditorHistory.mHistory.size
    }

    fun undo() {
        val edit: EditorItem = mEditorHistory.previous ?: return
        val text = mEditText.editableText
        val start = edit.mStart
        val end = start + if (edit.mAfter != null) edit.mAfter.length else 0
        mIsUndoOrRedo = true
        text.replace(start, end, edit.mBefore)
        mIsUndoOrRedo = false
        for (o in text.getSpans(0, text.length, UnderlineSpan::class.java)) {
            text.removeSpan(o)
        }
        Selection.setSelection(
            text,
            if (edit.mBefore == null) start else start + edit.mBefore.length
        )
    }

    fun redo() {
        val edit: EditorItem = mEditorHistory.next ?: return
        val text = mEditText.editableText
        val start = edit.mStart
        val end = start + if (edit.mBefore != null) edit.mBefore.length else 0
        mIsUndoOrRedo = true
        text.replace(start, end, edit.mAfter)
        mIsUndoOrRedo = false
        for (o in text.getSpans(0, text.length, UnderlineSpan::class.java)) {
            text.removeSpan(o)
        }
        Selection.setSelection(text, if (edit.mAfter == null) start else start + edit.mAfter.length)
    }

    fun storePersistentState(editor: SharedPreferences.Editor, prefix: String) {
        editor.putString(prefix + HASH_KEY, mEditText.text.toString().hashCode().toString())
        editor.putInt(prefix + MAX_SIZE_KEY, mEditorHistory.mMaxHistorySize)
        editor.putInt(prefix + POSITION_KEY, mEditorHistory.mPosition)
        editor.putInt(prefix + SIZE_KEY, mEditorHistory.mHistory.size)
        for ((i, ei) in mEditorHistory.mHistory.withIndex()) {
            val pre = "$prefix.$i"
            editor.putInt(pre + START_KEY, ei.mStart)
            editor.putString(pre + BEFORE_KEY, ei.mBefore.toString())
            editor.putString(pre + AFTER_KEY, ei.mAfter.toString())
        }
    }

    @Throws(IllegalStateException::class)
    fun restorePersistentState(sp: SharedPreferences, prefix: String): Boolean {
        val ok = doRestorePersistentState(sp, prefix)
        if (!ok) {
            mEditorHistory.clear()
        }
        return ok
    }

    private fun doRestorePersistentState(sp: SharedPreferences, prefix: String): Boolean {
        val hash = sp.getString(prefix + HASH_KEY, null) ?: return true
        if (hash.toInt() != mEditText.text.toString().hashCode()) {
            return false
        }
        mEditorHistory.clear()
        mEditorHistory.mMaxHistorySize = sp.getInt(prefix + MAX_SIZE_KEY, -1)
        val count = sp.getInt(prefix + SIZE_KEY, -1)
        if (count == -1) {
            return false
        }
        for (i in 0 until count) {
            val pre = "$prefix.$i"
            val start = sp.getInt(pre + START_KEY, -1)
            val before = sp.getString(pre + BEFORE_KEY, null)
            val after = sp.getString(pre + AFTER_KEY, null)
            if (start == -1 || before == null || after == null) {
                return false
            }
            mEditorHistory.add(EditorItem(start, before, after))
        }
        mEditorHistory.mPosition = sp.getInt(prefix + POSITION_KEY, -1)
        return mEditorHistory.mPosition != -1
    }

    private class EditorHistory {
        var mPosition = 0
        var mMaxHistorySize = -1
        val mHistory = LinkedList<EditorItem>()
        fun clear() {
            mPosition = 0
            mHistory.clear()
        }

        fun add(item: EditorItem) {
            while (mHistory.size > mPosition) {
                mHistory.removeLast()
            }
            mHistory.add(item)
            mPosition++
            if (mMaxHistorySize >= 0) {
                trimHistory()
            }
        }

        fun setMaxHistorySize(maxHistorySize: Int) {
            mMaxHistorySize = maxHistorySize
            if (mMaxHistorySize >= 0) {
                trimHistory()
            }
        }

        private fun trimHistory() {
            while (mHistory.size > mMaxHistorySize) {
                mHistory.removeFirst()
                mPosition--
            }
            if (mPosition < 0) {
                mPosition = 0
            }
        }

        val previous: EditorItem?
            get() {
                if (mPosition == 0) {
                    return null
                }
                mPosition--
                return mHistory[mPosition]
            }
        val next: EditorItem?
            get() {
                if (mPosition >= mHistory.size) {
                    return null
                }
                val item = mHistory[mPosition]
                mPosition++
                return item
            }
    }

    private class EditorItem(
        val mStart: Int,
        val mBefore: CharSequence?,
        val mAfter: CharSequence?
    )

    private inner class EditTextChangeListener : TextWatcher {
        private var mBeforeChange: CharSequence? = null
        private var mAfterChange: CharSequence? = null
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (mIsUndoOrRedo) {
                return
            }
            mBeforeChange = s.subSequence(start, start + count)
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (mIsUndoOrRedo) {
                return
            }
            mAfterChange = s.subSequence(start, start + count)
            mEditorHistory.add(EditorItem(start, mBeforeChange, mAfterChange))
        }

        override fun afterTextChanged(s: Editable) {}
    }

    companion object {
        private const val HASH_KEY = ".hash"
        private const val MAX_SIZE_KEY = ".maxSize"
        private const val POSITION_KEY = ".position"
        private const val SIZE_KEY = ".size"
        private const val START_KEY = ".start"
        private const val BEFORE_KEY = ".before"
        private const val AFTER_KEY = ".after"
    }

    init {
        mEditorHistory = EditorHistory()
        mEditTextChangeListener = EditTextChangeListener()
        mEditText.addTextChangedListener(mEditTextChangeListener)
    }
}