package com.example.tasky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tasky.data.Task

class MainActivity : ComponentActivity() {
    private val vm: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App(vm) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(vm: TaskViewModel) {
    val tasks by vm.tasks.collectAsState()
    var title by remember { mutableStateOf("") }
    var recurring by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tasky") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = {
                if (title.isNotBlank()) {
                    vm.addTask(title.trim(), recurring)
                    title = ""
                }
            }, text = { Text("Add Task") })
        }
    ) { padd ->
        Column(Modifier.padding(padd).padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("New task") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(Modifier.padding(top = 8.dp)) {
                Checkbox(checked = recurring, onCheckedChange = { recurring = it })
                Text("Recurring daily", modifier = Modifier.padding(start = 8.dp))
            }
            Spacer(Modifier.height(12.dp))
            Divider()
            Spacer(Modifier.height(12.dp))
            LazyColumn {
                items(tasks, key = { it.id }) { task ->
                    TaskRow(task, onToggle = { vm.toggle(task) })
                }
            }
        }
    }
}

@Composable
fun TaskRow(task: Task, onToggle: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.weight(1f)) {
            Text(task.title, style = MaterialTheme.typography.titleMedium)
            val sub = buildString {
                append(if (task.isRecurringDaily) "Daily • " else "One-off • ")
                append(if (task.isDoneToday) "Done" else "Pending")
            }
            Text(sub, style = MaterialTheme.typography.bodySmall)
        }
        Checkbox(checked = task.isDoneToday, onCheckedChange = { onToggle() })
    }
}
