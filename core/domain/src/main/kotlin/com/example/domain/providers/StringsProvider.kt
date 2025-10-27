package com.example.domain.providers

import androidx.annotation.PluralsRes

interface StringsProvider {
    operator fun invoke(stringResId: Int, vararg format: Any): String
    fun plural(@PluralsRes res: Int, quantity: Int, vararg formats: Any): String
    fun getStringArray(stringResId: Int, position: Int, vararg format: Any): String
}
