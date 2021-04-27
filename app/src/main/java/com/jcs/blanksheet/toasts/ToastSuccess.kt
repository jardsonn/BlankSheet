package com.jcs.blanksheet.toasts

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.PopupWindow
import com.jcs.blanksheet.R

/**
 * Created by Jardson Costa on 26/04/2021.
 */

class ToastSuccess(private val activity: Activity) {
    
    private var toastPopupWindow: PopupWindow? = null
    
    @SuppressLint("InflateParams")
    private fun getToast(): PopupWindow {
        val layout =
            LayoutInflater.from(activity).inflate(R.layout.toast_success_layout, null, false)
        
        val popupWindow = PopupWindow(
            layout,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        
        popupWindow.elevation =
            activity.resources.getDimensionPixelSize(R.dimen.toast_elevation).toFloat()
        
        popupWindow.animationStyle = R.style.AnimToastSuccess
        return popupWindow
    }
    
    fun show() {
        val toast = getToast()
        toastPopupWindow = toast
        
        if (activity.window != null) {
            toast.showAtLocation(
                activity.window.decorView,
                Gravity.CENTER,
                0,
                0
            )
        }
        Handler(Looper.getMainLooper()).postDelayed({
            toast.dismiss()
        }, 1000)
    }
    
    fun dismiss() {
        toastPopupWindow!!.dismiss()
    }
}