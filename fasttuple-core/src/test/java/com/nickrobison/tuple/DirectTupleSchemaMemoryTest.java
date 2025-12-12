package com.nickrobison.tuple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectTupleSchemaMemoryTest {

    @Test
    void testDestroyTuple() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .addField("b", Integer.TYPE)
                .directMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setLong(1, 100L);
        tuple.setInt(2, 50);
        
        assertEquals(100L, tuple.getLong(1));
        assertEquals(50, tuple.getInt(2));
        
        schema.destroyTuple(tuple);
    }

    @Test
    void testDestroyTypedTuple() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .implementInterface(SimpleTuple.class)
                .directMemory()
                .build();

        SimpleTuple tuple = schema.createTypedTuple(SimpleTuple.class);
        tuple.a(999L);
        assertEquals(999L, tuple.a());
        
        schema.destroyTypedTuple(tuple);
    }

    @Test
    void testDestroyTupleArray() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .directMemory()
                .build();

        FastTuple[] tuples = schema.createTupleArray(5);
        for (int i = 0; i < 5; i++) {
            tuples[i].setLong(1, i * 100L);
        }
        
        schema.destroyTupleArray(tuples);
    }

    @Test
    void testDestroyTypedTupleArray() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .implementInterface(SimpleTuple.class)
                .directMemory()
                .build();

        SimpleTuple[] tuples = schema.createTypedTupleArray(SimpleTuple.class, 3);
        for (int i = 0; i < 3; i++) {
            tuples[i].a(i * 10L);
        }
        
        schema.destroyTypedTupleArray(tuples);
    }

    @Test
    void testCreateRecordArray() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .addField("y", Integer.TYPE)
                .directMemory()
                .build();

        long recordArray = schema.createRecordArray(10);
        
        for (int i = 0; i < 10; i++) {
            long offset = recordArray + (long) i * schema.getByteSize();
            schema.setInt(offset, 0, i);
            schema.setInt(offset, 1, i * 2);
        }
        
        for (int i = 0; i < 10; i++) {
            long offset = recordArray + (long) i * schema.getByteSize();
            assertEquals(i, schema.getInt(offset, 0));
            assertEquals(i * 2, schema.getInt(offset, 1));
        }
        
        schema.destroy(recordArray);
    }

    public interface SimpleTuple {
        long a();
        void a(long value);
    }
}
