package com.nickrobison.tuple.codegen;

import com.nickrobison.tuple.FastTuple;
import com.nickrobison.tuple.TupleSchema;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to kill mutations in code generator classes
 * Focuses on verifying generated code structure and behavior
 */
class CodeGeneratorMutationTest {

    @Test
    void testGeneratedClassHasCorrectNumberOfFields() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .addField("b", Integer.TYPE)
                .addField("c", Short.TYPE)
                .heapMemory()
                .build();
        
        Class<?> tupleClass = schema.tupleClass();
        Field[] fields = tupleClass.getDeclaredFields();
        
        // Should have fields for a, b, c (at minimum)
        assertTrue(fields.length >= 3, "Expected at least 3 fields, found " + fields.length);
    }

    @Test
    void testGeneratedClassHasGettersAndSetters() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("x", Long.TYPE)
                .addField("y", Integer.TYPE)
                .heapMemory()
                .build();
        
        Class<?> tupleClass = schema.tupleClass();
        
        // Verify getter methods exist
        Method getX = tupleClass.getMethod("x");
        assertNotNull(getX);
        assertEquals(long.class, getX.getReturnType());
        
        Method getY = tupleClass.getMethod("y");
        assertNotNull(getY);
        assertEquals(int.class, getY.getReturnType());
        
        // Verify setter methods exist
        Method setX = tupleClass.getMethod("x", long.class);
        assertNotNull(setX);
        
        Method setY = tupleClass.getMethod("y", int.class);
        assertNotNull(setY);
    }

    @Test
    void testGeneratedClassHasIndexedAccessors() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("field", Long.TYPE)
                .heapMemory()
                .build();
        
        Class<?> tupleClass = schema.tupleClass();
        
        // Verify indexed getter exists
        Method getLong = tupleClass.getMethod("getLong", int.class);
        assertNotNull(getLong);
        assertEquals(long.class, getLong.getReturnType());
        
        // Verify indexed setter exists
        Method setLong = tupleClass.getMethod("setLong", int.class, long.class);
        assertNotNull(setLong);
        
        // Verify generic get/set
        Method get = tupleClass.getMethod("get", int.class);
        assertNotNull(get);
        assertEquals(Object.class, get.getReturnType());
        
        Method set = tupleClass.getMethod("set", int.class, Object.class);
        assertNotNull(set);
    }

    @Test
    void testAllPrimitiveTypeCodeGeneration() throws Exception {
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
        
        Class<?> tupleClass = schema.tupleClass();
        
        // Verify all primitive accessors are generated
        assertNotNull(tupleClass.getMethod("getByte", int.class));
        assertNotNull(tupleClass.getMethod("setByte", int.class, byte.class));
        
        assertNotNull(tupleClass.getMethod("getChar", int.class));
        assertNotNull(tupleClass.getMethod("setChar", int.class, char.class));
        
        assertNotNull(tupleClass.getMethod("getShort", int.class));
        assertNotNull(tupleClass.getMethod("setShort", int.class, short.class));
        
        assertNotNull(tupleClass.getMethod("getInt", int.class));
        assertNotNull(tupleClass.getMethod("setInt", int.class, int.class));
        
        assertNotNull(tupleClass.getMethod("getFloat", int.class));
        assertNotNull(tupleClass.getMethod("setFloat", int.class, float.class));
        
        assertNotNull(tupleClass.getMethod("getLong", int.class));
        assertNotNull(tupleClass.getMethod("setLong", int.class, long.class));
        
        assertNotNull(tupleClass.getMethod("getDouble", int.class));
        assertNotNull(tupleClass.getMethod("setDouble", int.class, double.class));
    }

    @Test
    void testGeneratedCodeThrowsOnInvalidIndex() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("field", Long.TYPE)
                .heapMemory()
                .build();
        
        FastTuple tuple = schema.createTuple();
        
        // Test that generated code properly validates indices
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(0));
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(2));
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(0, 1L));
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(2, 1L));
    }

    @Test
    void testGeneratedCodeThrowsOnTypeMismatch() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("longField", Long.TYPE)
                .addField("intField", Integer.TYPE)
                .heapMemory()
                .build();
        
        FastTuple tuple = schema.createTuple();
        
        // Test that generated code validates types
        assertThrows(IllegalArgumentException.class, () -> tuple.getInt(1)); // field 1 is Long
        assertThrows(IllegalArgumentException.class, () -> tuple.getLong(2)); // field 2 is Int
        assertThrows(IllegalArgumentException.class, () -> tuple.setInt(1, 1)); // field 1 is Long
        assertThrows(IllegalArgumentException.class, () -> tuple.setLong(2, 1L)); // field 2 is Int
    }

    @Test
    void testBoxedGettersAndSetters() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("a", Long.TYPE)
                .addField("b", Integer.TYPE)
                .heapMemory()
                .build();
        
        FastTuple tuple = schema.createTuple();
        
        // Test boxed set/get
        tuple.set(1, 100L);
        tuple.set(2, 50);
        
        Object val1 = tuple.get(1);
        Object val2 = tuple.get(2);
        
        assertEquals(100L, val1);
        assertEquals(50, val2);
        assertTrue(val1 instanceof Long);
        assertTrue(val2 instanceof Integer);
    }

    @Test
    void testDirectTupleCodeGeneration() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("value", Long.TYPE)
                .directMemory()
                .build();
        
        Class<?> tupleClass = schema.tupleClass();
        assertNotNull(tupleClass);
        
        // Direct tuples should have address field
        FastTuple tuple = schema.createTuple();
        assertNotNull(tuple);
        assertEquals(tupleClass, tuple.getClass());
        
        // Verify functional behavior
        tuple.setLong(1, 999L);
        assertEquals(999L, tuple.getLong(1));
        
        schema.destroyTuple(tuple);
    }

    @Test
    void testInterfaceMethodsAreImplemented() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("val", Long.TYPE)
                .implementInterface(TestIface.class)
                .heapMemory()
                .build();
        
        Class<?> tupleClass = schema.tupleClass();
        assertTrue(TestIface.class.isAssignableFrom(tupleClass));
        
        // Verify interface methods are present
        Method valGetter = tupleClass.getMethod("val");
        assertNotNull(valGetter);
        assertEquals(long.class, valGetter.getReturnType());
        
        Method valSetter = tupleClass.getMethod("val", long.class);
        assertNotNull(valSetter);
        
        // Verify functionality
        TestIface tuple = schema.createTypedTuple(TestIface.class);
        tuple.val(12345L);
        assertEquals(12345L, tuple.val());
    }

    @Test
    void testMultipleFieldsGenerateCorrectly() throws Exception {
        TupleSchema schema = TupleSchema.builder()
                .addField("f1", Long.TYPE)
                .addField("f2", Long.TYPE)
                .addField("f3", Long.TYPE)
                .addField("f4", Long.TYPE)
                .addField("f5", Long.TYPE)
                .heapMemory()
                .build();
        
        FastTuple tuple = schema.createTuple();
        
        // Set all fields to different values
        for (int i = 1; i <= 5; i++) {
            tuple.setLong(i, i * 100L);
        }
        
        // Verify all values are correctly stored
        for (int i = 1; i <= 5; i++) {
            assertEquals(i * 100L, tuple.getLong(i));
        }
    }

    public interface TestIface {
        long val();
        void val(long v);
    }
}
