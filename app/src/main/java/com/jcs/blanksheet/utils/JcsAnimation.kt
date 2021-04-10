package com.jcs.blanksheet.utils

import android.animation.*
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import com.jcs.blanksheet.R
import kotlin.math.hypot


/**
 * Created by Jardson Costa on 22/03/2021.
 */

object JcsAnimation {

    @JvmStatic
    val ANIMATION_DURATION_SHORTEST = 150

    @JvmStatic
    val ANIMATION_DURATION_SHORT = 250

    @JvmStatic
    val ANIMATION_DURATION_MEDIUM = 300L

    @JvmStatic
    val ANIMATION_DURATION_LONG = 800

    @JvmStatic
    val MIN_POSITION = 20

    @JvmStatic
    val MAX_POSITION = 50

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @JvmStatic
    @JvmOverloads
    fun circleRevealView(view: View, duration: Int = ANIMATION_DURATION_SHORT) {
        val cx = view.width
        val cy = view.height / 2

        val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)

        anim.duration = if (duration > 0) duration.toLong() else ANIMATION_DURATION_SHORT.toLong()

        view.visibility = View.VISIBLE
        anim.start()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @JvmStatic
    fun circleHideView(view: View, listenerAdapter: AnimatorListenerAdapter) {
        circleHideView(view, ANIMATION_DURATION_SHORT, listenerAdapter)
    }

    @TargetApi(21)
    @JvmStatic
    fun circleHideView(view: View, duration: Int, listenerAdapter: AnimatorListenerAdapter) {
        val cx = view.width
        val cy = view.height / 2

        val initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0f)

        anim.addListener(listenerAdapter)

        anim.duration = if (duration > 0) duration.toLong() else ANIMATION_DURATION_SHORT.toLong()

        anim.start()
    }

    @JvmStatic
    @JvmOverloads
    fun fadeInView(view: View, duration: Int = ANIMATION_DURATION_SHORTEST) {
        view.visibility = View.VISIBLE
        view.alpha = 0f

        ViewCompat.animate(view).alpha(1f).setDuration(duration.toLong()).setListener(null)
    }

    @JvmStatic
    @JvmOverloads
    fun fadeOutView(view: View, duration: Int = ANIMATION_DURATION_SHORTEST) {
        ViewCompat.animate(view).alpha(0f).setDuration(duration.toLong())
            .setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View) {
                }

                override fun onAnimationEnd(view: View) {
                    view.visibility = View.GONE
                    view.alpha = 1f
                }

                override fun onAnimationCancel(view: View) {}
            })
    }


    @SuppressLint("ObjectAnimatorBinding")
    private fun objectAnimatorY(target: Any, values: Float): ObjectAnimator {
        ObjectAnimator.ofFloat(target, "translationY", values).apply {
            duration = ANIMATION_DURATION_MEDIUM
            start()
            return this
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun animateItemDeleted(target: Any, from: Float, to: Float) {
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(target, "alpha", from, to)
                    .setDuration(550),
                ObjectAnimator.ofObject(
                    target, "backgroundColor", ArgbEvaluator(),
                    Color.rgb(255, 205, 205),
                    Color.rgb(255, 155, 155),
                    Color.rgb(255, 105, 105),
                    Color.rgb(255, 55, 55),
                    Color.rgb(255, 5, 5)
                )
                    .setDuration(500)
            )
            start()
        }
    }
//    @SuppressLint("ObjectAnimatorBinding")
//    fun animateItemDeleted(target: Any, from: Float, to: Float) {
//        AnimatorSet().apply {
//            setTarget(
//                ObjectAnimator.ofFloat(target, "alpha", from, to).apply {
//                    duration = ANIMATION_DURATION_MEDIUM
//                    start()
//                },
//                ObjectAnimator.ofObject(
//                    target,
//                    "backgroundColor",
//                    )
//            )
//            start()
//        }

    fun moveContentTop(view: View, hideView: Boolean): ObjectAnimator {
        val actionBarHeight = JcsUtils().actionBarHeight(view.context).toFloat()
        return if (hideView) {
            objectAnimatorY(view, -actionBarHeight)
        } else {
            objectAnimatorY(view, 0f)
        }
    }

    fun moveContainerEditor(view: View, moveViewDown: Boolean): ObjectAnimator {
        val actionBarHeight = JcsUtils().actionBarHeight(view.context).toFloat()
        return if (moveViewDown) {
            objectAnimatorY(view, actionBarHeight)
        } else {
            objectAnimatorY(view, 0f)
        }
    }

    fun flipView(context: Context, back: View, front: View, showFront: Boolean): Boolean {
        val leftIn =
            AnimatorInflater.loadAnimator(context, R.animator.flip_left_in) as AnimatorSet
        val leftOut =
            AnimatorInflater.loadAnimator(context, R.animator.flip_left_out) as AnimatorSet
        val rightIn =
            AnimatorInflater.loadAnimator(context, R.animator.flip_right_in) as AnimatorSet
        val rightOut =
            AnimatorInflater.loadAnimator(context, R.animator.flip_right_out) as AnimatorSet

        val showFrontAnim = AnimatorSet()
        val showBackAnim = AnimatorSet()

        leftIn.setTarget(back)
        rightOut.setTarget(front)
        showFrontAnim.playTogether(leftIn, rightOut)

        leftOut.setTarget(back)
        rightIn.setTarget(front)
        showBackAnim.playTogether(rightIn, leftOut)

        if (showFront)
            showFrontAnim.start()
        else
            showBackAnim.start()
        return !showFront
    }

}