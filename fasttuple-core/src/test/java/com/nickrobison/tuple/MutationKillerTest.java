package com.nickrobison.tuple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests specifically designed to kill mutations detected by PIT
 * Focus on edge cases, boundary conditions, and side effect verification
 */
class MutationKillerTest {

    @Test
    void testTupleSchemaBuilderValidation() throws Exception {
        // Test that builder properly validates field count matches
        TupleSchema.Builder builder = TupleSchema.builder();
        builder.addField("a", Long.TYPE);
        
        // Should succeed with matching counts
        TupleSchema schema = builder.heapMemory().build();
        assertNotNull(schema);
        assertEquals(1, schema.getFieldNames().length);
        assertEquals(1, schema.getFieldTypes().length);
    }

    @Test
    void testGetFieldIndexBoundaries() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("first", Long.TYPE)
                .addField("middle", Integer.TYPE)
                .addField("last", Short.TYPE)
                .heapMemory()
                .build();
        
        // Test all valid indices (1-based)
        assertEquals(1, schema.getFieldIndex("first"));
        assertEquals(2, schema.getFieldIndex("middle"));
        assertEquals(3, schema.getFieldIndex("last"));
        
        // Test invalid field name
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> schema.getFieldIndex("nonexistent"));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void testTupleSchemaToString() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Long.TYPE)
                .addField("y", Integer.TYPE)
                .heapMemory()
                .build();
        
        String str = schema.toString();
        assertNotNull(str);
        assertFalse(str.isEmpty());
        assertTrue(str.contains("x") || str.contains("y") || str.contains("TupleSchema"));
    }

    @Test
    void testFieldNamesAndTypesAreDefensiveCopies() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("field1", Long.TYPE)
                .addField("field2", Integer.TYPE)
                .heapMemory()
                .build();
        
        String[] names1 = schema.getFieldNames();
        String[] names2 = schema.getFieldNames();
        
        // Should be different array instances (defensive copy)
        assertNotSame(names1, names2);
        assertArrayEquals(names1, names2);
        
        // Modifying returned array shouldn't affect schema
        names1[0] = "modified";
        String[] names3 = schema.getFieldNames();
        assertEquals("field1", names3[0]);
        
        Class<?>[] types1 = schema.getFieldTypes();
        Class<?>[] types2 = schema.getFieldTypes();
        
        // Should be different array instances (defensive copy)
        assertNotSame(types1, types2);
        assertArrayEquals(types1, types2);
    }

    @Test
    void testTupleSchemaEqualsAndHashCode() throws Exception {
        TupleSchema schema1 = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .heapMemory()
                .build();
        
        TupleSchema schema2 = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .heapMemory()
                .build();
        
        TupleSchema schema3 = TupleSchema.builder()
                .addField("b", Long.TYPE)
                .heapMemory()
                .build();
        
        // Same schema instance equals itself
        assertEquals(schema1, schema1);
        
        // Different instances with same config might not be equal (implementation dependent)
        // But they should have valid hashCodes
        assertNotEquals(0, schema1.hashCode());
        assertNotEquals(0, schema2.hashCode());
        assertNotEquals(0, schema3.hashCode());
        
        // Schema should not equal non-schema objects
        assertNotEquals(schema1, "string");
        assertNotEquals(schema1, null);
        assertNotEquals(schema1, 123);
    }

    @Test
    void testPoolInitializerInvocation() throws Exception {
        HeapTupleSchema schema = TupleSchema.builder()
                .addField("value", Integer.TYPE)
                .poolOfSize(2)
                .heapMemory()
                .build();
        
        TuplePool<FastTuple> pool = schema.pool();
        assertNotNull(pool);
        
        // Check that pool is actually initialized
        FastTuple t1 = pool.checkout();
        assertNotNull(t1);
        
        // Verify tuple from pool is functional
        t1.setInt(1, 42);
        assertEquals(42, t1.getInt(1));
        
        pool.release(t1);
        
        // Check that we can get tuple back from pool
        FastTuple t2 = pool.checkout();
        assertNotNull(t2);
        
        pool.release(t2);
        pool.close();
    }

    @Test
    void testTupleClassLoader() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("field", Long.TYPE)
                .heapMemory()
                .build();
        
        ClassLoader loader = schema.getClassLoader();
        assertNotNull(loader);
        
        Class<?> tupleClass = schema.tupleClass();
        assertNotNull(tupleClass);
        assertSame(loader, tupleClass.getClassLoader());
    }

    @Test
    void testInterfaceImplementationVerification() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .implementInterface(TestInterface.class)
                .heapMemory()
                .build();
        
        Class<?> tupleClass = schema.tupleClass();
        assertTrue(TestInterface.class.isAssignableFrom(tupleClass));
        
        FastTuple tuple = schema.createTuple();
        assertTrue(tuple instanceof TestInterface);
    }

    @Test
    void testCreateTypedTupleValidation() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Integer.TYPE)
                .implementInterface(TestInterface2.class)
                .heapMemory()
                .build();
        
        TestInterface2 typed = schema.createTypedTuple(TestInterface2.class);
        assertNotNull(typed);
        
        typed.x(999);
        assertEquals(999, typed.x());
    }

    public interface TestInterface {
        long value();
        void value(long v);
    }

    public interface TestInterface2 {
        int x();
        void x(int v);
    }
}
