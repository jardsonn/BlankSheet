package com.jcs.blanksheet.ui

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import com.jcs.blanksheet.R
import com.jcs.blanksheet.db.DocumentDao
import com.jcs.blanksheet.db.DocumentDatabase
import com.jcs.blanksheet.model.Document
import com.jcs.blanksheet.utils.*
import com.jcs.blanksheet.utils.JcsAnimation.moveContainerEditor
import com.jcs.blanksheet.utils.JcsAnimation.moveContentTop
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*


/**
 * Created by Jardson Costa on 04/04/2021.
 */

class EditorActivity : AppCompatActivity() {

    private lateinit var textTitle: TextView

    private lateinit var textContent: TextView

    private lateinit var editTitle: EditText

    private lateinit var editContent: EditText

    private lateinit var scrollViewEditor: ScrollView

    private lateinit var rlEditContainer: RelativeLayout

    private lateinit var appBarLayout: AppBarLayout

    private lateinit var btnShowAppBar: ImageButton

    private lateinit var shadowActionMode: View

    private lateinit var documentDao: DocumentDao

    private var documentTemp: Document? = null

    private var actionMode: ActionMode? = null

    private var menu: Menu? = null

    private var showHideAppBar: ShowHideAppBar? = null

    private var editorUtil: EditorUtil? = null

    private var isPreviewMode = false

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        rlEditContainer = findViewById(R.id.relative_layout_edit_container)
        editTitle = findViewById(R.id.edit_title)
        textTitle = findViewById(R.id.text_title_from_editor)
        editContent = findViewById(R.id.edit_content)
        textContent = findViewById(R.id.text_content_from_editor)
        scrollViewEditor = findViewById(R.id.scroll_view_editor)
        shadowActionMode = findViewById(R.id.view_shadow_action_mode)

        btnShowAppBar = findViewById(R.id.btn_show_appbar)

        appBarLayout = findViewById(R.id.appbar_edit)

        val toolbar: Toolbar = findViewById(R.id.toolbar_edit)
        menu = toolbar.menu
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        lifecycleScope.launch {
            try {
                documentDao = DocumentDatabase.getInstance(this@EditorActivity).documentDao()
            } catch (e: Exception) {
                Log.e("Room database error: ", e.stackTraceToString())
            }
        }

        if (intent.extras != null) {
            val id = intent.extras!!.getInt(Constants.DOCUMENT_ID, 0)
            documentTemp = documentDao.getDocumentById(id)
            documentTemp?.let {
                editTitle.setText(it.title)
                editContent.setText(it.content)
            }
        }

        showHideAppBar = ShowHideAppBar(
            appBarLayout,
            btnShowAppBar,
            rlEditContainer
        )

        btnShowAppBar.setOnClickListener {
            showHideAppBar?.start()
        }

        editorUtil = EditorUtil(editTitle, editContent)

        editContent.addTextChangedListener { s ->
            // textContent.text = if (s!!.endsWith(" ")) s else s.insert(s.length, " ")
        }

        editTitle.addTextChangedListener { s ->
            //  textTitle.text = if (s!!.endsWith(" ")) s else s.insert(s.length, " ")
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            actionMode?.finish()
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            actionMode?.finish()
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun onActionModeStarted(mode: ActionMode?) {
        super.onActionModeStarted(mode)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            animateContentEditor(true)
            actionMode = mode
        }
    }

    override fun onActionModeFinished(mode: ActionMode?) {
        super.onActionModeFinished(mode)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            animateContentEditor(false)
            actionMode = null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        editorUtil?.textUndoRedoObserver(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_edit_undo -> {
                editorUtil?.undo()
            }
            R.id.item_menu_edit_redo -> {
                editorUtil?.redo()
            }
            R.id.item_menu_edit_preview -> {
                previewMarkdown(isPreviewMode, item)
            }
            R.id.item_menu_edit_save -> {
                saveContent()
            }
        }
        showHideAppBar?.start()
        return super.onOptionsItemSelected(item)
    }

