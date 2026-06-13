package com.jero.authentication.data.usecase

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.domain.providers.StringsProvider
import com.example.domain.usecase.user.GetGoogleIdTokenUseCase
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.jero.authentication.R

class GetGoogleIdTokenUseCaseImpl(
    private val context: Context,
    private val stringsProvider: StringsProvider,
) : GetGoogleIdTokenUseCase {
    override suspend fun invoke(): String? {
        val webClientId = stringsProvider(R.string.default_web_client_id)
        if (webClientId.isBlank()) return null

        return try {
            val credentialManager = CredentialManager.create(context)
            val googleSignInOption = GetSignInWithGoogleOption.Builder(webClientId).build()
            val request = GetCredentialRequest(listOf(googleSignInOption))
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                GoogleIdTokenCredential.createFrom(credential.data).idToken
            } else null
        } catch (_: GetCredentialCancellationException) {
            null
        }
    }
}
