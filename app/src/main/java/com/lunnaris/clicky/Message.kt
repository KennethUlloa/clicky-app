package com.lunnaris.clicky

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lunnaris.clicky.ui.theme.onSuccess
import com.lunnaris.clicky.ui.theme.onSuccessContainer
import com.lunnaris.clicky.ui.theme.success
import com.lunnaris.clicky.ui.theme.successContainer

enum class MessageType {
    SUCCESS, ERROR, INFO
}

data class Message(val title: String, val body: String, val type: MessageType)

@Composable
fun ErrorMessage(error: Throwable) {
    val title = if (error is ApiException) { error.title } else { "App Error" }
    val message = "${error.message}"
    Box(
        modifier = Modifier

            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.errorContainer,
                RoundedCornerShape(10.dp)
            )
            .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(10.dp))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(message, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun SuccessMessage(message: Message) {
    val title = message.title
    val body = message.body
    Box(
        modifier = Modifier

            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.successContainer,
                RoundedCornerShape(10.dp)
            )
            .border(1.dp, MaterialTheme.colorScheme.success, RoundedCornerShape(10.dp))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.success
            )
            Text(body, color = MaterialTheme.colorScheme.success)
        }
    }
}

@Composable
fun MessageElement(message: Message) {
    val title: String = message.title
    val body: String = message.body
    val textColor = when (message.type) {
        MessageType.ERROR -> MaterialTheme.colorScheme.onError
        MessageType.SUCCESS -> MaterialTheme.colorScheme.onSuccess
        else -> MaterialTheme.colorScheme.onTertiary
    }
    val containerColor = when (message.type) {
        MessageType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
        MessageType.SUCCESS -> MaterialTheme.colorScheme.onSuccessContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }

    Box(
        modifier = Modifier

            .fillMaxWidth()
            .background(
                containerColor,
                RoundedCornerShape(10.dp)
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(body, color = textColor)
        }
    }
}