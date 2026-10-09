package com.quoren.app.domain.audio

import com.quoren.app.domain.model.RepeatCount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RepeatEngineTest {

    private fun drain(engine: RepeatEngine, limit: Int = 100): List<RepeatStep> {
        val steps = mutableListOf<RepeatStep>()
        while (steps.size < limit) steps += engine.onAyahFinished() ?: break
        return steps
    }

    @Test
    fun singlePassAdvancesThroughRangeThenFinishes() {
        val engine = RepeatEngine(3, RepeatCount.ONE, RepeatCount.ONE, 0)
        assertEquals(listOf(1, 2), drain(engine).map { it.index })
    }

    @Test
    fun ayahRepeatReplaysEachAyahWithDelay() {
        val engine = RepeatEngine(2, RepeatCount.THREE, RepeatCount.ONE, 2000)
        val steps = drain(engine)
        assertEquals(listOf(0, 0, 1, 1, 1), steps.map { it.index })
        assertEquals(listOf(2000L, 2000L, 0L, 2000L, 2000L), steps.map { it.delayMs })
    }

    @Test
    fun rangeRepeatRestartsFromFirstAyah() {
        val engine = RepeatEngine(2, RepeatCount.ONE, RepeatCount.THREE, 1000)
        val steps = drain(engine)
        assertEquals(listOf(1, 0, 1, 0, 1), steps.map { it.index })
        assertEquals(1000L, steps[1].delayMs)
        assertEquals(0L, steps[0].delayMs)
    }

    @Test
    fun ayahAndRangeRepeatCombine() {
        val engine = RepeatEngine(2, RepeatCount.THREE, RepeatCount.THREE, 0)
        // per range pass: ayah0 x3 + ayah1 x3 = 6 plays => 5 steps, plus 2 range restarts
        assertEquals(17, drain(engine).size)
    }

    @Test
    fun unlimitedAyahRepeatNeverAdvances() {
        val engine = RepeatEngine(3, RepeatCount.UNLIMITED, RepeatCount.ONE, 0)
        val steps = drain(engine, limit = 50)
        assertEquals(50, steps.size)
        assertEquals(setOf(0), steps.map { it.index }.toSet())
    }

    @Test
    fun unlimitedRangeRepeatLoopsForever() {
        val engine = RepeatEngine(2, RepeatCount.ONE, RepeatCount.UNLIMITED, 0)
        assertEquals(50, drain(engine, limit = 50).size)
    }

    @Test
    fun jumpToResetsAyahPassOnlyWhenIndexChanges() {
        val engine = RepeatEngine(3, RepeatCount.THREE, RepeatCount.ONE, 0)
        engine.onAyahFinished()
        assertEquals(2, engine.ayahPass)
        engine.jumpTo(0)
        assertEquals(2, engine.ayahPass)
        engine.jumpTo(2)
        assertEquals(1, engine.ayahPass)
        assertEquals(2, engine.index)
    }

    @Test
    fun resetRestoresInitialState() {
        val engine = RepeatEngine(2, RepeatCount.ONE, RepeatCount.ONE, 0)
        drain(engine)
        engine.reset()
        assertEquals(0, engine.index)
        assertEquals(1, engine.rangePass)
        assertNull(RepeatEngine(1, RepeatCount.ONE, RepeatCount.ONE, 0).onAyahFinished())
    }
}
