package com.app.whatsappclone.googleSignIn

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.app.whatsappclone.ChatViewModel
import com.app.whatsappclone.SignInResult
import com.app.whatsappclone.UserData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient,
    val viewModel: ChatViewModel
) {
    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
            GoogleIdTokenRequestOptions.builder().setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("454535167782-a66gcr3gj8b8hab5ndqbcdu1olmaqg6j.apps.googleusercontent.com")
                .build()
        ).setAutoSelectEnabled(true).build()
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        viewModel.resetState()
        val cred = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = cred.googleIdToken
        val googleCred = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCred).await().user
            SignInResult(
                errorMessage = null,
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName.toString(),
                        profilePictureUrl = photoUrl.toString()
                            .substring(0, photoUrl.toString().length - 6),
                        email = email.toString()
                    )
                })
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                errorMessage = e.message,
                data = null
            )
        }
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run{
        UserData(
            email = email.toString(),
            userId = uid,
            username = displayName,
            profilePictureUrl = photoUrl.toString()
        )
    }
}