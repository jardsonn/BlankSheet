package com.jcs.blanksheet.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.jcs.blanksheet.R
import com.jcs.blanksheet.utils.JcsAnimation.circleHideView
import com.jcs.blanksheet.utils.JcsAnimation.circleRevealView
import com.jcs.blanksheet.utils.JcsAnimation.fadeInView
import com.jcs.blanksheet.utils.JcsAnimation.fadeOutView
import kotlin.math.roundToInt

/**
 * Created by Jardson Costa on 04/04/2021.
 */

class JcsSearchView @JvmOverloads constructor(
    private val mContext: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttributes: Int = 0
) : FrameLayout(mContext, attributeSet) {

    companion object {
        
        private const val EMPTY_STRING = ""

    }

    init {
        init()

        initStyle(attributeSet, defStyleAttributes)
    }

    var isOpen = false
        private set

    private var mShouldAnimate = true

    private var mShouldCloseOnTintClick = false

    private var mClearingFocus = false

    private lateinit var mTintView: View

    private lateinit var mRoot: FrameLayout

    private lateinit var mSearchBar: LinearLayout

    private lateinit var mSearchEditText: EditText

    private lateinit var mBack: ImageButton

    private lateinit var mClear: ImageButton

    private lateinit var mOldQuery: CharSequence

    private lateinit var mCurrentQuery: CharSequence

    private var mOnQueryTextListener: OnQueryTextListener? = null

    private var mSearchViewListener: SearchViewListener? = null

    private var mOnClearClickListener: OnClearTextClickListener? = null

    private fun init() {
        LayoutInflater.from(mContext).inflate(R.layout.search_layout, this, true)

        mRoot = findViewById(R.id.search_layout)
        mTintView = mRoot.findViewById(R.id.transparent_view)
        mSearchBar = mRoot.findViewById(R.id.search_bar)
        mBack = mRoot.findViewById(R.id.action_back)
        mSearchEditText = mRoot.findViewById(R.id.et_search)
        mClear = mRoot.findViewById(R.id.action_clear)

        mBack.setOnClickListener { closeSearch() }
        mClear.setOnClickListener { onClearClicked() }
        mTintView.setOnClickListener {
            if (mShouldCloseOnTintClick) {
                closeSearch()
            }
        }
        initSearchView()
    }

    @SuppressLint("CustomViewStyleable")
    private fun initStyle(attributeSet: AttributeSet?, defStyleAttribute: Int) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        val typedArray = mContext.obtainStyledAttributes(
            attributeSet,
            R.styleable.BlankSheetSearchView,
            defStyleAttribute,
            0
        )

        if (typedArray.hasValue(R.styleable.BlankSheetSearchView_searchBackground)) {
            background = typedArray.getDrawable(R.styleable.BlankSheetSearchView_searchBackground)!!
        }
        if (typedArray.hasValue(R.styleable.BlankSheetSearchView_android_textColor)) {
            setTextColor(
                typedArray.getColor(
                    R.styleable.BlankSheetSearchView_android_textColor,
                    ContextCompat.getColor(mContext, R.color.appbar_color_icon)
                )
            )
        }
        if (typedArray.hasValue(R.styleable.BlankSheetSearchView_android_textColorHint)) {
            setHintTextColor(
                typedArray.getColor(
                    R.styleable.BlankSheetSearchView_android_textColorHint,
                    ContextCompat.getColor(mContext, R.color.grey_900)
                )
            )
        }
        if (typedArray.hasValue(R.styleable.BlankSheetSearchView_android_hint)) {
            setHint(typedArray.getString(R.styleable.BlankSheetSearchView_android_hint))
        }

        if (typedArray.hasValue(R.styleable.BlankSheetSearchView_searchCloseIcon)) {
            setClearIcon(
                typedArray.getResourceId(
                    R.styleable.BlankSheetSearchView_searchCloseIcon,
                    R.drawable.ic_round_close
                )
            )
        }
        if (typedArray.hasValue(R.styleable.BlankSheetSearchView_searchBackIcon)) {
            setBackIcon(
                typedArray.getResourceId(
                    R.styleable.BlankSheetSearchView_searchBackIcon,
                    R.drawable.ic_round_arrow_back
                )
            )
        }

        if (typedArray.hasValue(R.styleable.BlankSheetSearchView_android_inputType)) {
            setInputType(
                typedArray.getInteger(
                    R.styleable.BlankSheetSearchView_android_inputType,
                    InputType.TYPE_CLASS_TEXT
                )
            )
        }
        if (typedArray.hasValue(R.styleable.BlankSheetSearchView_searchBarHeight)) {
            setSearchBarHeight(
                typedArray.getDimensionPixelSize(
                    R.styleable.BlankSheetSearchView_searchBarHeight,
                    appCompatActionBarHeight
                )
            )
        } else {
            setSearchBarHeight(appCompatActionBarHeight)
        }

        fitsSystemWindows = false
        typedArray.recycle()
    }

    private fun initSearchView() {
        mSearchEditText.setOnEditorActionListener { v, actionId, event ->
            onSubmitQuery()
            true
        }
        mSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                this@JcsSearchView.onTextChanged(s)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        mSearchEditText.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showKeyboard(mSearchEditText)
            }
        }
    }

    private fun onSubmitQuery() {
        val query: CharSequence? = mSearchEditText.text

        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (mOnQueryTextListener?.onQueryTextSubmit(query.toString()) == false) {
                closeSearch()
                mSearchEditText.setText(EMPTY_STRING)
            }
        }
    }

    private fun showKeyboard(view: View?) {
        view?.requestFocus()
        if (isHardKeyboardAvailable.not()) {
            val inputMethodManager =
                view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(view, 0)
        }
    }

    private val isHardKeyboardAvailable: Boolean
        get() = mContext.resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS

    private fun displayClearButton(display: Boolean) {
        mClear.visibility = if (display) VISIBLE else GONE
    }

    fun openSearch() {
        if (isOpen) {
            return
        }

        mSearchEditText.setText(EMPTY_STRING)
        mSearchEditText.requestFocus()
        if (mShouldAnimate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mRoot.visibility = VISIBLE
                circleRevealView(mSearchBar)
            } else {
                fadeInView(mRoot)
            }
        } else {
            mRoot.visibility = VISIBLE
        }

        mSearchViewListener?.onSearchViewOpened()

        isOpen = true
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun closeSearch() {
        if (!isOpen) {
            return
        }
        mSearchEditText.setText(EMPTY_STRING)
        clearFocus()
        if (mShouldAnimate) {
            val v: View = mRoot
            val listenerAdapter: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    v.visibility = GONE
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                circleHideView(mSearchBar, listenerAdapter)
            } else {
                fadeOutView(mRoot)
            }
        } else {
            mRoot.visibility = GONE
        }
        mSearchViewListener?.onSearchViewClosed()

        isOpen = false
    }

    private fun onTextChanged(newText: CharSequence) {
        mCurrentQuery = mSearchEditText.text
        if (!TextUtils.isEmpty(mCurrentQuery)) {
            displayClearButton(true)
        } else {
            displayClearButton(false)
        }
        mOnQueryTextListener?.onQueryTextChange(newText.toString())

        mOldQuery = mCurrentQuery
    }


    private fun onClearClicked() {
        mOnClearClickListener?.onClearClicked()
        mSearchEditText.setText(EMPTY_STRING)
    }

    fun setOnQueryTextListener(mOnQueryTextListener: OnQueryTextListener?) {
        this.mOnQueryTextListener = mOnQueryTextListener
    }

    fun setSearchViewListener(mSearchViewListener: SearchViewListener?) {
        this.mSearchViewListener = mSearchViewListener
    }

    fun setCloseOnTintClick(shouldClose: Boolean) {
        mShouldCloseOnTintClick = shouldClose
    }

    fun setShouldAnimate(mShouldAnimate: Boolean) {
        this.mShouldAnimate = mShouldAnimate
    }


    override fun setBackground(background: Drawable) {
        mTintView.background = background
    }

    override fun setBackgroundColor(color: Int) {
        setTintColor(color)
    }

    fun setSearchBarColor(color: Int) {
        mSearchEditText.setBackgroundColor(color)
        mSearchBar.setBackgroundColor(color)
    }

    private fun setTintColor(color: Int) {
        mTintView.setBackgroundColor(color)
    }

    fun setTintAlpha(alpha: Int) {
        if (alpha < 0 || alpha > 255) return
        val d = mTintView.background
        if (d is ColorDrawable) {
            val color = d.color
            val newColor =
                Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
            setTintColor(newColor)
        }
    }

    fun adjustTintAlpha(factor: Float) {
        if (factor < 0 || factor > 1.0) return
        val d = mTintView.background
        if (d is ColorDrawable) {
            var color = d.color
            color = adjustAlpha(color, factor)
            mTintView.setBackgroundColor(color)
        }
    }

    private fun adjustAlpha(color: Int, factor: Float): Int {
        if (factor < 0) return color
        val alpha = (Color.alpha(color) * factor).roundToInt()
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    fun setTextColor(color: Int) {
        mSearchEditText.setTextColor(color)
    }

    fun setHintTextColor(color: Int) {
        mSearchEditText.setHintTextColor(color)
    }

    fun setHint(hint: CharSequence?) {
        mSearchEditText.hint = hint
    }

    fun setClearIcon(resourceId: Int) {
        mClear.setImageResource(resourceId)
    }

    fun setBackIcon(resourceId: Int) {
        mBack.setImageResource(resourceId)
    }

    fun setInputType(inputType: Int) {
        mSearchEditText.inputType = inputType
    }

    fun setOnClearClickListener(listener: OnClearTextClickListener) {
        mOnClearClickListener = listener
    }

    fun setOnClearClickListener(listener: () -> Unit) {
        setOnClearClickListener(object : OnClearTextClickListener {
            override fun onClearClicked() {
                listener.invoke()
            }
        })
    }

    fun setSearchBarHeight(height: Int) {
        mSearchBar.minimumHeight = height
        mSearchBar.layoutParams.height = height
    }

    private val appCompatActionBarHeight: Int
        get() {
            val tv = TypedValue()
            context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)
            return resources.getDimensionPixelSize(tv.resourceId)
        }

    val currentQuery: String
        get() = if (!TextUtils.isEmpty(mCurrentQuery)) {
            mCurrentQuery.toString()
        } else EMPTY_STRING


    override fun clearFocus() {
        mClearingFocus = true
        hideKeyboard(this)
        super.clearFocus()
        mSearchEditText.clearFocus()
        mClearingFocus = false
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        return !(mClearingFocus || !isFocusable) && mSearchEditText.requestFocus(
            direction,
            previouslyFocusedRect
        )
    }

    interface OnQueryTextListener {
        fun onQueryTextSubmit(query: String): Boolean
        fun onQueryTextChange(newText: String): Boolean
    }

    interface SearchViewListener {
        fun onSearchViewOpened()

        fun onSearchViewClosed()
    }

    interface OnClearTextClickListener {
        fun onClearClicked()
    }

}
