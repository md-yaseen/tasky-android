package com.example.tasky

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tasky.data.TaskRepository
import com.example.tasky.widget.TaskWidget

class ResetWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result = try {
        TaskRepository.get(applicationContext).resetRecurring()
        TaskWidget().updateAll(applicationContext)
        Result.success()
    } catch (e: Exception) {
        Result.retry()
    }
}
