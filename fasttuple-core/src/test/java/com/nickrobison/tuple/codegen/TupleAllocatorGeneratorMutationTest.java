package com.nickrobison.tuple.codegen;

import com.nickrobison.tuple.FastTuple;
import com.nickrobison.tuple.TupleSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for TupleAllocatorGenerator to kill all mutations
 */
class TupleAllocatorGeneratorMutationTest {

    @Test
    void testAllocatorCreatesUniqueInstances() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple1 = schema.createTuple();
        FastTuple tuple2 = schema.createTuple();
        FastTuple tuple3 = schema.createTuple();

        assertNotNull(tuple1);
        assertNotNull(tuple2);
        assertNotNull(tuple3);
        
        // Each tuple should be a distinct instance
        assertNotSame(tuple1, tuple2);
        assertNotSame(tuple2, tuple3);
        assertNotSame(tuple1, tuple3);
        
        // But should be same class
        assertEquals(tuple1.getClass(), tuple2.getClass());
        assertEquals(tuple2.getClass(), tuple3.getClass());
    }

    @Test
    void testAllocatorWithDirectMemory() throws Exception {
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
        
        // Each should have independent memory
        tuple1.setInt(1, 10);
        tuple2.setInt(1, 20);
        
        assertEquals(10, tuple1.getInt(1));
        assertEquals(20, tuple2.getInt(1));
        
        schema.destroyTuple(tuple1);
        schema.destroyTuple(tuple2);
    }

    @Test
    void testAllocatorWithAllPrimitiveTypes() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("aByte", Byte.TYPE)
                .addField("aChar", Character.TYPE)
                .addField("aShort", Short.TYPE)
                .addField("anInt", Integer.TYPE)
                .addField("aFloat", Float.TYPE)
                .addField("aLong", Long.TYPE)
                .addField("aDouble", Double.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        assertNotNull(tuple);
        
        // Verify all fields are accessible
        tuple.setByte(1, (byte) 1);
        tuple.setChar(2, 'A');
        tuple.setShort(3, (short) 10);
        tuple.setInt(4, 100);
        tuple.setFloat(5, 1.5f);
        tuple.setLong(6, 1000L);
        tuple.setDouble(7, 2.5);
        
        assertEquals((byte) 1, tuple.getByte(1));
        assertEquals('A', tuple.getChar(2));
        assertEquals((short) 10, tuple.getShort(3));
        assertEquals(100, tuple.getInt(4));
        assertEquals(1.5f, tuple.getFloat(5), 0.001);
        assertEquals(1000L, tuple.getLong(6));
        assertEquals(2.5, tuple.getDouble(7), 0.001);
    }

    @Test
    void testAllocatorWithMaximumFields() throws Exception {
        TupleSchema.Builder builder = TupleSchema.builder();
        
        // Add many fields
        for (int i = 0; i < 20; i++) {
            builder.addField("field" + i, Long.TYPE);
        }
        
        TupleSchema schema = builder.heapMemory().build();
        FastTuple tuple = schema.createTuple();
        
        assertNotNull(tuple);
        
        // Verify all fields work
        for (int i = 1; i <= 20; i++) {
            tuple.setLong(i, i * 10L);
        }
        
        for (int i = 1; i <= 20; i++) {
            assertEquals(i * 10L, tuple.getLong(i));
        }
    }

    @Test
    void testAllocatorCreatesCorrectClass() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("data", Long.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        
        // Verify tuple is instance of FastTuple
        assertNotNull(tuple);
        assertTrue(FastTuple.class.isInstance(tuple));
        
        // Verify class has correct superclass
        assertEquals(FastTuple.class, tuple.getClass().getSuperclass());
    }

    @Test
    void testAllocatorWithInterface() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("value", Integer.TYPE)
                .implementInterface(TestInterface.class)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        assertNotNull(tuple);
        
        // Should be instance of interface
        assertTrue(tuple instanceof TestInterface);
        
        TestInterface typed = (TestInterface) tuple;
        typed.value(42);
        assertEquals(42, typed.value());
    }

    @Test
    void testCreateTypedTuple() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Long.TYPE)
                .implementInterface(AnotherInterface.class)
                .heapMemory()
                .build();

        AnotherInterface tuple = schema.createTypedTuple(AnotherInterface.class);
        assertNotNull(tuple);
        
        tuple.x(999L);
        assertEquals(999L, tuple.x());
    }

    @Test
    void testAllocatorConsistency() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("field", Long.TYPE)
                .heapMemory()
                .build();

        // Create multiple tuples and verify they're all functional
        for (int i = 0; i < 10; i++) {
            FastTuple tuple = schema.createTuple();
            assertNotNull(tuple);
            
            long value = i * 100L;
            tuple.setLong(1, value);
            assertEquals(value, tuple.getLong(1));
        }
    }

    @Test
    void testAllocatorWithTypedTuple() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("value", Integer.TYPE)
                .implementInterface(TestInterface.class)
                .heapMemory()
                .build();

        TestInterface tuple = schema.createTypedTuple(TestInterface.class);
        assertNotNull(tuple);
        
        tuple.value(777);
        assertEquals(777, tuple.value());
    }

    @Test
    void testAllocatorCreatesIndependentState() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("counter", Integer.TYPE)
                .heapMemory()
                .build();

        FastTuple t1 = schema.createTuple();
        FastTuple t2 = schema.createTuple();
        
        t1.setInt(1, 100);
        t2.setInt(1, 200);
        
        // State should be independent
        assertEquals(100, t1.getInt(1));
        assertEquals(200, t2.getInt(1));
        
        t1.setInt(1, 999);
        
        // Changing t1 shouldn't affect t2
        assertEquals(999, t1.getInt(1));
        assertEquals(200, t2.getInt(1));
    }

    @Test
    void testAllocatorHandlesMixedTypes() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("longField", Long.TYPE)
                .addField("intField", Integer.TYPE)
                .addField("shortField", Short.TYPE)
                .addField("byteField", Byte.TYPE)
                .heapMemory()
                .build();

        FastTuple tuple = schema.createTuple();
        
        tuple.setLong(1, Long.MAX_VALUE);
        tuple.setInt(2, Integer.MAX_VALUE);
        tuple.setShort(3, Short.MAX_VALUE);
        tuple.setByte(4, Byte.MAX_VALUE);
        
        assertEquals(Long.MAX_VALUE, tuple.getLong(1));
        assertEquals(Integer.MAX_VALUE, tuple.getInt(2));
        assertEquals(Short.MAX_VALUE, tuple.getShort(3));
        assertEquals(Byte.MAX_VALUE, tuple.getByte(4));
    }

    public interface TestInterface {
        int value();
        void value(int v);
    }

    public interface AnotherInterface {
        long x();
        void x(long v);
    }
}
