package com.hermeson.todolist

import android.content.Context
import androidx.core.content.contentValuesOf
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

private val locale = Locale("pt", "BR")

fun Date.format() : String {
    return SimpleDateFormat("dd/MM/yyyy", locale).format(this)
}

var TextInputLayout.text : String
    get() = editText?.text?.toString() ?: ""
    set(value) {
        editText?.setText(value)
    }

fun createFile(context: Context)
{
    File(context.getFilesDir().path,"list_tasks.json")
}

fun readFile(context: Context):String
{
    val file = File(context.getFilesDir().path,"list_tasks.json")
    return FileInputStream(file).bufferedReader().use { it.readText() }
}

fun writeFile(context: Context, text:String)
{
    val file = File(context.getFilesDir().path,"list_tasks.json")

    FileOutputStream(file).use {
        it.write(text.toByteArray())
    }
}