    private fun previewMarkdown(preview: Boolean, item: MenuItem) {
        if (!preview) {
            editContent.visibility = View.GONE
            editTitle.visibility = View.GONE
            textTitle.apply {
                visibility = View.VISIBLE
                text = editTitle.text
            }
            textContent.apply {
                visibility = View.VISIBLE
                text = editContent.text
            }

            menu!!.findItem(item.itemId).apply {
                title = resources.getString(R.string.editar)
                icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_round_edit)
            }
//            for (i in 0 until menu!!.size){
//
//            }
            for (i in menu!!) {
                if (i != item) {
                    i.isEnabled = false
                    editorUtil!!.changeMenuIconColor(this, i)
                }
            }
            JcsUtils().hideKeyboard(this)
            isPreviewMode = true
        } else {
            editContent.visibility = View.VISIBLE
            editContent.requestFocus()
            editTitle.visibility = View.VISIBLE
            textTitle.apply {
                visibility = View.GONE
                text = " "
            }
            textContent.apply {
                visibility = View.GONE
                text = " "
            }

            menu?.findItem(item.itemId)?.apply {
                title = resources.getString(R.string.prever)
                icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_round_preview)
            }

            for (i in menu!!) {
                editorUtil!!.restoreMenu()
                editorUtil!!.changeMenuIconColor(this, i)
            }
            isPreviewMode = false
        }
    }
//    private fun previewMarkdown(preview: Boolean, item: MenuItem) {
//        if (!preview) {
//            textContent.visibility = View.VISIBLE
//            editContent.visibility = View.GONE
//            textTitle.visibility = View.VISIBLE
//            editTitle.visibility = View.GONE
//
////            val node = markwon?.parse(editContent.text.toString())
////            val markdown = markwon?.render(node!!)
////            markwon?.setParsedMarkdown(textContent, markdown!!)
//
//            menu?.findItem(item.itemId)?.apply {
//                title = resources.getString(R.string.editar)
//                icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_round_edit)
//            }
//            isPreviewMode = true
//        } else {
//            textContent.visibility = View.GONE
//            editContent.visibility = View.VISIBLE
//            textTitle.visibility = View.GONE
//            editTitle.visibility = View.VISIBLE
//            menu?.findItem(item.itemId)?.apply {
//                title = resources.getString(R.string.prever)
//                icon =
//                    ContextCompat.getDrawable(applicationContext, R.drawable.ic_round_preview)
//            }
//            isPreviewMode = false
//        }
//    }

    private fun saveContent() {
        if (!TextUtils.isEmpty(editTitle.text) || !TextUtils.isEmpty(editContent.text)) {
            var title = editTitle.text.toString()
            val content = editContent.text.toString()
            if (title.isEmpty()) {
                title = JcsUtils().getFormattedTitle(editContent)
                editTitle.setText(title)
            }
            val actualDate = JcsUtils().actualDate()
            val dateForOrder = JcsUtils().dateForOrder()

            if (documentTemp == null) {
                documentTemp = Document(
                    0,
                    title,
                    content,
                    actualDate,
                    dateForOrder
                )
                documentDao.saveDocument(documentTemp!!)
            } else {
                documentTemp?.let {
                    it.title = title
                    it.content = content
                    it.date = actualDate
                    it.dateForOrder = dateForOrder
                    documentDao.updateDocument(it)
                }
            }
        }
        editorUtil!!.clearHistory()
        setResult(
            Constants.RESULT_CODE_RELOAD_LIST,
            Intent().putExtra(Constants.RESULT_RELOAD_LIST_KEY, true)
        )
    }

    private fun animateContentEditor(animateDown: Boolean) {
        if (animateDown) {
            showHideAppBar?.actionModeActivated(true)
            window.statusBarColor = Color.WHITE
            shadowActionMode.visibility = View.VISIBLE
            moveContentTop(btnShowAppBar, true)
            if (!appBarLayout.isVisible) moveContainerEditor(rlEditContainer, true)
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryVariant)
            shadowActionMode.visibility = View.INVISIBLE
            moveContentTop(btnShowAppBar, false)
            if (!appBarLayout.isVisible) {
                showHideAppBar?.actionModeActivated(false)
                moveContainerEditor(rlEditContainer, false)
            } else showHideAppBar?.actionModeActivated(false)
        }
    }
}
