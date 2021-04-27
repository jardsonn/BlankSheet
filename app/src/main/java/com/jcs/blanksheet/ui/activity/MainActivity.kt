package com.jcs.blanksheet.ui.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jcs.blanksheet.DocumentApplication
import com.jcs.blanksheet.R
import com.jcs.blanksheet.dialog.BottomSortDialog
import com.jcs.blanksheet.dialog.sharedpreference.SavedSortingData.SavedSortingDataLiveData
import com.jcs.blanksheet.entity.Document
import com.jcs.blanksheet.interfaces.EventClick
import com.jcs.blanksheet.ui.adapter.BlankSheetAdapter
import com.jcs.blanksheet.ui.adapter.SearchAdapter
import com.jcs.blanksheet.utils.Constants
import com.jcs.blanksheet.utils.Sort
import com.jcs.blanksheet.viewmodel.DocumentViewModel
import com.jcs.blanksheet.widget.JcsSearchView
import com.jcs.blanksheet.toasts.ToastUndo
import java.util.*


class MainActivity : AppCompatActivity(), EventClick.OnItemClickListener,
    EventClick.OnItemLongClickListener {
    
    private lateinit var toolbar: Toolbar
    
    private lateinit var fabAdd: FloatingActionButton
    
    private lateinit var recyclerView: RecyclerView
    
    private lateinit var cardView: CardView
    
    private lateinit var searchView: JcsSearchView
    
    private lateinit var adapter: BlankSheetAdapter
    
    private lateinit var searchAdapter: SearchAdapter
    
    private var actionMode: ActionMode? = null
    
    private var reloadList: Boolean = false
    
    private var singleBack: Boolean = false
    
    private val viewModel: DocumentViewModel by viewModels {
        DocumentViewModel.DocViewModelFactory((application as DocumentApplication).repository)
    }
    
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        toolbar = findViewById(R.id.toolbar_main)
        fabAdd = findViewById(R.id.main_add_fab)
        searchView = findViewById(R.id.search_view)
        recyclerView = findViewById(R.id.recycler_view)
        cardView = findViewById(R.id.card_view_main)
        setUpToolbar()
        
        adapter = BlankSheetAdapter()
        searchAdapter = SearchAdapter()
        
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        fabAdd.setOnClickListener {
            val i = Intent(this, EditorActivity::class.java)
            startActivityForResult(i, Constants.REQUEST_CODE_RELOAD_LIST)
        }
        
        recyclerView.setOnScrollChangeListener { _, _, _, _, oldScrollY ->
            if (oldScrollY > 0 && actionMode == null) fabAdd.show()
            else if (oldScrollY < 0) fabAdd.hide()
        }
        
        loadDocumentList(adapter)
        searchView.setOnQueryTextListener(searchQueryTextListener)
        searchView.setSearchViewListener(searchViewListener)
        adapter.setOnClickListener(this)
        adapter.setOnLongClickListener(this)
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE_RELOAD_LIST) {
            if (resultCode == Constants.RESULT_CODE_RELOAD_LIST) {
                reloadList = data!!.getBooleanExtra(Constants.RESULT_RELOAD_LIST_KEY, false)
            }
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_main_sort -> {
                BottomSortDialog(this).apply {
                    setOnOptionsChangeListener { dismiss() }
                    show()
                }
            }
            R.id.item_menu_main_search -> {
                searchView.openSearch()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onBackPressed() {
        if (searchView.isOpen)
            searchView.closeSearch()
        else super.onBackPressed()
        
    }
    
    override fun onItemClick(position: Int, doc: Document) {
        if (actionMode != null) {
            this.toggleSelection(position)
        } else {
//            val intentEditor = Intent(this, EditorActivity::class.java)
//            intentEditor.putExtra(Constants.DOCUMENT_ID, id)
//            startActivity(intentEditor)
            val intentEditor = Intent(this, EditorActivity::class.java)
            intentEditor.putExtra(Constants.DOCUMENT_ID, doc)
            startActivity(intentEditor)
        }
    }
    
    override fun onItemLongClick(position: Int, doc: Document) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback)
            this.toggleSelection(position)
        }
    }
    
    //  created methods
    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.menu_selection, menu)
            fabAdd.hide()
            return true
        }
        
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }
        
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.item_menu_selection_delete -> {
                    deleteDocument(
                        if (recyclerView.adapter is BlankSheetAdapter)
                            adapter.getCheckedDocuments()
                        else searchAdapter.getCheckedDocuments()
                    )
                }
            }
            actionMode?.finish()
            return true
        }
        
        override fun onDestroyActionMode(mode: ActionMode?) {
            fabAdd.show()
            clearSelection()
            actionMode = null
        }
    }
    
    private fun clearSelection() {
        if (recyclerView.adapter is BlankSheetAdapter) adapter.clearSelection()
        else searchAdapter.clearSelection()
        
        
    }
    
    private fun toggleSelection(position: Int) {
        val count: Int = if (recyclerView.adapter is BlankSheetAdapter) {
            adapter.toggleSelection(position)
            adapter.getSelectedItemCount()
        } else {
            searchAdapter.toggleSelection(position)
            searchAdapter.getSelectedItemCount()
        }
        
        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }
    
    private val searchQueryTextListener = object : JcsSearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            searchDocuments(query, searchAdapter)
            searchView.clearFocus()
            return true
        }
        
        override fun onQueryTextChange(newText: String): Boolean {
            searchDocuments(newText, searchAdapter)
            return true
        }
    }
    
    private val searchViewListener = object : JcsSearchView.SearchViewListener {
        override fun onSearchViewOpened() {
            fabAdd.hide()
        }
        
        override fun onSearchViewClosed() {
            fabAdd.show()
            loadDocumentList(adapter)
        }
    }
    
    fun searchDocuments(query: String?, searchAdapter: SearchAdapter) {
        
        searchAdapter.setOnClickListener { position, id ->
            if (actionMode == null) {
                val intentEditor = Intent(this, EditorActivity::class.java)
                intentEditor.putExtra(Constants.DOCUMENT_ID, id)
                searchView.closeSearch()
                startActivity(intentEditor)
            } else {
                this.toggleSelection(position)
            }
            
        }
        searchAdapter.setOnLongClickListener { position, _ ->
            if (actionMode == null) {
                actionMode = startSupportActionMode(actionModeCallback)
                this.toggleSelection(position)
            }
        }
        
        viewModel.searchDocument(query = query!!)
            .observe(this@MainActivity, { searchedDoc ->
                searchedDoc?.let {
                    searchAdapter.submitList(it)
                    recyclerView.adapter = searchAdapter
                }
            })
    }
    
    private fun loadDocumentList(adapter: BlankSheetAdapter) {
        SavedSortingDataLiveData(
            PreferenceManager.getDefaultSharedPreferences(this),
            Constants.SORT_BY,
            Sort.get(Constants.BY_NAME_AZ)
        ).getOrder(Constants.SORT_BY, Sort.get(Constants.BY_NAME_AZ))
            .observe(this) { sortBy ->
                viewModel.getAllDocuments(sortBy).observe(this) { docs ->
                    docs.let {
                        adapter.submitList(it)
                        if (recyclerView.adapter is SearchAdapter)
                            recyclerView.adapter = adapter
                    }
                }
            }
    }
    
    
    private fun deleteDocument(checkedDocument: List<Document>) {
        viewModel.deleteDocument(*checkedDocument.toTypedArray())
        ToastUndo(this, fabAdd).apply {
            setOnUndoClickListener {
                for (i in checkedDocument) {
                    viewModel.saveDocument(i)
                    dismiss()
                }
            }
            show()
        }
    }
    
    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        val customTitleLayout = layoutInflater.inflate(R.layout.custom_title, null)
        val customTitle = customTitleLayout.findViewById<TextView>(R.id.custom_title_text)
        val appName = SpannableString(resources.getText(R.string.app_name))
        appName.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorSecondary)),
            0,
            5,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        appName.setSpan(StyleSpan(Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        
        customTitle.text = appName
        supportActionBar?.customView = customTitleLayout
        
    }
    
}