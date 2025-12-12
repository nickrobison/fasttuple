package com.nickrobison.tuple.codegen;

import org.codehaus.janino.Java;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeMappingTest {

    @Test
    void testToPrimitive() {
        assertEquals(Java.Primitive.LONG, TypeMapping.toPrimitive(Long.TYPE));
        assertEquals(Java.Primitive.INT, TypeMapping.toPrimitive(Integer.TYPE));
        assertEquals(Java.Primitive.SHORT, TypeMapping.toPrimitive(Short.TYPE));
        assertEquals(Java.Primitive.CHAR, TypeMapping.toPrimitive(Character.TYPE));
        assertEquals(Java.Primitive.BYTE, TypeMapping.toPrimitive(Byte.TYPE));
        assertEquals(Java.Primitive.FLOAT, TypeMapping.toPrimitive(Float.TYPE));
        assertEquals(Java.Primitive.DOUBLE, TypeMapping.toPrimitive(Double.TYPE));
        assertEquals(Java.Primitive.VOID, TypeMapping.toPrimitive(Void.TYPE));
    }

    @Test
    void testToBoxedName() {
        assertEquals("Long", TypeMapping.toBoxedName(Long.TYPE));
        assertEquals("Integer", TypeMapping.toBoxedName(Integer.TYPE));
        assertEquals("Short", TypeMapping.toBoxedName(Short.TYPE));
        assertEquals("Character", TypeMapping.toBoxedName(Character.TYPE));
        assertEquals("Byte", TypeMapping.toBoxedName(Byte.TYPE));
        assertEquals("Float", TypeMapping.toBoxedName(Float.TYPE));
        assertEquals("Double", TypeMapping.toBoxedName(Double.TYPE));
    }

    @Test
    void testToBoxedNameUnsupported() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> TypeMapping.toBoxedName(Void.TYPE));
        assertEquals("Unsupported type: void", ex.getMessage());
    }

    @Test
    void testToAccessorName() {
        assertEquals("Byte", TypeMapping.toAccessorName(Byte.TYPE));
        assertEquals("Char", TypeMapping.toAccessorName(Character.TYPE));
        assertEquals("Short", TypeMapping.toAccessorName(Short.TYPE));
        assertEquals("Int", TypeMapping.toAccessorName(Integer.TYPE));
        assertEquals("Float", TypeMapping.toAccessorName(Float.TYPE));
        assertEquals("Double", TypeMapping.toAccessorName(Double.TYPE));
        assertEquals("Long", TypeMapping.toAccessorName(Long.TYPE));
    }

    @Test
    void testToAccessorNameUnsupported() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> TypeMapping.toAccessorName(Void.TYPE));
        assertEquals("Unsupported type: void", ex.getMessage());
    }
}
