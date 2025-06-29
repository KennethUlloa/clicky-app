package com.lunnaris.clicky

import android.view.ViewConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Modifier.detectTouchpadGestures(
    onDrag: (dx: Float, dy: Float) -> Unit,
    onScroll: (dy: Float) -> Unit,
    onClick: (Click) -> Unit,
    onDebug: (message: String) -> Unit
): Modifier = pointerInput(Unit) {
    coroutineScope {
        awaitEachGesture {
            val touches = mutableMapOf<PointerId, Offset>()

            val down = awaitFirstDown()
            touches[down.id] = down.position

            var longPressTriggered = false
            var wasDragged = false
            var wasScrolled = false
            val longPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()

            val longPressJob = launch {
                delay(longPressTimeout)
                longPressTriggered = true
                onClick(Click.RIGHT)
            }

            while (true) {
                val event = awaitPointerEvent()
                val activePointers = event.changes.filter { it.pressed }

                activePointers.forEach { change ->
                    touches[change.id] = change.position
                }

                when {

                    touches.size == 2 && activePointers.size == 2 -> {
                        longPressJob.cancel()
                        val (a, b) = activePointers
                        val changeA = a.positionChange()
                        val changeB = b.positionChange()

                        val avgDy = (changeA.y + changeB.y) / 2

                        if (avgDy != 0f) {
                            wasScrolled = true
                            onScroll(avgDy)
                        }

                        activePointers.forEach { it.consume() }
                    }

                    touches.size == 1 && activePointers.size == 1 -> {
                        longPressJob.cancel()
                        val change = activePointers.first()
                        val (dx, dy) = change.positionChange()
                        if (dx != 0f || dy != 0f) {
                            wasDragged = true
                            onDrag(dx, dy)
                        }
                        change.consume()
                        touches[change.id] = change.position
                    }

                    event.changes.all { !it.pressed } -> {
                        longPressJob.cancel()
                        if (!longPressTriggered && !wasDragged && !wasScrolled) {
                            onClick(Click.LEFT)
                        }
                        break
                    }
                }
            }
        }
    }
}


@Composable
fun TouchPad(
    modifier: Modifier,
    onMove: (dx: Float, dy: Float) -> Unit,
    onClick: (dir: String) -> Unit,
    onScroll: (dy: Float) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(10.dp)
                    )
                    .fillMaxSize()
                    .detectTouchpadGestures(
                        onDrag = { dx, dy ->
                            onMove(dx * Global.dragSensibility, dy * Global.dragSensibility)
                        },
                        onClick = { dir ->
                            when (dir) {
                                Click.RIGHT -> onClick("right")
                                Click.LEFT -> onClick("left")
                            }
                        },
                        onScroll = onScroll,
                        onDebug = {}
                    )
            ) {
            }
        }

        if (Global.showClickButtons) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            onClick("left")
                        }
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 10.dp,
                                vertical = 15.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.mouse_left_click_outline),
                            contentDescription = "Left click"
                        )
                        Text(stringResource(R.string.btn_left_click))
                    }

                }
                Box(
                    modifier = modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            onClick("right")
                        }
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 10.dp,
                                vertical = 15.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.mouse_right_click_outline),
                            contentDescription = "Right click"
                        )
                        Text(stringResource(R.string.btn_right_click))
                    }

                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun TouchPadPreview() {
    TouchPad(
        modifier = Modifier,
        onMove = { dx, dy -> },
        onClick = {},
        onScroll = {}
    )
}