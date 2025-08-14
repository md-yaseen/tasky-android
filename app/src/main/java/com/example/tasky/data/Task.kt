package com.example.tasky.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val createdOn: LocalDate = LocalDate.now(),
    val isDoneToday: Boolean = false,
    val isRecurringDaily: Boolean = false
)
