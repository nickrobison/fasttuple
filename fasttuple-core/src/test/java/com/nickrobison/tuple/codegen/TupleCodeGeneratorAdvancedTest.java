package com.nickrobison.tuple.codegen;

import com.nickrobison.tuple.FastTuple;
import com.nickrobison.tuple.TupleSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TupleCodeGeneratorAdvancedTest {

    @Test
    void testIndexedBoxedGetters() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .addField("b", Integer.TYPE)
                .addField("c", Short.TYPE)
                .addField("d", Character.TYPE)
                .addField("e", Byte.TYPE)
                .addField("f", Float.TYPE)
                .addField("g", Double.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();

        tuple.set(1, 100L);
        tuple.set(2, 50);
        tuple.set(3, (short) 10);
        tuple.set(4, 'X');
        tuple.set(5, (byte) 5);
        tuple.set(6, 3.14f);
        tuple.set(7, 2.71);

        assertEquals(100L, tuple.get(1));
        assertEquals(50, tuple.get(2));
        assertEquals((short) 10, tuple.get(3));
        assertEquals('X', tuple.get(4));
        assertEquals((byte) 5, tuple.get(5));
        assertEquals(3.14f, tuple.get(6));
        assertEquals(2.71, tuple.get(7));
    }

    @Test
    void testInvalidIndexThrows() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();

        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(0));
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(2));
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(999));
        
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(0, 1L));
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(2, 1L));
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(999, 1L));
    }

    @Test
    void testTypeMismatchThrows() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("longField", Long.TYPE)
                .addField("intField", Integer.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();

        assertThrows(IllegalArgumentException.class, () -> tuple.getInt(1));
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(2));
        
        assertThrows(IllegalArgumentException.class, () -> tuple.setInt(1, 100));
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(2, 100L));
    }

    @Test
    void testAllTypedGettersAndSetters() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .addField("b", Integer.TYPE)
                .addField("c", Short.TYPE)
                .addField("d", Character.TYPE)
                .addField("e", Byte.TYPE)
                .addField("f", Float.TYPE)
                .addField("g", Double.TYPE)
                .directMemory()
                .build();

        FastTuple tuple = schema.createTuple();

        tuple.setLong(1, 1000L);
        tuple.setInt(2, 100);
        tuple.setShort(3, (short) 10);
        tuple.setChar(4, 'A');
        tuple.setByte(5, (byte) 1);
        tuple.setFloat(6, 1.5f);
        tuple.setDouble(7, 1.25);

        assertEquals(1000L, tuple.getLong(1));
        assertEquals(100, tuple.getInt(2));
        assertEquals((short) 10, tuple.getShort(3));
        assertEquals('A', tuple.getChar(4));
        assertEquals((byte) 1, tuple.getByte(5));
        assertEquals(1.5f, tuple.getFloat(6), 0.001);
        assertEquals(1.25, tuple.getDouble(7), 0.001);
        
        schema.destroyTuple(tuple);
    }

    @Test
    void testEqualsAndHashCode() throws Exception {
        TupleSchema schema1 = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .heapMemory()
                .build();

        assertNotEquals(schema1, "not a schema");
        assertEquals(schema1, schema1);
        
        assertNotEquals(schema1.hashCode(), 0);
    }
}
