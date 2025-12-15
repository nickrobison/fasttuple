package com.nickrobison.tuple.codegen;

import com.nickrobison.tuple.FastTuple;
import com.nickrobison.tuple.TupleSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TupleAllocatorGeneratorTest {

    @Test
    void testCreateAllocatorForHeapTuple() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple1 = schema.createTuple();
        FastTuple tuple2 = schema.createTuple();

        assertNotNull(tuple1);
        assertNotNull(tuple2);
        assertNotSame(tuple1, tuple2);

        tuple1.setLong(1, 100L);
        tuple2.setLong(1, 200L);

        assertEquals(100L, tuple1.getLong(1));
        assertEquals(200L, tuple2.getLong(1));
    }

    @Test
    void testCreateAllocatorForDirectTuple() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .addField("y", Integer.TYPE)
                .directMemory()
                .build();

        FastTuple tuple1 = schema.createTuple();
        FastTuple tuple2 = schema.createTuple();

        assertNotNull(tuple1);
        assertNotNull(tuple2);
        assertNotSame(tuple1, tuple2);

        tuple1.setInt(1, 10);
        tuple1.setInt(2, 20);
        tuple2.setInt(1, 30);
        tuple2.setInt(2, 40);

        assertEquals(10, tuple1.getInt(1));
        assertEquals(20, tuple1.getInt(2));
        assertEquals(30, tuple2.getInt(1));
        assertEquals(40, tuple2.getInt(2));
        
        schema.destroyTuple(tuple1);
        schema.destroyTuple(tuple2);
    }

    @Test
    void testEqualsAndHashCode() throws Exception {
        TupleSchema schema1 = TupleSchema.builder()
                .addField("field", Long.TYPE)
                .heapMemory()
                .build();

        TupleSchema schema2 = TupleSchema.builder()
                .addField("field", Long.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple1a = schema1.createTuple();
        FastTuple tuple1b = schema1.createTuple();
        FastTuple tuple2 = schema2.createTuple();

        assertNotNull(tuple1a);
        assertNotNull(tuple1b);
        assertNotNull(tuple2);
        
        assertEquals(tuple1a.getClass(), tuple1b.getClass());
        assertEquals(tuple2.getClass(), tuple2.getClass());
    }
}
