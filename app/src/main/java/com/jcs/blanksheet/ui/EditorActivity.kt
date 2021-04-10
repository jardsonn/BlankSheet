package com.jcs.blanksheet.ui

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import com.jcs.blanksheet.R
import com.jcs.blanksheet.db.DocumentDao
import com.jcs.blanksheet.db.DocumentDatabase
import com.jcs.blanksheet.model.Document
import com.jcs.blanksheet.utils.Constants
import com.jcs.blanksheet.utils.EditorUtil
import com.jcs.blanksheet.utils.JcsAnimation.moveContainerEditor
import com.jcs.blanksheet.utils.JcsAnimation.moveContentTop
import com.jcs.blanksheet.utils.JcsUtils
import com.jcs.blanksheet.utils.ShowHideAppBar
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

    private lateinit var imgHint: ImageView

    private lateinit var appBarLayout: AppBarLayout

    private lateinit var btnShowAppBar: ImageButton

    private lateinit var shadowActionMode: View

    private lateinit var documentDao: DocumentDao

    private var documentTemp: Document? = null

    private var actionMode: ActionMode? = null

    private var menu: Menu? = null

    private var showHideAppBar: ShowHideAppBar? = null

    private var undoAndRedo: EditorUtil? = null

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
        imgHint = findViewById(R.id.img_editor_hint)
        shadowActionMode = findViewById(R.id.view_shadow_action_mode)

        btnShowAppBar = findViewById(R.id.btn_show_appbar)

        appBarLayout = findViewById(R.id.appbar_edit)

        val toolbar: Toolbar = findViewById(R.id.toolbar_edit)
        menu = toolbar.menu

        setSupportActionBar(toolbar)

        lifecycleScope.launch {
            try {
                documentDao = DocumentDatabase.getInstance(this@EditorActivity).documentDao()
            } catch (e: Exception) {
                Log.e("Room database error: ", e.stackTraceToString())
            }
        }

        imgHint.layoutParams.height = editContent.textSize.toInt()

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

//        markwon = Markwon.builder(this)
//            .usePlugin(object : AbstractMarkwonPlugin() {
//                override fun configureParser(builder: Parser.Builder) {
//                    builder.extensions(Collections.singleton(StrikethroughExtension.create()))
//                }
//            })
//            .usePlugin(object : AbstractMarkwonPlugin() {
//                override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
//                    builder.setFactory(
//                        Strikethrough::class.java
//                    ) { _, _ ->
//                        Strikethrough()
//                        UnderlineSpan()
//                    }
//                }
//            })
//            .usePlugin(object : AbstractMarkwonPlugin() {
//                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
//                    builder.asyncDrawableLoader(AsyncDrawableLoader.noOp())
//                }
//            })
//            .usePlugin(object : AbstractMarkwonPlugin() {
//                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
//                    builder.on(
//                        SoftLineBreak::class.java
//                    ) { visitor, _ -> visitor.forceNewLine() }
//                }
//            })
//            .usePlugin(HtmlPlugin.create { plugin: HtmlPlugin ->
//                plugin.addHandler(
//                    AlignTagHandler()
//                )
//            }).build()

        undoAndRedo = EditorUtil(editContent)

        imgHint.visibility = if (editContent.text.isEmpty()) View.VISIBLE else View.GONE

        val imgTextSpan =
            SpannableString(" ").setSpan(
                ImageSpan(
                    this,
                    R.drawable.ic_paragraph,
                    DynamicDrawableSpan.ALIGN_BASELINE
                ).drawable, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

        editContent.hint = imgTextSpan.toString()

        editContent.addTextChangedListener { s ->
            imgHint.visibility = if (s!!.isEmpty()) View.VISIBLE else View.GONE
        }

        editTitle.addTextChangedListener { s ->
            textTitle.text = s
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
        undoAndRedo?.textUndoRedoObserver(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_edit_undo -> {
                undoAndRedo?.undo()
            }
            R.id.item_menu_edit_redo -> {
                undoAndRedo?.redo()
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
            textContent.visibility = View.VISIBLE
            editContent.visibility = View.GONE
            textTitle.visibility = View.VISIBLE
            editTitle.visibility = View.GONE

//            val node = markwon?.parse(editContent.text.toString())
//            val markdown = markwon?.render(node!!)
//            markwon?.setParsedMarkdown(textContent, markdown!!)

            menu?.findItem(item.itemId)?.apply {
                title = resources.getString(R.string.editar)
                icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_round_edit)
                icon.let {
                    DrawableCompat.setTint(
                        it,
                        ContextCompat.getColor(applicationContext, R.color.appbar_color_icon)
                    )
                }
            }
            isPreviewMode = true
        } else {
            textContent.visibility = View.GONE
            editContent.visibility = View.VISIBLE
            textTitle.visibility = View.GONE
            editTitle.visibility = View.VISIBLE
            menu?.findItem(item.itemId)?.apply {
                title = resources.getString(R.string.prever)
                icon =
                    ContextCompat.getDrawable(applicationContext, R.drawable.ic_round_preview)
            }
            isPreviewMode = false
        }
    }

    private fun saveContent() {
        if (!TextUtils.isEmpty(editTitle.text) || !TextUtils.isEmpty(editContent.text)) {
            var title = editTitle.text.toString()
            val content = editContent.text.toString()
            if (title.isEmpty()) title = JcsUtils().getFormattedTitle(editContent)
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
