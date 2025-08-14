package com.example.tasky.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TaskRepository private constructor(ctx: Context) {
    private val dao = AppDatabase.get(ctx).taskDao()

    fun all(): Flow<List<Task>> = dao.all()

    suspend fun add(title: String, recurring: Boolean) {
        dao.insert(Task(title = title, isRecurringDaily = recurring))
    }

    suspend fun toggleDone(id: Long, done: Boolean) {
        val current = dao.all().first().firstOrNull { it.id == id } ?: return
        dao.update(current.copy(isDoneToday = done))
    }

    suspend fun resetRecurring() = dao.resetRecurringDoneFlags()

    companion object {
        @Volatile private var INSTANCE: TaskRepository? = null
        fun get(ctx: Context): TaskRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: TaskRepository(ctx.applicationContext).also { INSTANCE = it }
        }
    }
}
