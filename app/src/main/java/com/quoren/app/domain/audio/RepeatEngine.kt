package com.quoren.app.domain.audio

import com.quoren.app.domain.model.RepeatCount

/** Next playback step: play [index] of the range after waiting [delayMs]. */
data class RepeatStep(val index: Int, val delayMs: Long)

/**
 * Pure state machine deciding what follows an ayah that just finished.
 * Each ayah plays [ayahRepeat] times, then the range repeats [rangeRepeat] times. Delays apply to repetitions only.
 */
class RepeatEngine(
    private val itemCount: Int,
    private val ayahRepeat: RepeatCount,
    private val rangeRepeat: RepeatCount,
    private val delayMs: Long,
) {
    var index: Int = 0
        private set
    var ayahPass: Int = 1
        private set
    var rangePass: Int = 1
        private set

    init {
        require(itemCount > 0) { "itemCount must be positive" }
    }

    fun jumpTo(newIndex: Int) {
        val target = newIndex.coerceIn(0, itemCount - 1)
        if (target != index) {
            index = target
            ayahPass = 1
        }
    }

    fun reset() {
        index = 0
        ayahPass = 1
        rangePass = 1
    }

    /** Returns the next step, or null when everything has been played. */
    fun onAyahFinished(): RepeatStep? {
        if (!ayahRepeat.isLastPass(ayahPass)) {
            ayahPass++
            return RepeatStep(index, delayMs)
        }
        ayahPass = 1
        if (index + 1 < itemCount) {
            index++
            return RepeatStep(index, 0)
        }
        if (!rangeRepeat.isLastPass(rangePass)) {
            rangePass++
            index = 0
            return RepeatStep(0, delayMs)
        }
        return null
    }
}
