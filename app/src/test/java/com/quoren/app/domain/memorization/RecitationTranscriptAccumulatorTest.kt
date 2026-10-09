package com.quoren.app.domain.memorization

import org.junit.Assert.assertEquals
import org.junit.Test

class RecitationTranscriptAccumulatorTest {

    @Test
    fun keepsAllEndpointSegmentsUntilExplicitFinish() {
        val accumulator = RecitationTranscriptAccumulator()
        assertEquals("الحمد", accumulator.acceptSegment("الحمد"))
        assertEquals("الحمد لله", accumulator.acceptSegment("لله"))
        assertEquals("الحمد لله رب العالمين", accumulator.finish("رب العالمين"))
    }

    @Test
    fun partialHypothesisReplacesItselfAndDoesNotEraseCommittedWords() {
        val accumulator = RecitationTranscriptAccumulator()
        accumulator.acceptSegment("الحمد لله")
        assertEquals("الحمد لله رب", accumulator.acceptPartial("رب"))
        assertEquals("الحمد لله رب العالمين", accumulator.acceptPartial("رب العالمين"))
        assertEquals("الحمد لله رب العالمين", accumulator.finish(""))
    }

    @Test
    fun emptyOrRepeatedFinalSegmentDoesNotDuplicateText() {
        val accumulator = RecitationTranscriptAccumulator()
        accumulator.acceptSegment("بسم الله")
        assertEquals("بسم الله", accumulator.finish("بسم الله"))
        accumulator.reset()
        assertEquals("", accumulator.finish(""))
    }

    @Test
    fun emptyEndpointDoesNotEraseCurrentPartialWords() {
        val accumulator = RecitationTranscriptAccumulator()
        accumulator.acceptSegment("الحمد لله")
        accumulator.acceptPartial("رب العالمين")
        assertEquals("الحمد لله رب العالمين", accumulator.acceptSegment(""))
        assertEquals("الحمد لله رب العالمين", accumulator.finish(""))
    }
}
