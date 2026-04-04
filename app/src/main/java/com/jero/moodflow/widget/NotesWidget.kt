package com.jero.moodflow.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.google.firebase.auth.FirebaseAuth
import com.jero.localdatabase.dao.NoteDao
import com.jero.localdatabase.model.NoteEntity
import com.jero.moodflow.MainActivity
import com.jero.moodflow.R
import kotlinx.coroutines.flow.flowOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotesWidget : GlanceAppWidget(), KoinComponent {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    private val notesDao: NoteDao by inject()
    private val auth: FirebaseAuth by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val userId = auth.currentUser?.uid
        val notesFlow = userId?.let { notesDao.getNotesFlow(it) } ?: flowOf(emptyList())

        provideContent {
            val prefs = currentState<Preferences>()
            val filter = NotesWidgetFilter.valueOf(
                prefs[WIDGET_FILTER_KEY] ?: NotesWidgetFilter.ALL.name
            )
            val allNotes by notesFlow.collectAsState(emptyList())
            val notes = when (filter) {
                NotesWidgetFilter.ALL -> allNotes
                NotesWidgetFilter.PINNED -> allNotes.filter { it.pinned }
                NotesWidgetFilter.NORMAL -> allNotes.filter { !it.pinned }
            }.sortedByDescending { it.date }

            GlanceTheme {
                WidgetContent(
                    context = context,
                    notes = notes,
                    filter = filter,
                    isLoggedIn = userId != null,
                )
            }
        }
    }
}

@Composable
private fun WidgetContent(
    context: Context,
    notes: List<NoteEntity>,
    filter: NotesWidgetFilter,
    isLoggedIn: Boolean,
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground),
    ) {
        Header(context, filter)

        when {
            !isLoggedIn -> CenteredMessage(context.getString(R.string.widget_not_logged))
            notes.isEmpty() -> CenteredMessage(context.getString(R.string.widget_empty_notes))
            else -> NotesList(notes, context)
        }
    }
}

@Composable
private fun Header(context: Context, filter: NotesWidgetFilter) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(GlanceTheme.colors.primary)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .clickable(actionStartActivity(Intent(context, MainActivity::class.java).apply {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
})),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = context.getString(R.string.app_name),
            style = TextStyle(
                color = GlanceTheme.colors.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            ),
        )
        Spacer(modifier = GlanceModifier.defaultWeight())
        Text(
            text = context.getString(filter.labelRes),
            style = TextStyle(
                color = GlanceTheme.colors.onPrimary,
                fontSize = 11.sp,
            ),
        )
    }
}

@Composable
private fun NotesList(notes: List<NoteEntity>, context: Context) {
    LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
        items(notes) { note ->
            NoteItem(note = note, context = context)
            Spacer(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(GlanceTheme.colors.surfaceVariant),
            )
        }
    }
}

@Composable
private fun NoteItem(note: NoteEntity, context: Context) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable(actionStartActivity(Intent(context, MainActivity::class.java).apply {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
})),
    ) {
        Text(
            modifier = GlanceModifier.padding(bottom = 4.dp),
            text = note.title.ifBlank { context.getString(R.string.widget_untitled) },
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            ),
            maxLines = 1,
        )
        if (note.content.isNotBlank()) {
            Text(
                text = note.content,
                style = TextStyle(
                    color = GlanceTheme.colors.secondary,
                    fontSize = 11.sp,
                ),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun CenteredMessage(message: String) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = TextStyle(
                color = GlanceTheme.colors.secondary,
                fontSize = 13.sp,
            ),
        )
    }
}
