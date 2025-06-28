package com.lunnaris.clicky

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun KeyboardScreen() {
    Column(
        modifier = Modifier.padding(10.dp)
            .fillMaxSize()
    ) {
        TextInput(
            onTextSend = {
                API.emit("text", it)
            },
            onCommandSend = {
                API.emit("command", it)
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun KeyboardScreenPreview() {
    KeyboardScreen()
}