package com.demo.testapplication.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.demo.testapplication.R

typealias DialogBlock = ((MyDialog) -> Unit)?

class MyDialog(private val layout: Int, private var block: DialogBlock = null) :
    DialogFragment() {
    lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layout, container, false)

        this.mView = view
        block?.invoke(this)
        return view
    }
}