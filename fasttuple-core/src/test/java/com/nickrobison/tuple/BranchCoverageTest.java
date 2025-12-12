package com.nickrobison.tuple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests specifically targeting branch coverage gaps
 */
class BranchCoverageTest {

    @Test
    void testIndexedGettersWithInvalidIndex() throws Exception {
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
        
        // Test all typed getters with invalid index
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(0));
        assertThrows(IllegalArgumentException.class, () -> tuple.getInt(0));
        assertThrows(IllegalArgumentException.class, () -> tuple.getShort(0));
        assertThrows(IllegalArgumentException.class, () -> tuple.getChar(0));
        assertThrows(IllegalArgumentException.class, () -> tuple.getByte(0));
        assertThrows(IllegalArgumentException.class, () -> tuple.getFloat(0));
        assertThrows(IllegalArgumentException.class, () -> tuple.getDouble(0));
        assertThrows(IllegalArgumentException.class, () -> tuple.get(0));
        
        // Test all typed setters with invalid index
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(0, 1L));
        assertThrows(IllegalArgumentException.class, () -> tuple.setInt(0, 1));
        assertThrows(IllegalArgumentException.class, () -> tuple.setShort(0, (short)1));
        assertThrows(IllegalArgumentException.class, () -> tuple.setChar(0, 'a'));
        assertThrows(IllegalArgumentException.class, () -> tuple.setByte(0, (byte)1));
        assertThrows(IllegalArgumentException.class, () -> tuple.setFloat(0, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> tuple.setDouble(0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> tuple.set(0, 1L));
    }

    @Test
    void testTypeMismatchForAllTypes() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("longField", Long.TYPE)
                .addField("intField", Integer.TYPE)
                .addField("shortField", Short.TYPE)
                .addField("charField", Character.TYPE)
                .addField("byteField", Byte.TYPE)
                .addField("floatField", Float.TYPE)
                .addField("doubleField", Double.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        
        // Test getLong with wrong indices
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(2));
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(3));
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(4));
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(5));
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(6));
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(7));
        
        // Test getInt with wrong indices
        assertThrows(IllegalArgumentException.class, () -> tuple.getInt(1));
        assertThrows(IllegalArgumentException.class, () -> tuple.getInt(3));
        assertThrows(IllegalArgumentException.class, () -> tuple.getInt(4));
        assertThrows(IllegalArgumentException.class, () -> tuple.getInt(5));
        assertThrows(IllegalArgumentException.class, () -> tuple.getInt(6));
        assertThrows(IllegalArgumentException.class, () -> tuple.getInt(7));
        
        // Test getShort with wrong indices
        assertThrows(IllegalArgumentException.class, () -> tuple.getShort(1));
        assertThrows(IllegalArgumentException.class, () -> tuple.getShort(2));
        assertThrows(IllegalArgumentException.class, () -> tuple.getShort(4));
        assertThrows(IllegalArgumentException.class, () -> tuple.getShort(5));
        assertThrows(IllegalArgumentException.class, () -> tuple.getShort(6));
        assertThrows(IllegalArgumentException.class, () -> tuple.getShort(7));
        
        // Test getChar with wrong indices
        assertThrows(IllegalArgumentException.class, () -> tuple.getChar(1));
        assertThrows(IllegalArgumentException.class, () -> tuple.getChar(2));
        assertThrows(IllegalArgumentException.class, () -> tuple.getChar(3));
        assertThrows(IllegalArgumentException.class, () -> tuple.getChar(5));
        assertThrows(IllegalArgumentException.class, () -> tuple.getChar(6));
        assertThrows(IllegalArgumentException.class, () -> tuple.getChar(7));
        
        // Test getByte with wrong indices
        assertThrows(IllegalArgumentException.class, () -> tuple.getByte(1));
        assertThrows(IllegalArgumentException.class, () -> tuple.getByte(2));
        assertThrows(IllegalArgumentException.class, () -> tuple.getByte(3));
        assertThrows(IllegalArgumentException.class, () -> tuple.getByte(4));
        assertThrows(IllegalArgumentException.class, () -> tuple.getByte(6));
        assertThrows(IllegalArgumentException.class, () -> tuple.getByte(7));
        
        // Test getFloat with wrong indices
        assertThrows(IllegalArgumentException.class, () -> tuple.getFloat(1));
        assertThrows(IllegalArgumentException.class, () -> tuple.getFloat(2));
        assertThrows(IllegalArgumentException.class, () -> tuple.getFloat(3));
        assertThrows(IllegalArgumentException.class, () -> tuple.getFloat(4));
        assertThrows(IllegalArgumentException.class, () -> tuple.getFloat(5));
        assertThrows(IllegalArgumentException.class, () -> tuple.getFloat(7));
        
        // Test getDouble with wrong indices
        assertThrows(IllegalArgumentException.class, () -> tuple.getDouble(1));
        assertThrows(IllegalArgumentException.class, () -> tuple.getDouble(2));
        assertThrows(IllegalArgumentException.class, () -> tuple.getDouble(3));
        assertThrows(IllegalArgumentException.class, () -> tuple.getDouble(4));
        assertThrows(IllegalArgumentException.class, () -> tuple.getDouble(5));
        assertThrows(IllegalArgumentException.class, () -> tuple.getDouble(6));
    }

    @Test
    void testSettersTypeMismatch() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("longField", Long.TYPE)
                .addField("intField", Integer.TYPE)
                .addField("shortField", Short.TYPE)
                .addField("charField", Character.TYPE)
                .addField("byteField", Byte.TYPE)
                .addField("floatField", Float.TYPE)
                .addField("doubleField", Double.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        
        // Test setInt on non-int fields
        assertThrows(IllegalArgumentException.class, () -> tuple.setInt(1, 1));
        assertThrows(IllegalArgumentException.class, () -> tuple.setInt(3, 1));
        assertThrows(IllegalArgumentException.class, () -> tuple.setInt(4, 1));
        assertThrows(IllegalArgumentException.class, () -> tuple.setInt(5, 1));
        assertThrows(IllegalArgumentException.class, () -> tuple.setInt(6, 1));
        assertThrows(IllegalArgumentException.class, () -> tuple.setInt(7, 1));
        
        // Test setLong on non-long fields
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(2, 1L));
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(3, 1L));
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(4, 1L));
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(5, 1L));
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(6, 1L));
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(7, 1L));
    }

    @Test
    void testDirectTupleWithAllAccessors() throws Exception {
        DirectTupleSchema schema = (DirectTupleSchema) TupleSchema.builder()
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
        
        // Test all type mismatches for direct tuple
        assertThrows(IllegalArgumentException.class, () -> tuple.getInt(1));
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(2));
        assertThrows(IllegalArgumentException.class, () -> tuple.getShort(1));
        assertThrows(IllegalArgumentException.class, () -> tuple.getChar(1));
        assertThrows(IllegalArgumentException.class, () -> tuple.getByte(1));
        assertThrows(IllegalArgumentException.class, () -> tuple.getFloat(1));
        assertThrows(IllegalArgumentException.class, () -> tuple.getDouble(1));
        
        schema.destroyTuple(tuple);
    }
}
