package com.nickrobison.tuple.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by cliff on 5/2/14.
 */
public class Coterie {
    private static final Unsafe theUnsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            theUnsafe = (Unsafe) field.get(null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Unsafe unsafe() {
        return theUnsafe;
    }

    // --- Raw memory getters ---

    public static long getLong(long address) {
        return theUnsafe.getLong(address);
    }

    public static int getInt(long address) {
        return theUnsafe.getInt(address);
    }

    public static short getShort(long address) {
        return theUnsafe.getShort(address);
    }

    public static char getChar(long address) {
        return theUnsafe.getChar(address);
    }

    public static byte getByte(long address) {
        return theUnsafe.getByte(address);
    }

    public static float getFloat(long address) {
        return theUnsafe.getFloat(address);
    }

    public static double getDouble(long address) {
        return theUnsafe.getDouble(address);
    }

    // --- Raw memory setters ---

    public static void putLong(long address, long value) {
        theUnsafe.putLong(address, value);
    }

    public static void putInt(long address, int value) {
        theUnsafe.putInt(address, value);
    }

    public static void putShort(long address, short value) {
        theUnsafe.putShort(address, value);
    }

    public static void putChar(long address, char value) {
        theUnsafe.putChar(address, value);
    }

    public static void putByte(long address, byte value) {
        theUnsafe.putByte(address, value);
    }

    public static void putFloat(long address, float value) {
        theUnsafe.putFloat(address, value);
    }

    public static void putDouble(long address, double value) {
        theUnsafe.putDouble(address, value);
    }

    // --- Object field accessors ---

    public static long getLong(Object obj, long offset) {
        return theUnsafe.getLong(obj, offset);
    }

    public static void putLong(Object obj, long offset, long value) {
        theUnsafe.putLong(obj, offset, value);
    }

    // --- Memory management ---

    public static long allocateMemory(long bytes) {
        return theUnsafe.allocateMemory(bytes);
    }

    public static void freeMemory(long address) {
        theUnsafe.freeMemory(address);
    }

    // --- Reflection helpers ---

    public static long objectFieldOffset(Field field) {
        return theUnsafe.objectFieldOffset(field);
    }

    // --- Array helpers ---

    public static int arrayBaseOffset(Class<?> arrayClass) {
        return theUnsafe.arrayBaseOffset(arrayClass);
    }

    public static int arrayIndexScale(Class<?> arrayClass) {
        return theUnsafe.arrayIndexScale(arrayClass);
    }

    // --- Atomic operations ---

    public static boolean compareAndSwapObject(Object o, long offset, Object expected, Object update) {
        return theUnsafe.compareAndSwapObject(o, offset, expected, update);
    }

    public static void putOrderedObject(Object o, long offset, Object update) {
        theUnsafe.putOrderedObject(o, offset, update);
    }
}
