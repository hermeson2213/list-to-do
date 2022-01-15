package com.hermeson.todolist

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hermeson.todolist.databinding.ItemTaskBinding
import com.hermeson.todolist.model.Task

class TaskListAdapter : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(DiffCallback()) {

    var listenerEdit : (Task) -> Unit = {}
    var listenerDelete : (Task) -> Unit = {}
    var listenerDone : (Task) -> Unit = {}
    var listenerUndo : (Task) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Task) {
            binding.tvTitle.text = item.title
            binding.tvDate.text = "${item.date} ${item.hour}"
            binding.ivMore.setOnClickListener {
                showPopup(item)
            }

            if (item.done)
            {
                binding.ivDone.visibility = View.VISIBLE
            }
            else
            {
                binding.ivDone.visibility = View.GONE
            }
        }

        private fun showPopup(item: Task) {
            val ivMore = binding.ivMore
            val popupMenu = PopupMenu(ivMore.context, ivMore)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            val menuItemDone: MenuItem = popupMenu.menu.findItem(R.id.action_done)
            val menuItemUndo: MenuItem = popupMenu.menu.findItem(R.id.action_undo)

            if (!item.done)
            {
                menuItemDone.isVisible = true
                menuItemUndo.isVisible = false
            }
            else
            {
                menuItemDone.isVisible = false
                menuItemUndo.isVisible = true
            }

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_edit -> listenerEdit(item)
                    R.id.action_delete -> listenerDelete(item)
                    R.id.action_done -> listenerDone(item)
                    R.id.action_undo -> listenerUndo(item)
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
}