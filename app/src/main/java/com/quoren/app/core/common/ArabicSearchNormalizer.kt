package com.quoren.app.core.common

/**
 * Produces a search-only key. The result must never be displayed or stored as canonical Quran text.
 */
object ArabicSearchNormalizer {

    fun normalize(input: String): String {
        val out = StringBuilder(input.length)
        var lastWasSpace = true
        for (ch in input) {
            val mapped: Char? = when {
                isIgnorable(ch) -> null
                ch == '\u0671' || ch == '\u0622' || ch == '\u0623' || ch == '\u0625' -> '\u0627'
                ch == '\u0649' -> '\u064A'
                ch.isWhitespace() -> ' '
                else -> ch
            }
            if (mapped == null) continue
            if (mapped == ' ') {
                if (lastWasSpace) continue
                lastWasSpace = true
            } else {
                lastWasSpace = false
            }
            out.append(mapped)
        }
        return out.toString().trimEnd()
    }

    private fun isIgnorable(ch: Char): Boolean {
        val c = ch.code
        return c == 0x0640 ||
            c in 0x0610..0x061A ||
            c in 0x064B..0x065F ||
            c == 0x0670 ||
            c in 0x06D6..0x06DC ||
            c in 0x06DD..0x06DE ||
            c in 0x06DF..0x06E8 ||
            c in 0x06EA..0x06ED ||
            c in 0x08D3..0x08FF
    }
}
