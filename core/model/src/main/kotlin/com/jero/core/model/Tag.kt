package com.jero.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val UNTAGGED_TAG_ID = "predefined_untagged"
const val PERSONAL_TAG_ID = "predefined_personal"

@Parcelize
data class Tag(
    val id: String = "",
    val name: String = "",
    val userId: String = "",
    val pendingSync: Boolean = false,
) : Parcelable
