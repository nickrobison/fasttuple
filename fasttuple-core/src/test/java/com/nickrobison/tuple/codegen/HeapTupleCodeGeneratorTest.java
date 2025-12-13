package com.nickrobison.tuple.codegen;

import com.nickrobison.tuple.FastTuple;
import com.nickrobison.tuple.HeapTupleSchema;
import com.nickrobison.tuple.TupleSchema;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by cliff on 5/9/14.
 */
public class HeapTupleCodeGeneratorTest {
    @Test
    public void testAccessorsGetGenerated() throws Exception {
        HeapTupleSchema schema = TupleSchema.builder().
                addField("a", Long.TYPE).
                addField("b", Integer.TYPE).
                addField("c", Short.TYPE).
                addField("d", Character.TYPE).
                addField("e", Byte.TYPE).
                addField("f", Float.TYPE).
                addField("g", Double.TYPE).
                addField("h", String.class).
                heapMemory().
                build();

        HeapTupleCodeGenerator codegen = new HeapTupleCodeGenerator(null, schema.getFieldNames(), schema.getFieldTypes());
        Class<?> clazz = codegen.cookToClass();
        assertNullConstructorGenerated(clazz);
        assertGetterAndSetterGenerated(clazz, "a", long.class);
        assertGetterAndSetterGenerated(clazz, "b", int.class);
        assertGetterAndSetterGenerated(clazz, "c", short.class);
        assertGetterAndSetterGenerated(clazz, "d", char.class);
        assertGetterAndSetterGenerated(clazz, "e", byte.class);
        assertGetterAndSetterGenerated(clazz, "f", float.class);
        assertGetterAndSetterGenerated(clazz, "g", double.class);
        assertGetterAndSetterGenerated(clazz, "h", String.class);
    }

    @Test
    public void testAccessorsWork() throws Exception {
        TupleSchema schema = TupleSchema.builder().
                addField("a", Long.TYPE).
                addField("b", Integer.TYPE).
                addField("c", Short.TYPE).
                addField("d", Character.TYPE).
                addField("e", Byte.TYPE).
                addField("f", Float.TYPE).
                addField("g", Double.TYPE).
                addField("h", String.class).
                heapMemory().
                build();
        FastTuple tuple = schema.createTuple();
        assertGetterAndSetterRoundTrip(tuple, schema.tupleClass(), "a", Long.TYPE, 100L);
        assertGetterAndSetterRoundTrip(tuple, schema.tupleClass(), "b", Integer.TYPE, 40);
        assertGetterAndSetterRoundTrip(tuple, schema.tupleClass(), "c", Short.TYPE, (short)10);
        assertGetterAndSetterRoundTrip(tuple, schema.tupleClass(), "d", Character.TYPE, 'j');
        assertGetterAndSetterRoundTrip(tuple, schema.tupleClass(), "e", Byte.TYPE, (byte)255);
        assertGetterAndSetterRoundTrip(tuple, schema.tupleClass(), "f", Float.TYPE, 0.125f);
        assertGetterAndSetterRoundTrip(tuple, schema.tupleClass(), "g", Double.TYPE, 0.125);
        assertGetterAndSetterRoundTrip(tuple, schema.tupleClass(), "h", String.class, "Hello, world!");

    }

    @Test
    public void testIndexedSetAndGet() throws Exception {
        TupleSchema schema = TupleSchema.builder().
                addField("a", Long.TYPE).
                addField("b", Integer.TYPE).
                addField("c", Short.TYPE).
                addField("d", Character.TYPE).
                addField("e", Byte.TYPE).
                addField("f", Float.TYPE).
                addField("g", Double.TYPE).
                addField("h", String.class).
                heapMemory().
                build();
        FastTuple tuple = schema.createTuple();
        assertIndexedGetterAndSetterRoundTrip(tuple, 1, 100L);
        assertIndexedGetterAndSetterRoundTrip(tuple, 2, 40);
        assertIndexedGetterAndSetterRoundTrip(tuple, 3, (short)10);
        assertIndexedGetterAndSetterRoundTrip(tuple, 4, 'j');
        assertIndexedGetterAndSetterRoundTrip(tuple, 5, (byte)255);
        assertIndexedGetterAndSetterRoundTrip(tuple, 6, 0.125f);
        assertIndexedGetterAndSetterRoundTrip(tuple, 7, 0.125);
        assertIndexedGetterAndSetterRoundTrip(tuple, 8, "Hello, world!");
    }

