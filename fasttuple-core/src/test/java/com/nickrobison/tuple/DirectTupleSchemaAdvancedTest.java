package com.nickrobison.tuple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectTupleSchemaAdvancedTest {

    @Test
    void testAllPrimitiveAccessors() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("aByte", Byte.TYPE)
                .addField("aChar", Character.TYPE)
                .addField("aShort", Short.TYPE)
                .addField("aInt", Integer.TYPE)
                .addField("aFloat", Float.TYPE)
                .addField("aLong", Long.TYPE)
                .addField("aDouble", Double.TYPE)
                .directMemory()
                .build();

        long record = schema.createRecord();

        schema.setByte(record, 0, (byte) 42);
        schema.setChar(record, 1, 'Z');
        schema.setShort(record, 2, (short) 1000);
        schema.setInt(record, 3, 50000);
        schema.setFloat(record, 4, 3.14f);
        schema.setLong(record, 5, 9999999L);
        schema.setDouble(record, 6, 2.71828);

        assertEquals((byte) 42, schema.getByte(record, 0));
        assertEquals('Z', schema.getChar(record, 1));
        assertEquals((short) 1000, schema.getShort(record, 2));
        assertEquals(50000, schema.getInt(record, 3));
        assertEquals(3.14f, schema.getFloat(record, 4), 0.001);
        assertEquals(9999999L, schema.getLong(record, 5));
        assertEquals(2.71828, schema.getDouble(record, 6), 0.00001);

        schema.destroy(record);
    }

    @Test
    void testCreateTupleWithAddress() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .directMemory()
                .build();

        long address = schema.createRecord();
        schema.setLong(address, 0, 123456L);

        FastTuple tuple = schema.createTuple(address);
        assertEquals(123456L, tuple.getLong(1));

        tuple.setLong(1, 654321L);
        assertEquals(654321L, schema.getLong(address, 0));
        
        schema.destroy(address);
    }

    @Test
    void testDestroyWithTuple() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .addField("y", Integer.TYPE)
                .directMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        tuple.setInt(1, 100);
        tuple.setInt(2, 200);

        assertEquals(100, tuple.getInt(1));
        assertEquals(200, tuple.getInt(2));

        schema.destroy(tuple);
    }

    @Test
    void testEqualsAndHashCode() throws Exception {
        DirectTupleSchema schema1 = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .addField("b", Integer.TYPE)
                .directMemory()
                .build();

        DirectTupleSchema schema2 = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .addField("c", Integer.TYPE)
                .directMemory()
                .build();

        assertEquals(schema1, schema1);
        assertNotEquals(schema1, schema2);
        assertNotEquals(schema1, "not a schema");

        assertNotEquals(schema1.hashCode(), 0);
        
        int[] layout1 = schema1.getLayout();
        int[] layout2 = schema1.getLayout();
        assertArrayEquals(layout1, layout2);
        assertNotSame(layout1, layout2);
    }

    @Test
    void testGetByteSize() throws Exception {
        DirectTupleSchema schema1 = TupleSchema.builder()
                .addField("a", Byte.TYPE)
                .directMemory()
                .build();
        
        assertTrue(schema1.getByteSize() >= 1);

        DirectTupleSchema schema2 = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .addField("b", Long.TYPE)
                .directMemory()
                .padToWordSize(64)
                .build();

        assertEquals(64, schema2.getByteSize());
    }

    @Test
    void testClassLoader() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .directMemory()
                .build();

        assertNotNull(schema.getClassLoader());
        assertNotNull(schema.tupleClass());
    }

    public interface TestInterface {
        long value();
        void value(long v);
    }

    @Test
    void testCreateTypedTuple() throws Exception {
        DirectTupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .implementInterface(TestInterface.class)
                .directMemory()
                .build();

        TestInterface typed = schema.createTypedTuple(TestInterface.class);
        assertNotNull(typed);
        
        typed.value(999L);
        assertEquals(999L, typed.value());
        
        schema.destroyTypedTuple(typed);
    }
}
