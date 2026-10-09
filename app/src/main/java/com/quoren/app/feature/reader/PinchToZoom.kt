package com.quoren.app.feature.reader

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput

/** Two-finger pinch only; single-finger drags stay untouched so the pager and scroll keep working. */
fun Modifier.pinchToZoom(onZoom: (Float) -> Unit, onEnd: () -> Unit): Modifier = composed {
    val currentOnZoom by rememberUpdatedState(onZoom)
    val currentOnEnd by rememberUpdatedState(onEnd)
    pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            var zooming = false
            do {
                val event = awaitPointerEvent()
                if (event.changes.count { it.pressed } >= 2) {
                    val zoom = event.calculateZoom()
                    if (zoom != 1f) {
                        zooming = true
                        currentOnZoom(zoom)
                    }
                    if (zooming) event.changes.forEach { it.consume() }
                }
            } while (event.changes.any { it.pressed })
            if (zooming) currentOnEnd()
        }
    }
}
