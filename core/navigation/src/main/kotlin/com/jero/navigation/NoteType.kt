package com.jero.navigation

import android.net.Uri
import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import com.jero.core.model.Note
import kotlinx.serialization.json.Json

object NoteType : NavType<Note>(isNullableAllowed = false) {

    override fun put(bundle: Bundle, key: String, value: Note) {
        bundle.putParcelable(key, value)
    }

    override fun get(bundle: Bundle, key: String): Note? =
        BundleCompat.getParcelable(bundle, key, Note::class.java)

    override fun parseValue(value: String): Note {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: Note): String = Uri.encode(Json.encodeToString(value))
}
