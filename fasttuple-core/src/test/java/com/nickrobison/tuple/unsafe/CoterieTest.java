package com.nickrobison.tuple.unsafe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoterieTest {

    @Test
    void testUnsafeAccessor() {
        assertNotNull(Coterie.unsafe());
        assertSame(Coterie.unsafe(), Coterie.unsafe());
    }

    @Test
    void testUnsafeCanAllocateMemory() {
        long address = Coterie.allocateMemory(8);
        assertTrue(address != 0);

        Coterie.putLong(address, 123456789L);
        assertEquals(123456789L, Coterie.getLong(address));

        Coterie.freeMemory(address);
    }
}
