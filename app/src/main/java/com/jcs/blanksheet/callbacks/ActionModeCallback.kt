package com.jcs.blanksheet.callbacks

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import com.jcs.blanksheet.R

/**
 * Created by Jardson Costa on 13/04/2021.
 */

abstract class ActionModeCallback: ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.menu_selection, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }
}