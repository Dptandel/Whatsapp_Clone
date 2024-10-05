package com.app.whatsappclone

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    private val userCollection = Firebase.firestore.collection(USERS_COLLECTION)

    fun resetState() {

    }

    fun onSignInResult(signInResult: SignInResult) {
        _state.update {
            it.copy(
                isSignIn = signInResult.data != null,
                signInError = signInResult.errorMessage
            )
        }
    }

    fun addUserDataToFireStore(userData: UserData) {
        val userDataMap = mapOf(
            "userId" to userData.userId,
            "username" to userData.username,
            "profilePictureUrl" to userData.profilePictureUrl,
            "email" to userData.email
        )

        val userDocument = userCollection.document(userData.userId)
        userDocument.get().addOnSuccessListener {
            if (it.exists()) {
                userDocument.update(userDataMap).addOnSuccessListener {
                    Log.d(ContentValues.TAG, "User Data added to Firebase Successfully...!")
                }.addOnFailureListener {
                    Log.d(ContentValues.TAG, "User Data added to Firebase Failed...!")
                }
            } else {
                userDocument.set(userDataMap).addOnSuccessListener {
                    Log.d(ContentValues.TAG, "User Data added to Firebase Successfully...!")
                }.addOnFailureListener {
                    Log.d(ContentValues.TAG, "User Data added to Firebase Failed...!")
                }
            }
        }
            .addOnFailureListener { exception ->
            }
    }
}