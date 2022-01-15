package com.hermeson.todolist

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.style.UpdateAppearance
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.datastore.rxjava3.RxDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hermeson.todolist.databinding.ActivityMainBinding
import com.hermeson.todolist.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { TaskListAdapter() }

    companion object {
        private const val CREATE_NEW_TASK = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createFile(this)
        var list_json = readFile(this)
        val itemType = object : TypeToken<ArrayList<Task>>() {}.type
        ListTasks.list = Gson().fromJson(list_json, itemType)

        binding.rvTasks.adapter = adapter
        insertListeners()
    }

    private fun insertListeners() {
        binding.fvNewTask.setOnClickListener {
            startActivity(Intent(this, NewTaskActivity::class.java))
        }

        adapter.listenerEdit = {
            val intent = Intent(this, NewTaskActivity::class.java)
            intent.putExtra(NewTaskActivity.TASK_ID, it.id)
            startActivityForResult(intent, CREATE_NEW_TASK)
        }

        adapter.listenerDelete ={
            ListTasks.deleteTask(it)
            var json = Gson().toJson(ListTasks.getList())
            writeFile(this, json).let {
                updateList()
            }
        }

        adapter.listenerDone ={
            var item = ListTasks.findById(it.id)
            item?.done = true
            ListTasks.insertTask(item!!)
            var json = Gson().toJson(ListTasks.getList())
            writeFile(this, json).let {
                updateList()
            }
        }

        adapter.listenerUndo ={
            var item = ListTasks.findById(it.id)
            item?.done = false
            ListTasks.insertTask(item!!)
            var json = Gson().toJson(ListTasks.getList())
            writeFile(this, json).let {
                updateList()
            }
        }
    }

    private fun updateList() {
        val list = ListTasks.getList()
        binding.includeEmpty.emptyState.visibility = if (list.isEmpty()) View.VISIBLE
        else View.GONE

        adapter.submitList(list, {
            adapter.notifyDataSetChanged()
        })
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }
}