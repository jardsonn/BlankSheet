package com.jcs.blanksheet.ui

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout
import com.jcs.blanksheet.DocumentApplication
import com.jcs.blanksheet.R
import com.jcs.blanksheet.model.Document
import com.jcs.blanksheet.utils.Constants
import com.jcs.blanksheet.utils.EditorUtil
import com.jcs.blanksheet.utils.JcsAnimation.moveContainerEditor
import com.jcs.blanksheet.utils.JcsAnimation.moveContentTop
import com.jcs.blanksheet.utils.JcsUtils
import com.jcs.blanksheet.utils.ShowHideAppBar
import com.jcs.blanksheet.viewmodel.DocumentViewModel
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.coroutines.InternalCoroutinesApi


/**
 * Created by Jardson Costa on 04/04/2021.
 */

class EditorActivity : AppCompatActivity() {

    private lateinit var textTitle: TextView

    private lateinit var textContent: TextView

    private lateinit var editTitle: EditText

    private lateinit var editContent: EditText

    private lateinit var scrollViewEditor: NestedScrollView

    private lateinit var progressLoadText: CircularProgressBar

    private lateinit var rlEditContainer: RelativeLayout

    private lateinit var appBarLayout: AppBarLayout

    private lateinit var btnShowAppBar: ImageButton

    private lateinit var shadowActionMode: View

    // private lateinit var documentDao: DocumentDao

    private var documentTemp: Document? = null

    private var actionMode: ActionMode? = null

    private var menu: Menu? = null

    private var showHideAppBar: ShowHideAppBar? = null

    private var editorUtil: EditorUtil? = null

    private var isPreviewMode = false

    private val viewModel: DocumentViewModel by viewModels {
        DocumentViewModel.DocViewModelFactory((application as DocumentApplication).repository)
    }

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        rlEditContainer = findViewById(R.id.relative_layout_edit_container)
        editTitle = findViewById(R.id.edit_title_editor)
        textTitle = findViewById(R.id.text_title_editor)
        editContent = findViewById(R.id.edit_content_editor)
        textContent = findViewById(R.id.text_content_editor)
        scrollViewEditor = findViewById(R.id.scroll_view_editor)
        progressLoadText = findViewById(R.id.progress_load_text)
        shadowActionMode = findViewById(R.id.view_shadow_action_mode)

        btnShowAppBar = findViewById(R.id.btn_show_appbar)

        appBarLayout = findViewById(R.id.appbar_edit)

        val toolbar: Toolbar = findViewById(R.id.toolbar_edit)
        menu = toolbar.menu
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)


        intent.extras?.let {
            val id = it.getInt(Constants.DOCUMENT_ID, 0)
            viewModel.getDocumentById(id).observe(this, { doc ->
                documentTemp = doc
                editTitle.setText(doc.title)
                editContent.setText(doc.content)
            })
        }


//        lifecycleScope.launch {
//        //    progressLoadText.visibility = View.VISIBLE
//            documentDao = DocumentDatabase.getDatabase(this@EditorActivity, this).documentDao()
//            if (intent.extras != null) {
//                val id = intent.extras?.getInt(Constants.DOCUMENT_ID, 0)
//            //    withContext(Dispatchers.Main){
//                    documentDao.getDocumentById(id!!).collect { doc ->
//                        documentTemp = doc
//                        editTitle.setText(doc.title)
//                        editContent.setText(doc.content)
//                    }
//            //    progressLoadText.visibility = View.GONE
//             //   }
//            }
//        }

        showHideAppBar = ShowHideAppBar(
            appBarLayout,
            btnShowAppBar,
            rlEditContainer
        )

        btnShowAppBar.setOnClickListener {
            showHideAppBar?.start()
        }

        editorUtil = EditorUtil(editTitle, editContent)

//        HandlerCompat.createAsync(Looper.getMainLooper())
//            .post {
//                editTitle.addTextChangedListener{ s ->
//                    textTitle.text = s
//                }
//                 editContent.addTextChangedListener{ s ->
//                    textContent.text = s
//                }
//            }

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
            JcsUtils().hideKeyboard(this)
            progressLoadText.visibility = View.VISIBLE

            HandlerCompat.createAsync(Looper.getMainLooper()).post {
                editContent.visibility = View.INVISIBLE
                editTitle.visibility = View.INVISIBLE
                editTitle.clearFocus()
                editContent.requestFocus()
                textTitle.clearFocus()
                textContent.requestFocus()

                textTitle.setText(editTitle.text, TextView.BufferType.EDITABLE)
                textContent.setText(editContent.text, TextView.BufferType.EDITABLE)

                runOnUiThread {
                    textTitle.visibility = View.VISIBLE
                    textContent.visibility = View.VISIBLE
                    progressLoadText.visibility = View.GONE

                }
            }

            menu!!.findItem(item.itemId).apply {
                title = resources.getString(R.string.edit_container)
                icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_round_edit)
            }

            for (i in menu!!) {
                if (i != item) {
                    i.isEnabled = false
                    editorUtil!!.changeMenuIconColor(this, i)
                }
            }
            isPreviewMode = true
        } else {
            editContent.visibility = View.VISIBLE
            editTitle.visibility = View.VISIBLE
            editTitle.clearFocus()
            editContent.requestFocus()

            textTitle.apply {
                visibility = View.GONE
                text = " "
            }
            textContent.apply {
                visibility = View.GONE
                text = " "
            }

            menu?.findItem(item.itemId)?.apply {
                title = resources.getString(R.string.preview)
                icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_round_preview)
            }

            for (i in menu!!) {
                editorUtil!!.restoreMenu()
                editorUtil!!.changeMenuIconColor(this, i)
            }
            isPreviewMode = false
        }
    }

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
                viewModel.saveDocument(documentTemp!!)
            } else {
                documentTemp?.let {
                    it.title = title
                    it.content = content
                    it.date = actualDate
                    it.dateForOrder = dateForOrder
                    viewModel.updateDocument(it)
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
