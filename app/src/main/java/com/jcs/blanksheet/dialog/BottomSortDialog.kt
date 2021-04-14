package com.jcs.blanksheet.dialog

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jcs.blanksheet.R
import com.jcs.blanksheet.dialog.sharedpreference.SharedPreferenceLiveData
import com.jcs.blanksheet.utils.Constants
import com.jcs.blanksheet.utils.Sort

/**
 * Created by Jardson Costa on 07/04/2021.
 */

class BottomSortDialog(private var act: AppCompatActivity) : BottomSheetDialog(act),
    RadioGroup.OnCheckedChangeListener {
    private lateinit var rgSort: RadioGroup
    private lateinit var rbByNameAZ: RadioButton
    private lateinit var rbByNameZA: RadioButton
    private lateinit var rbByDateRecent: RadioButton
    private lateinit var rbByDateOld: RadioButton

//    private var mPreferences: SharedPreferences? = null
//    private var mEditor: SharedPreferences.Editor? = null

    private var mPreference: SharedPreferenceLiveData.SharedPreferenceStringLiveData? = null
    private var mPreferenceManager: SharedPreferences? = null

    private var listener: OnOptionsChangeListener? = null
    private var listenerDismiss: OnDismissListener? = null

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  window?.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_sort)
        //   window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //  window!!.attributes.windowAnimations = R.style.AnimDialog
        //   window?.setGravity(Gravity.BOTTOM)

        rgSort = findViewById(R.id.rg_dialog_sort)!!
        rbByNameAZ = findViewById(R.id.rb_dialog_by_name_az)!!
        rbByNameZA = findViewById(R.id.rb_dialog_by_name_za)!!
        rbByDateRecent = findViewById(R.id.rb_dialog_by_date_recent)!!
        rbByDateOld = findViewById(R.id.rb_dialog_by_date_oldest)!!

        rgSort.setOnCheckedChangeListener(this)

//        mPreferences = context.getSharedPreferences(Constants.RADIO_BUTTON, Context.MODE_PRIVATE)
//        mEditor = this.mPreferences?.edit()
      //  mPreferenceManager = PreferenceManager(context)
     //   mPreference = mPreferenceManager?.getSharedPrefs()

        mPreferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
       mPreference = SharedPreferenceLiveData.SharedPreferenceStringLiveData(
           mPreferenceManager!!,
                Constants.SORT_BY,
                Sort.get(0)!!
            )

        mPreference?.let { it ->
            it.getString(Constants.SORT_BY, Sort.get(0)!!)
                .observe(act, { sortBy ->
                    when (sortBy) {
                        Sort.get(0) -> {
                            rbByNameAZ.isChecked = true
                        }
                        Sort.get(1)!! -> {
                            rbByNameZA.isChecked = true
                        }
                        Sort.get(2)!! -> {
                            rbByDateRecent.isChecked = true
                        }
                        Sort.get(3)!! -> {
                            rbByDateOld.isChecked = true
                        }
                    }
                })
        }

//        when (mPreference?.getValueFromPreferences(Constants.SORT_BY, Sort.get(0)!!)) {
//            Sort.get(0)!! -> {
//                rbByNameAZ.isChecked = true
//            }
//            Sort.get(1)!! -> {
//                rbByNameZA.isChecked = true
//            }
//            Sort.get(2)!! -> {
//                rbByDateRecent.isChecked = true
//            }
//            Sort.get(3)!! -> {
//                rbByDateOld.isChecked = true
//            }
//        }

//        when (mPreferences?.getString(Constants.SORT_BY, Constants.SORT_BY_NAME_AZ)) {
//            Constants.SORT_BY_NAME_AZ -> {
//                rbByNameAZ.isChecked = true
//            }
//            Constants.SORT_BY_NAME_ZA -> {
//                rbByNameZA.isChecked = true
//            }
//            Constants.SORT_BY_DATE_RECENT -> {
//                rbByDateRecent.isChecked = true
//            }
//            Constants.SORT_BY_DATE_OLDEST -> {
//                rbByDateOld.isChecked = true
//            }
        //       }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        when (checkedId) {
            R.id.rb_dialog_by_name_az -> {
//                mEditor?.putString(Constants.SORT_BY, Sort.get(0))
//                mEditor?.commit()
                //mPreferenceManager?.setSharedPreferences(Constants.SORT_BY, Sort.get(0)!!)
                mPreferenceManager?.edit()!!.putString(Constants.SORT_BY, Sort.get(0)).commit()
            }
            R.id.rb_dialog_by_name_za -> {
//                mEditor?.putString(Constants.SORT_BY, Sort.get(1))
//                mEditor?.commit()
                mPreferenceManager?.edit()!!.putString(Constants.SORT_BY, Sort.get(1)).commit()
            }
            R.id.rb_dialog_by_date_recent -> {
//                mEditor?.putString(Constants.SORT_BY, Sort.get(2))
//                mEditor?.commit()
                mPreferenceManager?.edit()!!.putString(Constants.SORT_BY, Sort.get(2)).commit()
            }
            R.id.rb_dialog_by_date_oldest -> {
//                mEditor?.putString(Constants.SORT_BY, Sort.get(3))
//                mEditor?.commit()
                mPreferenceManager?.edit()!!.putString(Constants.SORT_BY, Sort.get(3)).commit()
            }
        }
        findViewById<RadioButton>(checkedId)!!.setOnClickListener {
            listener?.onOptionsChanged()
        }

    }

    fun setOnOptionsChangeListener(listener: OnOptionsChangeListener) {
        this.listener = listener
    }

    fun setOnOptionsChangeListener(listener: () -> Unit) {
        setOnOptionsChangeListener(object : OnOptionsChangeListener {
            override fun onOptionsChanged() {
                listener.invoke()
            }
        })
    }

    fun setOnDismissListener(listener: OnDismissListener) {
        this.listenerDismiss = listener
    }

    fun setOnDismissListener(listener: () -> Unit) {
        setOnDismissListener(object : OnDismissListener {
            override fun onDismiss() {
                listener.invoke()
            }
        })
    }

    override fun dismiss() {
        super.dismiss()
        listenerDismiss?.onDismiss()
    }

    interface OnDismissListener {
        fun onDismiss()
    }

    interface OnOptionsChangeListener {
        fun onOptionsChanged()
    }

}