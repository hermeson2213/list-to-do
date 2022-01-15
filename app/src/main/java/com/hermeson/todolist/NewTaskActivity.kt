package com.hermeson.todolist

import android.R
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import androidx.appcompat.view.ActionMode
import androidx.core.widget.TextViewCompat
import com.hermeson.todolist.databinding.ActivityNewTaskBinding
import androidx.core.widget.TextViewCompat.setCustomSelectionActionModeCallback
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson
import com.hermeson.todolist.model.Task
import java.util.*

class NewTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewTaskBinding
    private var item:Task? = null

    companion object {
        const val TASK_ID = "task_id"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (intent.hasExtra(TASK_ID)) {
            val taskId = intent.getIntExtra(TASK_ID, 0)
            item = ListTasks.findById(taskId)

            if(item != null)
            {
                binding.tilTitle.text = item?.title.toString()
                binding.tilDate.text = item?.date.toString()
                binding.tilHour.text = item?.hour.toString()
                binding.btnNewTask.text = "Salvar"
            }
        }

        insertListeners()
    }

    private fun insertListeners() {
        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnNewTask.setOnClickListener {
            var task = Task(
                binding.tilTitle.text,
                binding.tilHour.text,
                binding.tilDate.text,
                id = intent.getIntExtra(TASK_ID, 0),
                done = item?.done?: false
            )

            ListTasks.insertTask(task)
            var json = Gson().toJson(ListTasks.getList())
            writeFile(this, json).let {
                finish()
            }
        }

        binding.tilDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()

            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                binding.tilDate.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.tilHour.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()

            timePicker.addOnPositiveButtonClickListener {
                val minute = if (timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute
                val hour = if (timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour

                binding.tilHour.text = "$hour:$minute"
            }

            timePicker.show(supportFragmentManager, null)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}