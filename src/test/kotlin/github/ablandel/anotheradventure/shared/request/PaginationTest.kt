package github.ablandel.anotheradventure.shared.request

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class PaginationTest {
    @Test
    fun `default constructor`() {
        val actual = Pagination()
        assertEquals(0, actual.offset)
        assertEquals(20, actual.limit)
        assertNull(actual.cursor)
    }

    @Test
    fun `default constructor with offset not valid`() {
        val actual = Pagination(offset = -1)
        assertEquals(0, actual.offset)
        assertEquals(20, actual.limit)
        assertNull(actual.cursor)
    }

    @Test
    fun `default constructor with offset`() {
        val actual = Pagination(offset = 1)
        assertEquals(1, actual.offset)
        assertEquals(20, actual.limit)
        assertNull(actual.cursor)
    }

    @Test
    fun `default constructor with limit not valid`() {
        val actual = Pagination(limit = -1)
        assertEquals(0, actual.offset)
        assertEquals(20, actual.limit)
        assertNull(actual.cursor)
    }

    @Test
    fun `default constructor with limit`() {
        val actual = Pagination(limit = 1)
        assertEquals(0, actual.offset)
        assertEquals(1, actual.limit)
        assertNull(actual.cursor)
    }

    @Test
    fun `default constructor with cursor not valid`() {
        val actual = Pagination(cursor = -1)
        assertEquals(0, actual.offset)
        assertEquals(20, actual.limit)
        assertNull(actual.cursor)
    }

    @Test
    fun `default constructor with cursor`() {
        val actual = Pagination(cursor = 1)
        assertEquals(0, actual.offset)
        assertEquals(20, actual.limit)
        assertEquals(1, actual.cursor)
    }
}
