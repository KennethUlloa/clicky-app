package com.lunnaris.clicky

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Settings(navController: NavController) {
    Global.allowQR = true
    val scope = rememberCoroutineScope()
    var serverAddress by remember { mutableStateOf(Global.serverAddress) }
    var secret by remember { mutableStateOf("") }
    var dragSensibility by remember { mutableFloatStateOf(Global.dragSensibility) }
    var scrollSensibility by remember { mutableFloatStateOf(Global.scrollSensibility) }
    var messageData by remember { mutableStateOf<MessageData?>(null) }
    var inverseScroll by remember { mutableStateOf(Global.inverseScroll) }
    var showClickButtons by remember { mutableStateOf(Global.showClickButtons) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (messageData != null) {
                MessageElement(messageData!!)
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.title_connection),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                TextField(
                    serverAddress, onValueChange = { serverAddress = it }, label = {
                        Text(stringResource(R.string.server_address))
                    }, modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    secret, onValueChange = { secret = it }, label = {
                        Text(stringResource(R.string.secret))
                    }, modifier = Modifier.fillMaxWidth()
                )
            }
            Row(
                horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            API.quit()
                                .onSuccess {
                                    API.quitSocket()
                                    messageData = MessageData(
                                        context.getString(R.string.msg_success),
                                        context.getString(R.string.msg_successfully_quit),
                                        MessageType.SUCCESS
                                    )
                                }.onFailure {
                                    messageData = getFromError(it, context)
                                }
                        }
                    }) {
                    Text(stringResource(R.string.btn_disconnect))
                }
                Spacer(modifier = Modifier.size(width = 10.dp, height = 1.dp))
                Button(
                    onClick = {
                        Global.serverAddress = serverAddress
                        scope.launch {
                            API.login(secret).onSuccess {
                                API.setToken(it.token).initSocket()
                                messageData = MessageData(
                                    context.getString(R.string.msg_success),
                                    context.getString(R.string.msg_successful_login),
                                    MessageType.SUCCESS
                                )
                                delay(5000)
                                messageData = null
                            }.onFailure {
                                messageData = getFromError(it, context)
                                delay(5000)
                                messageData = null
                            }
                        }
                    }) {
                    Text(stringResource(R.string.btn_connect))
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    String.format(
                        "${context.getString(R.string.drag_velocity)} %.1f",
                        dragSensibility
                    )
                )
                Slider(
                    dragSensibility, valueRange = 0.1f..3f, onValueChange = {
                        dragSensibility = it
                        Global.dragSensibility = it
                    })
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    String.format(
                        "${context.getString(R.string.scroll_velocity)} %.1f",
                        scrollSensibility
                    )
                )
                Slider(
                    scrollSensibility, valueRange = 0.1f..3f, onValueChange = {
                        scrollSensibility = it
                        Global.scrollSensibility = it
                    })
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.inverse_scroll))
                Checkbox(inverseScroll, onCheckedChange = {
                    inverseScroll = it
                    Global.inverseScroll = it
                })
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.show_click_buttons))
                Checkbox(showClickButtons, onCheckedChange = {
                    showClickButtons = it
                    Global.showClickButtons = it
                })
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    val navController = rememberNavController()
    Settings(navController)
}