package com.lunnaris.clicky

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class TextCommand(val command: String, val icon: Int)

@Composable
fun TextInput(onTextSend: (text: String) -> Unit, onCommandSend: (command: String) -> Unit) {
    var text by remember { mutableStateOf("") }

    val commands = listOf(
        TextCommand("enter", R.drawable.keyboard_return),
        TextCommand("backspace", R.drawable.backspace)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextField(
            text,
            onValueChange = { text = it },
            label = { Text(stringResource(R.string.text_send_placeholder)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    onTextSend(text)
                    text = ""
                }
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(Modifier.weight(1f))

            commands.forEach {
                key(it.command) {
                    OutlinedButton(
                        onClick = {
                            onCommandSend(it.command)
                        }
                    ) {
                        Icon(
                            painter = painterResource(it.icon),
                            contentDescription = it.command
                        )
                    }
                }
            }

            Button(onClick = {
                onTextSend(text)
                text = ""
            }) {
                Text(stringResource(R.string.btn_send))
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun TextInputPreview() {
    TextInput({},{})
}