package com.nickrobison.tuple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeapTupleSchemaAdvancedTest {

    public interface TestInterface {
        int value();
        void value(int v);
    }

    @Test
    void testCreateTypedTuple() throws Exception {
        HeapTupleSchema schema = TupleSchema.builder()
                .addField("value", Integer.TYPE)
                .implementInterface(TestInterface.class)
                .heapMemory()
                .build();

        TestInterface typed = schema.createTypedTuple(TestInterface.class);
        assertNotNull(typed);
        
        typed.value(42);
        assertEquals(42, typed.value());
    }

    @Test
    void testCreateSingleTuple() throws Exception {
        HeapTupleSchema schema = TupleSchema.builder()
                .addField("x", Long.TYPE)
                .addField("y", Long.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        assertNotNull(tuple);
        
        tuple.setLong(1, 100L);
        tuple.setLong(2, 200L);
        
        assertEquals(100L, tuple.getLong(1));
        assertEquals(200L, tuple.getLong(2));
        
        schema.destroyTuple(tuple);
    }

    @Test
    void testDestroyTypedTuple() throws Exception {
        HeapTupleSchema schema = TupleSchema.builder()
                .addField("value", Integer.TYPE)
                .implementInterface(TestInterface.class)
                .heapMemory()
                .build();

        TestInterface typed = schema.createTypedTuple(TestInterface.class);
        typed.value(123);
        
        schema.destroyTypedTuple(typed);
    }

    @Test
    void testDestroyTupleArray() throws Exception {
        HeapTupleSchema schema = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .heapMemory()
                .build();

        FastTuple[] tuples = schema.createTupleArray(5);
        for (int i = 0; i < tuples.length; i++) {
            tuples[i].setLong(1, i * 10L);
        }
        
        schema.destroyTupleArray(tuples);
    }

    @Test
    void testDestroyTypedTupleArray() throws Exception {
        HeapTupleSchema schema = TupleSchema.builder()
                .addField("value", Integer.TYPE)
                .implementInterface(TestInterface.class)
                .heapMemory()
                .build();

        TestInterface[] tuples = schema.createTypedTupleArray(TestInterface.class, 3);
        for (int i = 0; i < tuples.length; i++) {
            tuples[i].value(i * 5);
        }
        
        schema.destroyTypedTupleArray(tuples);
    }

    @Test
    void testClassLoader() throws Exception {
        HeapTupleSchema schema = TupleSchema.builder()
                .addField("field", Long.TYPE)
                .heapMemory()
                .build();

        assertNotNull(schema.getClassLoader());
        assertNotNull(schema.tupleClass());
    }
}
