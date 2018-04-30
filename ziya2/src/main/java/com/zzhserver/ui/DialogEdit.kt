package com.zzhserver.ui

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText

import com.zzhserver.R
import com.zzhserver.utils.LogUtils

/**
 * Created by Administrator on 2017/12/3 0003.
 */

class DialogEdit(context: Context, themeResId: Int) : AlertDialog(context, themeResId) {
    companion object {
        private var builder: AlertDialog.Builder? = null
        fun builder(context: Context): DialogEdit {
            builder = AlertDialog.Builder(context)
            LogUtils.i("准备")
            return DialogEdit(context)
        }
    }

    constructor(context: Context) : this(context, R.style.Dialog) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit, null)
        builder?.setView(view)
        et_name = view.findViewById<EditText>(R.id.et_name)
    }

    private var et_name: EditText? = null


    fun getName(): String? {
        return et_name?.text.toString();
    }

    fun msg(hint: String, message: String, title: String): DialogEdit {
        if (!hint.isNotEmpty())
            et_name?.hint = hint
        if (!message.isNotEmpty())
            builder?.setMessage(message)
        if (!title.isNotEmpty())
            builder?.setMessage(message)
        return this
    }

    fun yes(listener: DialogInterface.OnClickListener): DialogEdit {
        builder?.setPositiveButton(android.R.string.ok, listener)
        return this
    }

    fun no(): DialogEdit {
        builder?.setNegativeButton(android.R.string.cancel) { dialogInterface, i -> }
        return this
    }

    fun build() {
        builder?.create()?.show()
    }
}
