package com.example.peoplelist.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.peoplelist.R

/**
 * Created by Berk Ç. on 9/14/21.
 */
class InvisibleProgressDialog(private val activity: Activity) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?) = Dialog(activity).apply {
        setContentView(R.layout.progress_dialog)
        window?.setDimAmount(0.06f)
        isCancelable = false
    }
}