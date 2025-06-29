package com.lunnaris.clicky

import android.content.Context
import androidx.compose.foundation.background
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

enum class MessageType {
    SUCCESS, ERROR, INFO
}

data class MessageData(val title: String, val body: String, val type: MessageType)

@Composable
fun MessageElement(messageData: MessageData) {
    val title: String = messageData.title
    val body: String = messageData.body
    val textColor = when (messageData.type) {
        MessageType.ERROR -> MaterialTheme.colorScheme.onError
        MessageType.SUCCESS -> MaterialTheme.colorScheme.onSuccess
        else -> MaterialTheme.colorScheme.onTertiary
    }
    val containerColor = when (messageData.type) {
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

fun getFromError(error: Throwable, context: Context): MessageData {
    val body = if (error is ApiException) {
        getAppErrorMessage(context, error.appCode)
    } else {
        error.message ?: context.getString(R.string.error_message)
    }
    val title = context.getString(R.string.error_title)
    return MessageData(
        title,
        body,
        MessageType.ERROR
    )
}