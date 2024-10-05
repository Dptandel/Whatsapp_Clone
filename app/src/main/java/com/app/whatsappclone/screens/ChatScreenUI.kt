package com.app.whatsappclone.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun ChatScreenUI() {
    Text(text = "Chat Screen", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center, fontSize = 24.sp)
}