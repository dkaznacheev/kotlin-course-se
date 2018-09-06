package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun testImpossible() {
        assertEquals(null, formattedName(3, "a?c"))
    }

    @Test
    fun testSimple01() {
        assertEquals("abba", formattedName(2, "a??a"))
    }

    @Test
    fun testSimple02() {
        assertEquals("abba", formattedName(2, "?b?a"))
    }

    @Test
    fun testQuestionMark() {
        assertEquals("a", formattedName(1, "?"))
    }

    @Test
    fun testOrdered() {
        assertEquals("abcaefahijklmnophbajliiglaljannpiadbbdaipnnajlalgiiljabhponmlkjihafeacba",
                formattedName(16,
                "?bc??f?hi?k??n??hb??liiglal???npi???b???p?n?j?al????j???ponmlkjih??e??b?"))
    }
}