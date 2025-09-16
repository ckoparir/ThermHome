package com.example.thermhome.util

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.InverseMethod
import androidx.room.TypeConverter
import com.example.thermhome.R

fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, duration).show()

class Converters {

    companion object {

        @JvmStatic
        @TypeConverter
        @SuppressLint("ResourceType")
        fun intToColor(context: Context, value: Int): Int {

            return if (value > -1) ContextCompat.getColor(context, R.color.gray_400)
            else ContextCompat.getColor(context, R.color.red)
        }

        @JvmStatic
        @InverseMethod("intToFloat")
        fun floatToInt(multiplier: Int, value: Float): Int {
            return (value * multiplier).toInt()
        }

        @JvmStatic
        fun intToFloat(multiplier: Int, value: Int): Float {
            return value.toFloat() / multiplier
        }

        @JvmStatic
        @InverseMethod("intToBool")
        fun boolToInt(value: Boolean): Int {
            return if (value) 1 else 0
        }

        @JvmStatic
        fun intToBool(value: Int): Boolean {
            return value > 0
        }

    }
}