    @Test
    public void testIndexedTypedSetAndGet() throws Exception {
        TupleSchema schema = TupleSchema.builder().
                addField("a", Long.TYPE).
                addField("b", Integer.TYPE).
                addField("c", Short.TYPE).
                addField("d", Character.TYPE).
                addField("e", Byte.TYPE).
                addField("f", Float.TYPE).
                addField("g", Double.TYPE).
                addField("h", String.class).
                heapMemory().
                build();
        FastTuple tuple = schema.createTuple();
        assertIndexedTypedGetterAndSetterRoundTrip(tuple, 1, 100L);
        assertIndexedTypedGetterAndSetterRoundTrip(tuple, 2, 40);
        assertIndexedTypedGetterAndSetterRoundTrip(tuple, 3, (short)10);
        assertIndexedTypedGetterAndSetterRoundTrip(tuple, 4, 'j');
        assertIndexedTypedGetterAndSetterRoundTrip(tuple, 5, (byte)255);
        assertIndexedTypedGetterAndSetterRoundTrip(tuple, 6, 0.125f);
        assertIndexedTypedGetterAndSetterRoundTrip(tuple, 7, 0.125);
        assertIndexedTypedGetterAndSetterRoundTrip(tuple, 8, "Hello, world!");
    }

    @Test
    public void testInterfaceIsImplemented() throws Exception {
        TupleSchema schema = TupleSchema.builder().
                addField("a", Long.TYPE).
                implementInterface(StaticBinding.class).
                heapMemory().
                build();
        FastTuple tuple = schema.createTuple();
        assertInstanceOf(StaticBinding.class, tuple);
    }

    @Test
    public void testStringInterfaceIsImplemented() throws Exception {
        TupleSchema schema = TupleSchema.builder().
                addField("a", String.class).
                implementInterface(StringBinding.class).
                heapMemory().
                build();
        FastTuple tuple = schema.createTuple();
        assertInstanceOf(StringBinding.class, tuple);
    }

    public void assertGetterAndSetterGenerated(Class<?> clazz, String name, Class<?> type) throws Exception {
        assertEquals(type, clazz.getDeclaredMethod(name).getReturnType());
        assertNotNull(clazz.getDeclaredMethod(name, type));
    }

    public void assertGetterAndSetterRoundTrip(Object tuple, Class<?> clazz, String name, Class<?> type, Object value) throws Exception {
        clazz.getDeclaredMethod(name, type).invoke(tuple, value);
        assertEquals(value, clazz.getDeclaredMethod(name).invoke(tuple));
    }

    public void assertIndexedGetterAndSetterRoundTrip(FastTuple tuple, int index, Object value) {
        tuple.set(index, value);
        assertEquals(value, tuple.get(index));
    }

    public void assertIndexedTypedGetterAndSetterRoundTrip(FastTuple tuple, int index, Object value) {
        if (value.getClass().equals(Long.class)) {
            tuple.setLong(index, (Long) value);
            assertEquals(value, tuple.getLong(index));
        } else if (value.getClass().equals(Short.class)) {
            tuple.setShort(index, (Short) value);
            assertEquals(value, tuple.getShort(index));
        } else if (value.getClass().equals(Character.class)) {
            tuple.setChar(index, (Character) value);
            assertEquals(value, tuple.getChar(index));
        } else if (value.getClass().equals(Integer.class)) {
            tuple.setInt(index, (Integer) value);
            assertEquals(value, tuple.getInt(index));
        } else if (value.getClass().equals(Byte.class)) {
            tuple.setByte(index, (Byte) value);
            assertEquals(value, tuple.getByte(index));
        } else if (value.getClass().equals(Float.class)) {
            tuple.setFloat(index, (Float) value);
            assertEquals(value, tuple.getFloat(index));
        } else if (value.getClass().equals(Double.class)) {
            tuple.setDouble(index, (Double) value);
            assertEquals(value, tuple.getDouble(index));
        }
    }

    public void assertNullConstructorGenerated(Class<?> clazz) {
        // Check for a no-arg constructor
        try {
            final Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
            assertEquals(0, declaredConstructor.getParameterTypes().length);
        } catch (NoSuchMethodException e) {
            fail("No-arg constructor not found in class: " + clazz.getName());
        }
    }

    @SuppressWarnings("unused")
    public interface StaticBinding {
        void a(long a);
        long a();
    }

    @SuppressWarnings("unused")
    public interface StringBinding {
        void a(String a);
        String a();
    }
}
