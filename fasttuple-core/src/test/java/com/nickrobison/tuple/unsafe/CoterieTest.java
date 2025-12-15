package com.nickrobison.tuple.unsafe;

import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.*;

class CoterieTest {

    @Test
    void testUnsafeAccessor() {
        Unsafe unsafe = Coterie.unsafe();
        assertNotNull(unsafe);
        
        Unsafe unsafe2 = Coterie.unsafe();
        assertSame(unsafe, unsafe2);
    }

    @Test
    void testUnsafeCanAllocateMemory() {
        Unsafe unsafe = Coterie.unsafe();
        
        long address = unsafe.allocateMemory(8);
        assertTrue(address != 0);
        
        unsafe.putLong(address, 123456789L);
        assertEquals(123456789L, unsafe.getLong(address));
        
        unsafe.freeMemory(address);
    }
}
