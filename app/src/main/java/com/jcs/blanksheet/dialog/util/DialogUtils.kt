package com.jcs.blanksheet.dialog.util

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

/**
 * Created by Jardson Costa on 08/04/2021.
 */

object DialogUtils {

     fun createBottomSheetBackgroundDrawable(
        color: Int,
        cornerRadius: Float
    ): Drawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadii = floatArrayOf(
                cornerRadius,
                cornerRadius,
                cornerRadius,
                cornerRadius,
                0f,
                0f,
                0f,
                0f
            )
        }
    }
}