package com.demo.testapplication.util

import androidx.appcompat.app.AppCompatActivity
import com.demo.testapplication.component.DialogBlock
import com.demo.testapplication.component.MyDialog

fun AppCompatActivity.showDialog(
    layout: Int,
    cancelable: Boolean = true,
    func: DialogBlock
) {
    val dialog = MyDialog(layout, func)
    dialog.dialog?.setCancelable(cancelable)
    dialog.isCancelable = cancelable
    dialog.show(this.supportFragmentManager, MyDialog::class.java.name)
}

