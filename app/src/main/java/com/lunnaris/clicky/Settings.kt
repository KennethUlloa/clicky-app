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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var message by remember { mutableStateOf<Message?>(null) }
    var inverseScroll by remember { mutableStateOf(Global.inverseScroll) }
    var showClickButtons by remember { mutableStateOf(Global.showClickButtons) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (message != null) {
                MessageElement(message!!)
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Connection", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                TextField(
                    serverAddress, onValueChange = { serverAddress = it }, label = {
                    Text("Server Address")
                }, modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    secret, onValueChange = { secret = it }, label = {
                    Text("Secret")
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
                                    message = Message(
                                        "Success",
                                        "Successfully quit control",
                                        MessageType.SUCCESS
                                    )
                                }.onFailure {
                                    val title = if (it is ApiException) { it.title } else { "APP ERROR" }
                                    val body = it.message ?: "Unknown"
                                    message = Message(
                                        title,
                                        body,
                                        MessageType.ERROR
                                    )
                                }
                        }
                    }) {
                    Text("Disconnect")
                }
                Spacer(modifier = Modifier.size(width = 10.dp, height = 1.dp))
                Button(
                    onClick = {
                        Global.serverAddress = serverAddress
                        scope.launch {
                            API.login(secret).onSuccess {
                                    API.setToken(it.token).initSocket()
                                    message = Message("Success", "Successfully connected", MessageType.SUCCESS)
                                    delay(5000)
                                    message = null
                                }.onFailure {
                                    val title = if (it is ApiException) { it.title } else { "APP ERROR" }
                                    val body = it.message ?: "Unknown"
                                    message = Message(
                                        title,
                                        body,
                                        MessageType.ERROR
                                    )
                                    delay(5000)
                                    message = null
                                }
                        }
                    }) {
                    Text("Connect")
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(String.format("Drag velocity %.1f", dragSensibility))
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
                Text(String.format("Scroll velocity %.1f", scrollSensibility))
                Slider(
                    scrollSensibility, valueRange = 0.1f..3f, onValueChange = {
                        scrollSensibility = it
                        Global.scrollSensibility = it
                    })
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Inverse scroll")
                Checkbox(inverseScroll, onCheckedChange = {
                    inverseScroll = it
                    Global.inverseScroll = it
                })
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show click buttons")
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