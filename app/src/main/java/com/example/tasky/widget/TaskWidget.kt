package com.example.tasky.widget

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.ActionParameters
import androidx.glance.appwidget.action.actionParametersOf
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.unit.dp
import com.example.tasky.MainActivity
import com.example.tasky.data.AppDatabase
import com.example.tasky.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class TaskWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { Content(context) }
    }
    suspend fun updateAll(context: Context) {
        val manager = GlanceAppWidgetManager(context)
        val ids = manager.getGlanceIds(TaskWidget::class.java)
        ids.forEach { update(context, it) }
    }
}

private val KEY_TOGGLE_ID = ActionParameters.Key<Long>("toggle_id")

@androidx.compose.runtime.Composable
private fun Content(context: Context) {
    val dao = AppDatabase.get(context).taskDao()
    val tasks: List<Task> = runBlocking(Dispatchers.IO) { dao.all().first() }
    val top = tasks.take(6)

    GlanceTheme {
        Column(modifier = GlanceModifier.fillMaxSize().padding(12.dp)) {
            Row {
                Text(text = "Tasky")
                Spacer(GlanceModifier.defaultWeight())
                Text(text = "+ Add", modifier = GlanceModifier.clickable(actionRunCallback<OpenAppAction>()))
            }
            Spacer(GlanceModifier.height(8.dp))
            if (top.isEmpty()) {
                Text("No tasks. Tap + Add.")
            } else {
                top.forEach { t ->
                    Row(modifier = GlanceModifier
                        .padding(vertical = 4.dp)
                        .clickable(actionRunCallback<ToggleAction>(actionParametersOf(KEY_TOGGLE_ID to t.id)))
                    ) {
                        Text(text = (if (t.isDoneToday) "☑ " else "☐ ") + t.title)
                    }
                }
            }
        }
    }
}

import androidx.glance.action.clickable

class ToggleAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val id = parameters[KEY_TOGGLE_ID] ?: return
        val dao = AppDatabase.get(context).taskDao()
        val list = runBlocking(Dispatchers.IO) { dao.all().first() }
        val current = list.firstOrDefault { it.id == id } ?: return
        dao.update(current.copy(isDoneToday = !current.isDoneToday))
        TaskWidget().updateAll(context)
    }
}

class OpenAppAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

private fun <T> List<T>.firstOrDefault(pred: (T)->Boolean): T? {
    for (x in this) if (pred(x)) return x
    return null
}

class TaskWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TaskWidget()
}
