package com.lunnaris.clicky

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lunnaris.clicky.ui.theme.ClickyTheme


@Composable
fun Home(navController: NavController) {
    Global.allowQR = true
    var messageData by remember { mutableStateOf<MessageData?>(null) }
    var deviceName by remember { mutableStateOf(Global.deviceName) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (API.isReady()) {
            API.deviceData().onSuccess {
                deviceName = it.deviceName
            }.onFailure {
                messageData = getFromError(it, context)
            }
        }
    }

    Column(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(if (deviceName != null && deviceName!!.isNotEmpty()) {
                    R.drawable.laptop
                } else {
                    R.drawable.laptop_off
                }),
                contentDescription = "Device name"
            )
            Spacer(Modifier.size(10.dp))
            Text(deviceName ?: stringResource(R.string.no_device), fontSize = 22.sp)
        }
        if (messageData != null) {
            MessageElement(messageData!!)
        }
        TouchPad(
            modifier = Modifier.weight(0.5f, true),
            onScroll = { dy ->
                val direction = if (Global.inverseScroll) {
                    -1
                } else {
                    1
                }
                API.emit("scroll", dy * Global.scrollSensibility * direction)
            },
            onMove = { dx, dy ->
                API.emit("move", dx, dy)
            },
            onClick = { dir ->
                API.emit("click", dir)
            }
        )

        CommandPanel {
            API.emit("command", it)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    val navController = rememberNavController()
    ClickyTheme {
        Home(navController)
    }
}