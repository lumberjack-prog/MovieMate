package com.app.moviemate

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager

// jest odpowiedzialna za tworzenie i wyświetlanie niestandardowego dialogu ładowania w aplikacji
class LoadingDialog(context: Context) {

    private val dialog: Dialog = Dialog(context)

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_loading_dialog)
        dialog.setCancelable(false)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

}