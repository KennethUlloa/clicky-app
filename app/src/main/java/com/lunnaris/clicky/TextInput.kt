package com.lunnaris.clicky

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TextInput(onTextSend: (text: String) -> Unit, onCommandSend: (command: String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextField(
            text,
            onValueChange = { text = it },
            label = { Text("Text") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
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
            OutlinedButton(
                onClick = {
                    onCommandSend("enter")
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.keyboard_return),
                    contentDescription = "enter"
                )
            }
            OutlinedButton(
                onClick = {
                    onCommandSend("backspace")
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.backspace),
                    contentDescription = "backpace"
                )
            }
            Button(onClick = {
                onTextSend(text)
                text = ""
            }) {
                Text("Send")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun TextInputPreview() {
    TextInput({},{})
}