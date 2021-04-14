package com.jcs.blanksheet.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.jcs.blanksheet.R
import com.jcs.blanksheet.callbacks.EventClick
import com.jcs.blanksheet.model.Document
import com.jcs.blanksheet.utils.ColorGenerator
import com.jcs.blanksheet.utils.JcsAnimation
import com.jcs.blanksheet.utils.JcsUtils
import com.jcs.blanksheet.widget.TextDrawable

/**
 * Created by Jardson Costa on 04/04/2021.
 */

class BlankSheetAdapter() :
    ListAdapter<Document, BlankSheetAdapter.BlankSheetHolder>(DOC_COMPARATOR) {

    private var selectedItems: SparseBooleanArray = SparseBooleanArray()
    private var listenerClick: EventClick.OnClickListener? = null
    private var listenerLongClick: EventClick.OnLongClickListener? = null
    private var isSelectionMode: Boolean = false

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlankSheetHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.main_item_row_list, parent, false)
        return BlankSheetHolder(view)
    }

    override fun onBindViewHolder(holder: BlankSheetHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    inner class BlankSheetHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val mCardView = view.findViewById<MaterialCardView>(R.id.card_item_content)
        private val mTextIcon = view.findViewById<TextView>(R.id.text_icon)
        private val mTextTitle = view.findViewById<TextView>(R.id.textview_row_title)
        private val mTextDate = view.findViewById<TextView>(R.id.textview_row_data)
        private val mImageIconText = view.findViewById<ImageView>(R.id.image_icon_text)
        private val mImageIconTextBack = view.findViewById<ImageView>(R.id.image_icon_checked)

        fun bind(document: Document) {
            val title = document.title
            mTextTitle.text = title
            mTextDate.text = document.date
            mTextIcon.text = JcsUtils().getFirstLetter(title)
            mTextIcon.setTextColor(ColorGenerator.MATERIAL.getColor(title))
            mImageIconText.setImageDrawable(JcsUtils().iconTextDrawable(title))

            mImageIconTextBack.background = TextDrawable.builder()
                .buildRoundRect(
                    "",
                    ContextCompat.getColor(itemView.context, R.color.check_icon_background_color),
                    100
                )

            mCardView.apply {
                isChecked = isSelected(adapterPosition)
                animateIconChecked(mCardView, mImageIconTextBack, mImageIconText)
                setOnClickListener {
                    listenerClick?.onDocumentClick(adapterPosition, document, this)
                }
                setOnLongClickListener {
                    listenerLongClick?.onDocumentLongClick(adapterPosition, document)
                    true
                }
            }
        }

    }

    private fun animateIconChecked(cardView: MaterialCardView, viewBack: View, viewFront: View) {
        cardView.apply {
            if (isSelectionMode) {
                setCardBackgroundColor(
                    if (isChecked) {
                        viewBack.visibility = View.VISIBLE
                        JcsAnimation.flipView(
                            context,
                            viewFront,
                            viewBack,
                            false
                        )
                        ContextCompat.getColor(
                            context,
                            R.color.checked_card
                        )
                    } else {
                        JcsAnimation.flipView(context, viewFront, viewBack, true)
                        viewBack.visibility = View.GONE
                        Color.WHITE
                    }
                )
            } else {
                if (viewBack.visibility == View.VISIBLE) {
                    JcsAnimation.flipView(context, viewFront, viewBack, true)
                    viewBack.visibility = View.GONE
                    setCardBackgroundColor(Color.WHITE)
                }
                if (isChecked) {
                    JcsAnimation.flipView(context, viewFront, viewBack, true)
                    viewBack.visibility = View.GONE
                    setCardBackgroundColor(Color.WHITE)
                }
            }
        }
    }

    fun setOnClickListener(listener: EventClick.OnClickListener) {
        this.listenerClick = listener
    }

    fun setOnLongClickListener(listener: EventClick.OnLongClickListener) {
        this.listenerLongClick = listener
    }

    fun setOnLongClickListener(listener: (position: Int, document: Document) -> Unit) {
        setOnLongClickListener(object : EventClick.OnLongClickListener {
            override fun onDocumentLongClick(position: Int, document: Document) {
                listener.invoke(position, document)
            }

        })
    }

    fun setOnClickListener(listener: (position: Int, document: Document, cardView: MaterialCardView) -> Unit) {
        setOnClickListener(object : EventClick.OnClickListener {
            override fun onDocumentClick(
                position: Int,
                document: Document,
                cardView: MaterialCardView
            ) {
                listener.invoke(position, document, cardView)
            }
        })
    }

    fun isSelected(position: Int): Boolean {
        return getSelectedItems().contains(position)
    }

    fun toggleSelection(position: Int) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position)
            getItem(position).isChecked = false
        } else {
            selectedItems.put(position, true)
            getItem(position).isChecked = true
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

    fun isSelectionMode(isSelectionMode: Boolean) {
        this.isSelectionMode = isSelectionMode
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

    companion object {
        private val DOC_COMPARATOR = object : DiffUtil.ItemCallback<Document>() {
            override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }
}