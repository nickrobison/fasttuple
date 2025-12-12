package com.nickrobison.tuple;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TuplePoolAdvancedTest {

    @Test
    void testPoolCheckoutAndRelease() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .poolOfSize(3)
                .directMemory()
                .build();

        TuplePool<FastTuple> pool = schema.pool();
        
        FastTuple t1 = pool.checkout();
        FastTuple t2 = pool.checkout();
        FastTuple t3 = pool.checkout();
        
        assertNotNull(t1);
        assertNotNull(t2);
        assertNotNull(t3);
        
        t1.setLong(1, 100L);
        t2.setLong(1, 200L);
        t3.setLong(1, 300L);
        
        pool.release(t1);
        pool.release(t2);
        pool.release(t3);
        
        FastTuple t4 = pool.checkout();
        assertNotNull(t4);
        
        pool.release(t4);
        pool.close();
    }

    @Test
    void testInitializerCalledOnCheckout() throws Exception {
        AtomicInteger initCount = new AtomicInteger(0);
        
        HeapTupleSchema schema = TupleSchema.builder()
                .addField("counter", Integer.TYPE)
                .poolOfSize(2)
                .heapMemory()
                .build();

        TuplePool<FastTuple> pool = new TuplePool<>(
                2,
                false,
                schema,
                schema,
                tuple -> {
                    initCount.incrementAndGet();
                    tuple.setInt(1, 999);
                }
        );

        FastTuple t1 = pool.checkout();
        assertEquals(999, t1.getInt(1));
        assertTrue(initCount.get() > 0);
        
        t1.setInt(1, 123);
        pool.release(t1);
        
        FastTuple t2 = pool.checkout();
        assertEquals(999, t2.getInt(1));
        
        pool.release(t2);
        pool.close();
    }

    @Test
    void testPoolSizeTracking() {
        TuplePool<Long> pool = new TuplePool<>(5, true,
                size -> {
                    Long[] ary = new Long[size];
                    Arrays.fill(ary, 0L);
                    return ary;
                },
                ary -> {});

        assertEquals(0, pool.getSize());
        
        Long t1 = pool.checkout();
        assertEquals(5, pool.getSize());
        
        pool.release(t1);
        
        for (int i = 0; i < 5; i++) {
            pool.checkout();
        }
        assertEquals(5, pool.getSize());
        
        pool.checkout();
        assertEquals(10, pool.getSize());
        
        pool.close();
    }

    @Test
    void testNonExpandingPoolThrowsWhenExhausted() {
        TuplePool<Integer> pool = new TuplePool<>(2, false,
                size -> {
                    Integer[] ary = new Integer[size];
                    Arrays.fill(ary, 0);
                    return ary;
                },
                ary -> {});

        Integer t1 = pool.checkout();
        Integer t2 = pool.checkout();
        
        assertThrows(IllegalStateException.class, pool::checkout);
        
        pool.release(t1);
        Integer t3 = pool.checkout();
        assertNotNull(t3);
        
        pool.release(t2);
        pool.release(t3);
        pool.close();
    }
}
