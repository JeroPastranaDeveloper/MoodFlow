package com.jero.moodflow.widget

import androidx.annotation.StringRes
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jero.moodflow.R

enum class NotesWidgetFilter(@StringRes val labelRes: Int) {
    ALL(R.string.widget_filter_all),
    PINNED(R.string.widget_filter_pinned),
    NORMAL(R.string.widget_filter_normal),
}

val WIDGET_FILTER_KEY = stringPreferencesKey("widget_filter")
