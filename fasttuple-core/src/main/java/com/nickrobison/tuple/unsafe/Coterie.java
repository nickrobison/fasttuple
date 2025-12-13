package com.nickrobison.tuple.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

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

    public static String getString(long address) {
        int length = theUnsafe.getInt(address);
        byte[] chars = new byte[length];
        final long dataOffset = address + 4;
        for (int i = 0; i < length; i++) {
            chars[i] = theUnsafe.getByte(dataOffset + i);
        }
        return new String(chars, StandardCharsets.UTF_8);
    }

    public static void putString(long address, String value) {
        // We allocate 24 bytes for string data
        // 4 for the length and 20 for the actual string data
        final byte[] data = value.getBytes(StandardCharsets.UTF_8);
        final int length = Math.min(data.length, 20);
        theUnsafe.putInt(address, length);
        final long dataOffset = address + 4;
        for (int i = 0; i < length; i++) {
            theUnsafe.putByte(dataOffset + i, data[i]);
        }

    }
}
