package com.example.thermhome.ui

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import com.example.thermhome.R

class NumberDialog(private val context: Context) : Dialog(context) {
    private val dialog: Dialog = Dialog(context, R.style.Dialog)

    fun show(btnCelsius: Button, func: IExternalMethod) {
        dialog.setContentView(R.layout.number_dialog)
        dialog.window?.setBackgroundDrawableResource(R.drawable.transparent)

        val btnOk = dialog.findViewById<View>(R.id.btnOk) as Button
        val btnCancel = dialog.findViewById<View>(R.id.btnCancel) as Button
        val npDigit = dialog.findViewById<View>(R.id.npDigit) as NumberPicker
        val npPrecision = dialog.findViewById<View>(R.id.npPrecision) as NumberPicker
        val numbers = arrayOf("00", "10", "20", "30", "40", "50", "60", "70", "80", "90")

        npDigit.maxValue = 28
        npDigit.minValue = 20
        npDigit.wrapSelectorWheel = false

        npPrecision.minValue = 0
        npPrecision.maxValue = 9
        npPrecision.displayedValues = numbers
        npPrecision.wrapSelectorWheel = false

        setNumberPicker(btnCelsius, npDigit, npPrecision)

        btnOk.setOnClickListener {
            btnCelsius.text = String.format("%02d.%02d", npDigit.value, npPrecision.value * 10)
            func.externalMethod()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    fun onPositiveClick(func: IExternalMethod) {
        func.externalMethod()
    }

    private fun setNumberPicker(btnCelsius: Button, npDigit: NumberPicker, npPrecision: NumberPicker) {

        val temp = btnCelsius.text.toString().split("\\ ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        val splits = temp.split("\\D".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val digit = splits[0].toInt()
        val precision = splits[1].toInt() / 10
        npDigit.value = digit
        npPrecision.value = precision
    }
}
