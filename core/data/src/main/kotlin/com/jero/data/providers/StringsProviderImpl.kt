package com.jero.data.providers

import android.content.Context
import com.example.domain.providers.StringsProvider

class StringsProviderImpl(
    private val context: Context
): StringsProvider {
    override fun invoke(stringResId: Int, vararg format: Any): String =
        context.getString(stringResId, *format)

    override fun plural(
        res: Int,
        quantity: Int,
        vararg formats: Any,
    ): String =
        context.resources.getQuantityString(res, quantity, *formats)

    override fun getStringArray(
        stringResId: Int,
        position: Int,
        vararg format: Any,
    ): String =
        context.resources.getStringArray(stringResId)[position].format(*format)
}
