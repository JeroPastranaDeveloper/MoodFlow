package com.jero.moodflow.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import com.jero.designsystem.theme.MoodFlowTheme
import com.jero.moodflow.R
import kotlinx.coroutines.launch

class NotesWidgetConfigActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)

        appWidgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            MoodFlowTheme {
                ConfigContent(onFilterSelected = ::saveAndFinish)
            }
        }
    }

    private fun saveAndFinish(filter: NotesWidgetFilter) {
        lifecycleScope.launch {
            val glanceId = GlanceAppWidgetManager(this@NotesWidgetConfigActivity)
                .getGlanceIdBy(appWidgetId)

            updateAppWidgetState(
                context = this@NotesWidgetConfigActivity,
                glanceId = glanceId,
            ) { prefs ->
                prefs[WIDGET_FILTER_KEY] = filter.name
            }
            NotesWidget().update(this@NotesWidgetConfigActivity, glanceId)

            val result = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, result)
            finish()
        }
    }
}

@Composable
private fun ConfigContent(onFilterSelected: (NotesWidgetFilter) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.widget_configure_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(32.dp))

        FilterButton(
            label = stringResource(R.string.widget_filter_all),
            onClick = { onFilterSelected(NotesWidgetFilter.ALL) },
            primary = true,
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilterButton(
            label = stringResource(R.string.widget_filter_pinned),
            onClick = { onFilterSelected(NotesWidgetFilter.PINNED) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilterButton(
            label = stringResource(R.string.widget_filter_normal),
            onClick = { onFilterSelected(NotesWidgetFilter.NORMAL) },
        )
    }
}

@Composable
private fun FilterButton(label: String, onClick: () -> Unit, primary: Boolean = false) {
    if (primary) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
        ) {
            Text(text = label)
        }
    } else {
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
        ) {
            Text(text = label)
        }
    }
}
