package com.nickrobison.tuple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TupleSchemaEdgeCasesTest {

    @Test
    void testGetFieldIndexNotFound() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("field1", Long.TYPE)
                .addField("field2", Integer.TYPE)
                .heapMemory()
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> schema.getFieldIndex("nonExistent"));
        assertEquals("Field nonExistent not found", ex.getMessage());
    }

    @Test
    void testGetFieldIndexFound() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("alpha", Long.TYPE)
                .addField("beta", Integer.TYPE)
                .addField("gamma", Short.TYPE)
                .heapMemory()
                .build();

        assertEquals(1, schema.getFieldIndex("alpha"));
        assertEquals(2, schema.getFieldIndex("beta"));
        assertEquals(3, schema.getFieldIndex("gamma"));
    }

    @Test
    void testGetFieldNames() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Long.TYPE)
                .addField("y", Integer.TYPE)
                .heapMemory()
                .build();

        String[] names = schema.getFieldNames();
        assertEquals(2, names.length);
        assertEquals("x", names[0]);
        assertEquals("y", names[1]);
        
        names[0] = "modified";
        String[] names2 = schema.getFieldNames();
        assertEquals("x", names2[0]);
    }

    @Test
    void testGetFieldTypes() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Long.TYPE)
                .addField("y", Integer.TYPE)
                .heapMemory()
                .build();

        Class<?>[] types = schema.getFieldTypes();
        assertEquals(2, types.length);
        assertEquals(Long.TYPE, types[0]);
        assertEquals(Integer.TYPE, types[1]);
        
        types[0] = Short.TYPE;
        Class<?>[] types2 = schema.getFieldTypes();
        assertEquals(Long.TYPE, types2[0]);
    }

    @Test
    void testTupleClass() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .heapMemory()
                .build();

        assertNotNull(schema.tupleClass());
        assertEquals(FastTuple.class, schema.tupleClass().getSuperclass());
    }

    @Test
    void testCreateTuple() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        assertNotNull(tuple);
        
        FastTuple tuple2 = schema.createTuple();
        assertNotNull(tuple2);
        assertNotSame(tuple, tuple2);
    }

    @Test
    void testPoolInitialization() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .poolOfSize(5)
                .expandingPool()
                .heapMemory()
                .build();

        TuplePool<FastTuple> pool = schema.pool();
        assertNotNull(pool);
    }
}
