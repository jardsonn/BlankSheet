package com.jcs.blanksheet.toasts

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.PopupWindow
import android.widget.TextView
import com.jcs.blanksheet.R
import com.mikhaellopez.circularprogressbar.CircularProgressBar


/**
 * Created by Jardson Costa on 01/04/2021.
 */

class ToastUndo(private val activity: Activity?, private val view: View) {
    private var listener: OnUndoClickListener? = null
    private var mToast: PopupWindow? = null
    private var viewTranslationY = 0f
    
    companion object {
        const val LENGTH_LONG = 3500
        const val LENGTH_SHORT = 2000
        
        const val LENGTH_SUPER_LONG = 5000
        
    }
    
    @SuppressLint("InflateParams")
    private fun getToast(): PopupWindow {
        val inflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.toast_undo_layout, null)
        val textUndo = layout.findViewById<TextView>(R.id.text_toast_undo)
        val textCount = layout.findViewById<TextView>(R.id.progress_toast_count)
        val progress = layout.findViewById<CircularProgressBar>(R.id.progress_toast)
        
        layout.measure(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        
        viewTranslationY = layout.measuredHeight - view.paddingBottom.toFloat()
        
        val popupWindow = PopupWindow(
            layout,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        
        popupWindow.elevation =
            activity.resources.getDimensionPixelSize(R.dimen.toast_elevation).toFloat()
        
        progress.setProgressWithAnimation(0f, LENGTH_SUPER_LONG.toLong())
        progress.onProgressChangeListener = {
            textCount.text = it.toInt().toString()
            if (it.equals(1f)) popupWindow.dismiss()
        }
        
        textUndo.setOnClickListener {
            listener!!.onUndoClick(this)
        }
        
        popupWindow.setOnDismissListener {
            lowerAnimateView()
        }
        
        popupWindow.animationStyle = R.style.AnimToastUndo
        return popupWindow
    }
    
    fun show() {
        val toast = getToast()
        mToast = toast
        liftAnimateView()
        if (activity!!.window != null) {
            toast.showAtLocation(
                activity.window.decorView,
                Gravity.BOTTOM,
                0,
                activity.resources.getDimensionPixelSize(R.dimen.toast_margin_bottom)
            )
        }
        Handler(Looper.getMainLooper()).postDelayed({
            toast.dismiss()
        }, LENGTH_SUPER_LONG.toLong())
    }
    
    fun dismiss() {
        mToast!!.dismiss()
    }
    
    private fun liftAnimateView() {
        ObjectAnimator.ofFloat(view, "translationY", -(viewTranslationY + 5)).apply {
            duration = 200
            interpolator = AccelerateInterpolator()
            start()
        }
    }
    
    private fun lowerAnimateView() {
        ObjectAnimator.ofFloat(view, "translationY", 0f).apply {
            duration = 200
            interpolator = AccelerateInterpolator()
            start()
        }
    }
    
    private fun setOnUndoClickListener(listener: OnUndoClickListener) {
        this.listener = listener
    }
    
    fun setOnUndoClickListener(listener: (toastUndo: ToastUndo) -> Unit) {
        setOnUndoClickListener(object : OnUndoClickListener {
            override fun onUndoClick(toastUndo: ToastUndo) {
                listener.invoke(toastUndo)
            }
        })
    }
    
    interface OnUndoClickListener {
        fun onUndoClick(toastUndo: ToastUndo)
    }
    
}