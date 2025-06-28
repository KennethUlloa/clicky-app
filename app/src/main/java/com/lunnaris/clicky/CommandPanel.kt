package com.lunnaris.clicky

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class Command(val label: String, val command: String, val iconRes: Int? = null) {
    constructor(label: String, iconRes: Int?): this(label, label, iconRes)
}

val commands = listOf(
    // Command("backspace", R.drawable.backspace),
    Command("mute", R.drawable.volume_mute),
    Command("vol. down", "vol_down", R.drawable.volume_minus),
    Command("vol. up", "vol_up", R.drawable.volume_plus),
    Command("backward", R.drawable.skip_backward),
    Command("play/pause", "play_pause", R.drawable.play_pause),
    Command("forward", R.drawable.skip_forward),
)


@Composable
fun CommandButton(command: Command, modifier: Modifier, onClick: (String) -> Unit) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(10.dp)
            )
            .clickable {
            onClick(command.command)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            command.iconRes?.let {
                Icon(
                    painter = painterResource(command.iconRes),
                    contentDescription = command.label
                )
            }
            Text(command.label)
        }

    }
}


@Composable
fun CommandPanel(onCommandSend: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            commands.chunked(3).forEach {
                    row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach {
                        CommandButton(it, Modifier.weight(1f)) {
                            cmd ->
                            onCommandSend(cmd)
                        }
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommandPanelPreview() {
    CommandPanel {  }
}