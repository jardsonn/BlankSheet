package com.jcs.blanksheet.ui.adapter

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.jcs.blanksheet.R
import com.jcs.blanksheet.interfaces.EventClick
import com.jcs.blanksheet.entity.Document
import com.jcs.blanksheet.utils.ColorGenerator
import com.jcs.blanksheet.utils.JcsUtils
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Jardson Costa on 15/04/2021.
 */

class SearchAdapter : ListAdapter<Document, SearchAdapter.SearchHolder>(SEARCH_COMPARATOR) {
    
    private val selectedItems = SparseBooleanArray()
    
    private var currentSelectedPos: Int = -1
    
    private var listenerItemClick: EventClick.OnItemClickListener? = null
    
    private var listenerItemLongClick: EventClick.OnItemLongClickListener? = null
    
    inner class SearchHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textIcon = view.findViewById<TextView>(R.id.text_icon)
        private val textTitle = view.findViewById<TextView>(R.id.textview_row_title)
        private val textDate = view.findViewById<TextView>(R.id.textview_row_data)
        private val imgIconText = view.findViewById<ImageView>(R.id.image_icon_text)
        private val iconChecked = view.findViewById<ImageView>(R.id.image_icon_checked)
        
        fun bind(document: Document) {
            (itemView as MaterialCardView).isChecked = isSelected(position = adapterPosition)
            
            with(document) {
                textTitle.text = title
                textDate.text = date
                textIcon.text = title.first().toString().capitalize(Locale.getDefault())
                textIcon.setTextColor(ColorGenerator.MATERIAL.getColor(this))
                imgIconText.setImageDrawable(JcsUtils().iconTextDrawable(this))
                DrawableCompat.setTint(
                    iconChecked.drawable,
                    ColorGenerator.MATERIAL.getColor(this)
                )
                
                if (isSelected(position = adapterPosition)) {
                    textIcon.isVisible = false
                    iconChecked.isVisible = true
                } else {
                    textIcon.isVisible = true
                    iconChecked.isVisible = false
                }
                
                itemView.setOnClickListener {
                    listenerItemClick?.onItemClick(adapterPosition, this)
                }
                itemView.setOnLongClickListener {
                    listenerItemLongClick?.onItemLongClick(adapterPosition, this)
                    true
                }
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        return SearchHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.main_item_row_list, parent, false)
        )
    }
    
    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        holder.bind(getItem(position))
        if (currentSelectedPos == position) currentSelectedPos = -1
    }
    
    override fun getItemCount(): Int {
        return currentList.size
    }
    
    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }
    
    fun isItemChecked(position: Int): Boolean {
        return getItem(position).isChecked
    }
    
    fun isSelected(position: Int): Boolean {
        return getSelectedItems().contains(position)
    }
    
    fun toggleSelection(position: Int) {
        currentSelectedPos = position
        
        if (selectedItems[position, false]) {
            selectedItems.delete(position)
            currentList[position].isChecked = false
        } else {
            selectedItems.put(position, true)
            currentList[position].isChecked = true
        }
        notifyItemChanged(position)
    }
    
    fun clearSelection() {
        val selection = getSelectedItems()
        selectedItems.clear()
        for (i in selection) {
            notifyItemChanged(i)
        }
    }
    
    fun getSelectedItemCount(): Int {
        return selectedItems.size()
    }
    
    private fun getSelectedItems(): List<Int> {
        val items: MutableList<Int> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }
    
    fun getCheckedDocuments(): List<Document> = currentList.filter {
        it.isChecked
    }
    
    fun setOnClickListener(listenerItem: EventClick.OnItemClickListener) {
        this.listenerItemClick = listenerItem
    }
    
    fun setOnLongClickListener(listenerItem: EventClick.OnItemLongClickListener) {
        this.listenerItemLongClick = listenerItem
    }
    
    fun setOnLongClickListener(listener: (position: Int, doc: Document) -> Unit) {
        setOnLongClickListener(object : EventClick.OnItemLongClickListener {
            override fun onItemLongClick(position: Int, doc: Document) {
                listener.invoke(position, doc)
            }
        })
    }
    
    fun setOnClickListener(listener: (position: Int, doc: Document) -> Unit) {
        setOnClickListener(object : EventClick.OnItemClickListener {
            override fun onItemClick(position: Int, doc: Document) {
                listener.invoke(position, doc)
            }
        })
    }
    
    companion object {
        private val SEARCH_COMPARATOR = object : DiffUtil.ItemCallback<Document>() {
            override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }
}