package com.jcs.blanksheet.utils

import android.os.Build
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.ScrollView
import com.google.android.material.appbar.AppBarLayout
import com.jcs.blanksheet.R
import com.jcs.blanksheet.utils.JcsAnimation.moveContainerEditor
import com.jcs.blanksheet.utils.JcsAnimation.moveContentTop

/**
 * Created by Jardson Costa on 22/03/2021.
 */


class ShowHideAppBar(
    private val appBarLayout: AppBarLayout,
    private val btnShowHideAppBar: ImageButton,
    private val editContainer: RelativeLayout
) : CountDownTimer(TOTAL_MILLIS, COUNT_DOWN_INTERVAL) {

    companion object {
        const val TOTAL_MILLIS: Long = 10000
        const val COUNT_DOWN_INTERVAL: Long = 1000
    }

    private val animShow =
        AnimationUtils.loadAnimation(appBarLayout.context, R.anim.slide_show_appbar)
    private val animHide =
        AnimationUtils.loadAnimation(appBarLayout.context, R.anim.slide_hide_appbar)

    private var isActionModeActivated = false


    fun actionModeActivated(isActionModeActivated: Boolean) {
        this.isActionModeActivated = isActionModeActivated // retorna se action mode esta ativado
    }

    // começa a contagem regressiva de 10000ms
    override fun onTick(millisUntilFinished: Long) {
        // verifica se appBarLayout esta visivel
        if (appBarLayout.visibility == View.GONE) {
            appBarLayout.visibility = View.VISIBLE
            //editContainer.scrollTo(0, editContainer.bottom)
            moveContentTop(appBarLayout, false) // appBarLayout fica visivel e desliza para baixo
            moveContentTop(btnShowHideAppBar, true) // btnShowHideAppBar desliza para cima
            moveContainerEditor(editContainer, true) // editContainer desliza para baixo
        }
    }

    override fun onFinish() {
        // verifica se appBarLayout esta visivel
        if (appBarLayout.visibility == View.VISIBLE) {
            appBarLayout.visibility = View.GONE
           // editContainer.scrollTo(0, editContainer.bottom)
            moveContentTop(appBarLayout, true) // appBarLayout desliza para cima e fica invisivel
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                // se action mode esta atiavdo
                if (!isActionModeActivated) {
                    moveContainerEditor(
                        editContainer,
                        false
                    ) // editContainer desliza para o ponto inicial
                    moveContentTop(
                        btnShowHideAppBar,
                        false
                    ) // btnShowHideAppBar desliza para o ponto inicial
                } else {
                    // se action mode não esta atiavdo o editContainer não retorna para o ponto inicial
                    moveContainerEditor(editContainer, false).cancel()
                }
            } else {
                moveContentTop(btnShowHideAppBar, false)
                moveContainerEditor(editContainer, false)
            }
        }
    }

//    override fun onTick(time: Long) {
//        if (appBarLayout.visibility == View.GONE) {
//            btnShowHideAppBar.apply {
//                visibility = View.GONE
//                startAnimation(animHide)
//            }
//            appBarLayout.apply {
//                visibility = View.VISIBLE
//                startAnimation(animShow)
//            }
//            moveContainerEditor(editContainer, true)
//        } else {
//            appBarLayout.visibility = View.VISIBLE
//            btnShowHideAppBar.visibility = View.GONE
//        }
//    }
//
//    override fun onFinish() {
//        if (appBarLayout.visibility == View.VISIBLE) {
//            appBarLayout.apply {
//                visibility = View.GONE
//                startAnimation(animHide)
//            }
//            btnShowHideAppBar.apply {
//                visibility = View.VISIBLE
//                startAnimation(animShow)
//            }
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
//                if (!isActionModeActivated) moveContainerEditor(editContainer, false)
//                else cancel()
//            } else moveContainerEditor(editContainer, false)
//        }
//    }

}
