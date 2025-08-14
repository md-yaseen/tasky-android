package com.example.tasky

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.data.Task
import com.example.tasky.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(app: Application): AndroidViewModel(app) {
    private val repo = TaskRepository.get(app)
    val tasks = repo.all().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addTask(title: String, recurring: Boolean) = viewModelScope.launch {
        repo.add(title, recurring)
        com.example.tasky.widget.TaskWidget().updateAll(getApplication())
    }

    fun toggle(task: Task) = viewModelScope.launch {
        repo.toggleDone(task.id, !task.isDoneToday)
        com.example.tasky.widget.TaskWidget().updateAll(getApplication())
    }
}
